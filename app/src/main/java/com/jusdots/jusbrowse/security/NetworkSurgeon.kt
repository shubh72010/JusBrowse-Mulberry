package com.jusdots.jusbrowse.security

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * The Network Surgeon: Provides OkHttpClient with privacy features.
 * Uses system CA validation — certificate pinning requires real, rotation-proof pins.
 * Fake/placeholder pins (previous implementation) caused guaranteed SSLPeerUnverifiedException.
 */
object NetworkSurgeon {
    @Volatile
    private var sharedClient: OkHttpClient? = null

    private fun buildClient(): OkHttpClient {
        // Bootstrap client without DoH (used only by DnsOverHttps to avoid circular dependency)
        val bootstrapClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return OkHttpClient.Builder()
            .followRedirects(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .cookieJar(GhostCookieJar)
            .dns(DnsOverHttps(bootstrapClient))
            // System CA validation — correct for HTTPS + trusted CA infrastructure.
            // Do NOT add a CertificatePinner here unless you have real, rotation-safe SPKI hashes.
            .build()
    }

    /**
     * Shared OkHttpClient for internal components (DoH, API scanning, CNAME uncloaking).
     * Lazy-initialized, thread-safe singleton.
     */
    fun getSharedClient(): OkHttpClient {
        return sharedClient ?: synchronized(this) {
            sharedClient ?: buildClient().also { sharedClient = it }
        }
    }
}
