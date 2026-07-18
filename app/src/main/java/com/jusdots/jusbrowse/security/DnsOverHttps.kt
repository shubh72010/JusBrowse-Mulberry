package com.jusdots.jusbrowse.security

import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap

/**
 * DNS over HTTPS (DoH) Client Implementation
 * Uses a common provider (Cloudflare) to maintain low fingerprint entropy.
 * Integrated with OkHttp for seamless network surgery.
 */
class DnsOverHttps(private val client: OkHttpClient) : Dns {
    private val dohUrl = "https://cloudflare-dns.com/dns-query".toHttpUrl()
    private data class CacheEntry(val addresses: List<InetAddress>, val expiry: Long)
    private val cache = ConcurrentHashMap<String, CacheEntry>()

    override fun lookup(hostname: String): List<InetAddress> {
        // 1. Fast cache check
        cache[hostname]?.let { entry ->
            if (System.currentTimeMillis() < entry.expiry) {
                return entry.addresses
            } else {
                cache.remove(hostname)
            }
        }

        // 2. Perform DoH resolution
        try {
            val request = okhttp3.Request.Builder()
                .url(dohUrl.newBuilder().addQueryParameter("name", hostname).addQueryParameter("type", "A").build())
                .header("Accept", "application/dns-json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw UnknownHostException("DoH Failed: ${response.code}")
                
                val body = response.body?.string() ?: throw UnknownHostException("Empty DoH response")
                val json = org.json.JSONObject(body)
                val answer = json.optJSONArray("Answer")
                
                if (answer != null) {
                    var minTtl = 60
                    val results = mutableListOf<InetAddress>()
                    for (i in 0 until answer.length()) {
                        val obj = answer.getJSONObject(i)
                        val type = obj.optInt("type")
                        if (type == 1 || type == 28) { // Type A (IPv4) or AAAA (IPv6)
                            try {
                                val ip = obj.getString("data")
                                results.add(InetAddress.getByName(ip))
                                if (obj.has("TTL")) {
                                    val ttl = obj.getInt("TTL")
                                    if (ttl < minTtl || minTtl == 60) minTtl = ttl
                                }
                            } catch (_: Exception) { /* skip malformed record */ }
                        }
                    }
                    if (results.isNotEmpty()) {
                        val expiry = System.currentTimeMillis() + (minTtl * 1000L)
                        cache[hostname] = CacheEntry(results, expiry)
                        return results
                    }
                }
            }
        } catch (e: Exception) {
            // Refusing system DNS fallback to prevent DNS leaks
            throw java.io.IOException("DoH resolution failed — refusing system DNS fallback")
        }

        throw UnknownHostException("Could not resolve $hostname via DoH or System")
    }
}
