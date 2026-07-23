package com.jusdots.jusbrowse.security

import com.jusdots.jusbrowse.BrowserApplication
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings

object GeckoSessionFactory {

    fun createSession(
        isPrivate: Boolean = false,
        containerId: String? = null,
        javascriptEnabled: Boolean = true
    ): GeckoSession {
        val settings = GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .useTrackingProtection(true)
            .contextId(containerId)
            .allowJavascript(javascriptEnabled)
            .build()

        val session = GeckoSession(settings)

        val runtime = BrowserApplication.runtime
            ?: throw IllegalStateException("GeckoRuntime not initialized")

        session.open(runtime)
        return session
    }
}
