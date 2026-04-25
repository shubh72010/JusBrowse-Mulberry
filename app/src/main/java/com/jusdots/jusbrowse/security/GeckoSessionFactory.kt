package com.jusdots.jusbrowse.security

import com.jusdots.jusbrowse.BrowserApplication
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings

/**
 * Factory for creating and initializing GeckoSessions.
 */
object GeckoSessionFactory {
    
    @Volatile
    var follianModeActive: Boolean = false

    fun createSession(
        isPrivate: Boolean = false,
        containerId: String? = null
    ): GeckoSession {
        val settingsBuilder = GeckoSessionSettings.Builder()
            .usePrivateMode(isPrivate)
            .useTrackingProtection(true)
            .userAgentMode(GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
            .suspendMediaWhenInactive(true)
            .allowJavascript(!follianModeActive) // Follian disables JS
            // Android mobile Tor-style UA: keeps appVersion/oscpu/platform consistent at engine level.
            // Desktop UA caused a contradiction (appVersion="Android 16", UA="Windows NT 10.0").
            .userAgentOverride("Mozilla/5.0 (Android 10; Mobile; rv:140.0) Gecko/140.0 Firefox/140.0")

        // Follian additional restrictions
        if (follianModeActive) {
            // These can be fine-tuned; allowJavascript covers the main case
        }

        // Container isolation via contextual identity
        if (containerId != null) {
            settingsBuilder.contextId(containerId)
        }

        val session = org.mozilla.geckoview.GeckoSession(settingsBuilder.build())
        
        // Setup Native Feature Protection (EasyPrivacy equivalent)
        session.settings.useTrackingProtection = true
        
        // Open the session with the global runtime
        val runtime = BrowserApplication.runtime 
            ?: throw IllegalStateException("GeckoRuntime not initialized")
        
        session.open(runtime)
        return session
    }
}
