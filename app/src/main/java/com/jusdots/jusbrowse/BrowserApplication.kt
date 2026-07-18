package com.jusdots.jusbrowse

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.jusdots.jusbrowse.data.database.BrowserDatabase
import com.jusdots.jusbrowse.security.BrowserMessageDelegate
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.WebExtension
import org.mozilla.geckoview.WebExtensionController

class BrowserApplication : Application() {

    companion object {
        @Volatile
        private var instance: BrowserApplication? = null

        @Volatile
        var runtime: GeckoRuntime? = null
            private set

        fun getInstance(): BrowserApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }

        val database: BrowserDatabase by lazy {
            val app = getInstance()
            val context = app.applicationContext
            val dbName = "browser_database"

            Room.databaseBuilder(
                context,
                BrowserDatabase::class.java,
                dbName
            ).addMigrations(BrowserDatabase.MIGRATION_7_8)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val currentProcessName = if (android.os.Build.VERSION.SDK_INT >= 28) {
            Application.getProcessName()
        } else {
            val am = getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.runningAppProcesses?.find { it.pid == android.os.Process.myPid() }?.processName ?: ""
        }

        if (currentProcessName == packageName) {
            val contentBlocking = ContentBlocking.Settings.Builder()
                .cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS)
                .enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.STRICT)
                .safeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT)
                .cookiePurging(true)
                .build()

            val settings = GeckoRuntimeSettings.Builder()
                .contentBlocking(contentBlocking)
                .globalPrivacyControlEnabled(true)
                .allowInsecureConnections(GeckoRuntimeSettings.HTTPS_ONLY)
                .trustedRecursiveResolverMode(GeckoRuntimeSettings.TRR_MODE_FIRST)
                .trustedRecursiveResolverUri("https://cloudflare-dns.com/dns-query")
                .enterpriseRootsEnabled(true)
                .setLnaEnabled(true)
                .setLnaBlocking(true)
                .build()
                .setFingerprintingProtection(true)
                .setFingerprintingProtectionPrivateBrowsing(true)
                .setCookieBehaviorOptInPartitioning(true)

            runtime = GeckoRuntime.create(this, settings)

            val extController = runtime?.webExtensionController

            // Auto-allow extension installation permissions
            extController?.setPromptDelegate(object :
                org.mozilla.geckoview.WebExtensionController.PromptDelegate {
                override fun onInstallPromptRequest(
                    extension: WebExtension,
                    permissions: Array<String>,
                    origins: Array<String>,
                    installer: Array<String>
                ): GeckoResult<WebExtension.PermissionPromptResponse>? {
                    Log.d("BrowserApp", "Auto-allowing extension: ${extension.metaData?.name}")
                    return GeckoResult.fromValue(WebExtension.PermissionPromptResponse(true, true, true))
                }
            })

            extController?.ensureBuiltIn(
                "resource://android/assets/extensions/jusbrowse-privacy/",
                "jusbrowse-privacy@jusdots.com"
            )?.accept({ extension ->
                if (extension != null) {
                    Log.d("BrowserApp", "WebExtension installed: ${extension.metaData?.name}")
                    val delegate = BrowserMessageDelegate(this)
                    extension.setMessageDelegate(delegate, "jusbrowse")
                }
            }, { exception ->
                Log.e("BrowserApp", "Failed to register WebExtension", exception)
            })
        }
    }
}
