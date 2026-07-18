package com.jusdots.jusbrowse.security

import com.jusdots.jusbrowse.BrowserApplication
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.StorageController

object PrivateBrowsingManager {

    fun createPrivateSession(): GeckoSession {
        val settings = GeckoSessionSettings.Builder()
            .usePrivateMode(true)
            .build()
        val session = GeckoSession(settings)
        val runtime = BrowserApplication.runtime
        if (runtime != null) {
            session.open(runtime)
        }
        return session
    }

    fun cleanupPrivateSession(session: GeckoSession) {
        session.close()
    }

    fun clearAllBrowsingData() {
        BrowserApplication.runtime?.storageController?.clearData(
            StorageController.ClearFlags.ALL
        )
    }

    fun clearCookies() {
        BrowserApplication.runtime?.storageController?.clearData(
            StorageController.ClearFlags.COOKIES
        )
    }

    fun clearSessionCookies() {
        BrowserApplication.runtime?.storageController?.clearData(
            StorageController.ClearFlags.COOKIES
        )
    }
}
