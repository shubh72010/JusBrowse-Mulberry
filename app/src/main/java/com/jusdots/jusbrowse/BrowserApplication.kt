package com.jusdots.jusbrowse

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.room.Room
import com.jusdots.jusbrowse.data.database.BrowserDatabase
import com.jusdots.jusbrowse.security.BrowserMessageDelegate
import com.jusdots.jusbrowse.security.ExtensionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.WebExtension
import java.lang.ref.WeakReference

data class PendingExtensionInstall(
    val extensionName: String,
    val extensionVersion: String,
    val permissions: List<String>,
    val origins: List<String>,
    val result: GeckoResult<WebExtension.PermissionPromptResponse>
)

class BrowserApplication : Application() {

    companion object {
        @Volatile
        private var instance: BrowserApplication? = null

        @Volatile
        var runtime: GeckoRuntime? = null
            private set

        val pendingExtensionInstall: MutableState<PendingExtensionInstall?> = mutableStateOf(null)

        @Volatile
        var extensionManager: ExtensionManager? = null

        private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        @Volatile
        private var activityRef: WeakReference<Activity>? = null

        fun setCurrentActivity(activity: Activity?) {
            activityRef = if (activity != null) WeakReference(activity) else null
        }

        fun getCurrentActivity(): Activity? {
            return activityRef?.get()
        }

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
            ).addMigrations(BrowserDatabase.MIGRATION_7_8, BrowserDatabase.MIGRATION_8_9)
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
            val extMan = ExtensionManager(database.extensionDao())
            extensionManager = extMan

            // Prompt user for extension install permissions
            extController?.setPromptDelegate(object :
                org.mozilla.geckoview.WebExtensionController.PromptDelegate {
                override fun onInstallPromptRequest(
                    extension: WebExtension,
                    permissions: Array<String>,
                    origins: Array<String>,
                    installer: Array<String>
                ): GeckoResult<WebExtension.PermissionPromptResponse>? {
                    val meta = extension.metaData
                    val name = meta.name ?: extension.id
                    val version = meta.version
                    Log.d("BrowserApp", "Install requested: $name v$version")

                    val result = GeckoResult<WebExtension.PermissionPromptResponse>()
                    pendingExtensionInstall.value = PendingExtensionInstall(
                        extensionName = name,
                        extensionVersion = version,
                        permissions = permissions.toList(),
                        origins = origins.toList(),
                        result = result
                    )
                    return result
                }
            })

            extController?.ensureBuiltIn(
                "resource://android/assets/extensions/jusbrowse-privacy/",
                "jusbrowse-privacy@jusdots.com"
            )?.accept({ extension ->
                if (extension != null) {
                    Log.d("BrowserApp", "WebExtension installed: ${extension.metaData.name}")
                    val delegate = BrowserMessageDelegate(this)
                    extension.setMessageDelegate(delegate, "jusbrowse")
                }
                // Third-party extensions must not receive the built-in extension's
                // privileged native-message namespace.
                appScope.launch {
                    extMan.reinstallPersistedExtensions()
                }
            }, { exception ->
                Log.e("BrowserApp", "Failed to register WebExtension", exception)
                // Still try to re-register even if built-in fails
                appScope.launch {
                    extMan.reinstallPersistedExtensions()
                }
            })
        }
    }
}
