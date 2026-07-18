package com.jusdots.jusbrowse.security

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

object DnsResolver {
    private val cnameCache = ConcurrentHashMap<String, String>()

    /**
     * Resolves the CNAME for a given host using Google DNS-over-HTTPS.
     * Returns the target domain if a CNAME exists, or null otherwise.
     */
    suspend fun resolveCname(host: String, customDohUrl: String? = null): String? = withContext(Dispatchers.IO) {
        // Return from cache if available
        cnameCache[host]?.let { if (it == "NONE") return@withContext null else return@withContext it }

        try {
            val urlStr = if (!customDohUrl.isNullOrBlank()) {
                val urlObj = try { URL(customDohUrl) } catch (e: Exception) { null }
                if (urlObj == null || urlObj.protocol != "https") {
                    "https://cloudflare-dns.com/dns-query?name=$host&type=CNAME"
                } else {
                    val base = customDohUrl.substringBefore("#")
                    val sep = if (urlObj.query != null) "&" else "?"
                    "$base${sep}name=$host&type=CNAME"
                }
            } else {
                "https://cloudflare-dns.com/dns-query?name=$host&type=CNAME"
            }
            
            val request = okhttp3.Request.Builder()
                .url(urlStr)
                .header("Accept", "application/json")
                .build()

            val response = NetworkSurgeon.getSharedClient().newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val json = JSONObject(responseBody)
                    val answer = json.optJSONArray("Answer")
                    if (answer != null && answer.length() > 0) {
                        val cname = answer.getJSONObject(0).optString("data")?.trimEnd('.')
                        if (!cname.isNullOrEmpty()) {
                            cnameCache[host] = cname
                            return@withContext cname
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Silently fail, we don't want to break browsing
        }

        // Cache negative result to avoid repeated lookups
        cnameCache[host] = "NONE"
        null
    }
}
