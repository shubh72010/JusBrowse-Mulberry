package com.jusdots.jusbrowse.security

import okhttp3.Call
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * The Network Surgeon: Provides OkHttpClient with privacy features.
 * Legacy WebView interception gutted for GeckoView migration.
 */
object NetworkSurgeon {
    private var dohClient: OkHttpClient? = null

    private fun getClient(): Call.Factory {
        if (dohClient == null) {
            val bootstrapClient = OkHttpClient.Builder().build()
            dohClient = OkHttpClient.Builder()
                .followRedirects(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cookieJar(GhostCookieJar)
                .dns(DnsOverHttps(bootstrapClient))
                .build()
        }
        return dohClient!!
    }

    /**
     * Shared client for internal components.
     */
    fun getSharedClient(): Call.Factory = getClient()
}
