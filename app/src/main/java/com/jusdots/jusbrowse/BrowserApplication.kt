package com.jusdots.jusbrowse

import android.app.Application
import androidx.room.Room
import com.jusdots.jusbrowse.data.database.BrowserDatabase
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import org.json.JSONObject
import kotlinx.coroutines.*
import org.mozilla.geckoview.WebExtension

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
            val personaId = com.jusdots.jusbrowse.security.FakeModeManager.getSavedPersonaId(context)
            val dbName = if (personaId != null) "browser_database_$personaId" else "browser_database"
            
            Room.databaseBuilder(
                context,
                BrowserDatabase::class.java,
                dbName
            ).fallbackToDestructiveMigration().build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Only initialize GeckoRuntime and WebExtensions in the main process to prevent ANRs in child processes
        val currentProcessName = if (android.os.Build.VERSION.SDK_INT >= 28) {
            Application.getProcessName()
        } else {
            val am = getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.runningAppProcesses?.find { it.pid == android.os.Process.myPid() }?.processName ?: ""
        }
        
        if (currentProcessName == packageName) {
            
            val deviceMode = com.jusdots.jusbrowse.security.MemorySurgeon.getDeviceMode(this)
            
            val prefs = com.jusdots.jusbrowse.data.repository.PreferencesRepository(this)
            
            // Base privacy settings
            val baseArgs = mutableListOf(
                "--pref", "toolkit.telemetry.enabled=false",
                "--pref", "datareporting.policy.dataSubmissionEnabled=false",
                "--pref", "intl.accept_languages=en-US, en",
                "--pref", "privacy.trackingprotection.enabled=true",
                "--pref", "privacy.trackingprotection.pbmode.enabled=true",
                // Safe Browsing is disabled to prevent all visited URLs from being sent to Google.
                // Trade-off: Users lose protection against known malware/phishing domains.
                "--pref", "browser.safebrowsing.malware.enabled=false",
                "--pref", "browser.safebrowsing.phishing.enabled=false",
                "--pref", "dom.security.https_only_mode=true",
                "--pref", "network.prefetch-next=false",
                "--pref", "network.dns.disablePrefetch=true",
                "--pref", "network.trr.mode=3",
                "--pref", "network.trr.uri=https://cloudflare-dns.com/dns-query",
                "--pref", "privacy.use_utc_timezone=true",
                "--pref", "device.sensors.enabled=false",
                "--pref", "dom.enable_performance=false",
                "--pref", "dom.w3c_pointer_events.enabled=false",
                // Tor Browser reports "Mozilla"/"Mozilla" for WebGL — match that to avoid leaking real GPU identity
                "--pref", "webgl.override-unmasked-vendor=Mozilla",
                "--pref", "webgl.override-unmasked-renderer=Mozilla",
                // Force en-US locale for Intl APIs and JS Date formatting.
                // GeckoView's RFP does NOT normalize locale on Android; system locale (e.g. en-IN)
                // leaks through toLocaleString, Intl.DateTimeFormat, and navigator.language.
                // These two prefs are belt-and-suspenders alongside the JS injection in content.js.
                "--pref", "intl.locale.requested=en-US",
                // Forces Date.prototype.toLocaleString and Intl APIs to use en-US regardless of system locale
                "--pref", "javascript.use_us_english_locale=true",
                // WebRTC IP leak protection (Engine Layer)
                "--pref", "media.peerconnection.ice.no_host=true",
                "--pref", "media.peerconnection.ice.default_address_only=true",
                // Global Privacy Control (GPC) signal
                "--pref", "privacy.globalprivacycontrol.enabled=true",
                "--pref", "privacy.globalprivacycontrol.functionality.enabled=true",
                
                // ═══════════════════════════════════════════════════════
                // TRUE PER-SITE ISOLATION BOUNDARIES (State Partitioning)
                // ═══════════════════════════════════════════════════════
                // 1. Total Cookie Protection (isolates cookies per top-level site)
                "--pref", "network.cookie.cookieBehavior=5",
                // 2. Isolate 3rd-party non-cookie storage (localStorage, IndexedDB, Cache API)
                "--pref", "privacy.partition.always_partition_third_party_non_cookie_storage=true",
                // 3. Isolate workers (Service Workers, Shared Workers)
                "--pref", "privacy.partition.service_workers=true",
                // 4. Isolate network state (HTTP cache, TLS sessions, connection pooling)
                "--pref", "privacy.partition.network_state=true"
            )



            // RFP + letterboxing are active in ALL modes so screen, platform, locale, and
            // navigator properties are bucketed at engine level — the same mechanism Tor Browser uses.
            baseArgs.addAll(listOf(
                "--pref", "privacy.resistFingerprinting=true",
                "--pref", "privacy.resistFingerprinting.letterboxing=true"
            ))

            if (deviceMode == com.jusdots.jusbrowse.security.MemorySurgeon.DeviceMode.LOW_SPEC) {
                // Strict memory caches for Low Spec
                baseArgs.addAll(listOf(
                    "--pref", "browser.cache.memory.capacity=10240", // Limit RAM cache to 10MB
                    "--pref", "browser.sessionhistory.max_entries=10", // Keep less history in memory
                    "--pref", "javascript.options.mem.max=100", // Soft cap JS heap
                    "--pref", "image.mem.max_decoded_image_kb=20480" // 20MB decoded image cap
                ))
            } else {
                // Standard/Aggressive caching for High Spec
                baseArgs.addAll(listOf(
                    "--pref", "browser.cache.memory.capacity=51200", // 50MB RAM cache
                    "--pref", "browser.sessionhistory.max_entries=50",
                    "--pref", "network.http.max-connections=100" // More concurrent net conns
                ))
            }

            // Initialize GeckoRuntime with strict privacy settings
            val settings = GeckoRuntimeSettings.Builder()
                .aboutConfigEnabled(true)
                .arguments(baseArgs.toTypedArray())
                .build()

            settings.setBaselineFingerprintingProtection(true)
            settings.setBaselineFingerprintingProtectionOverrides("+JSDateTimeUTC,+CanvasRandomization")

            runtime = GeckoRuntime.create(this, settings)

            // Register privacy WebExtension
            setupWebExtensions(runtime!!)

            // Asynchronously check if Follian is on and signal GeckoSessionFactory.
            // This avoids blocking the main thread during Application.onCreate.
            MainScope().launch {
                val isFollianActive = prefs.follianMode.first()
                if (isFollianActive) {
                    com.jusdots.jusbrowse.security.GeckoSessionFactory.follianModeActive = true
                }
            }
        }
    }

    private fun setupWebExtensions(runtime: org.mozilla.geckoview.GeckoRuntime) {
        val extensionId = "jusbrowse-privacy@jusdots.com"
        val extensionPath = "resource://android/assets/extensions/jusbrowse-privacy/"
        
        runtime.webExtensionController.ensureBuiltIn(extensionPath, extensionId)
            .accept({ extension ->
                Log.d("BrowserApplication", "WebExtension registered: ${extension?.id}")
                // Register message delegate for this extension
                extension?.setMessageDelegate(com.jusdots.jusbrowse.security.BrowserMessageDelegate(this@BrowserApplication), "jusbrowse")
            }, { throwable ->
                Log.e("BrowserApplication", "Failed to register WebExtension", throwable)
            })
    }
}
