package com.jusdots.jusbrowse

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jusdots.jusbrowse.data.repository.DownloadRepository
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import com.jusdots.jusbrowse.security.DownloadReceiver
import com.jusdots.jusbrowse.ui.screens.BrowserScreen
import com.jusdots.jusbrowse.ui.theme.JusBrowse2Theme
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime

class MainActivity : ComponentActivity() {
    private lateinit var downloadReceiver: DownloadReceiver
    private lateinit var viewModel: BrowserViewModel

    // Holds the pending GeckoResult for an in-flight WebAuthn/FIDO2 request.
    private var pendingWebAuthnResult: GeckoResult<Intent>? = null

    /**
     * ActivityResult launcher for WebAuthn/FIDO2 passkey flows.
     * GeckoView fires GeckoRuntime.ActivityDelegate.onStartActivityForResult() when a site calls
     * navigator.credentials.create() or navigator.credentials.get(). We launch the system
     * FIDO2 picker here and resolve the GeckoResult with the response.
     */
    private val webAuthnLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val geckoResult = pendingWebAuthnResult ?: return@registerForActivityResult
            pendingWebAuthnResult = null
            if (result.resultCode == RESULT_OK && result.data != null) {
                geckoResult.complete(result.data)
            } else {
                geckoResult.completeExceptionally(
                    IllegalStateException("WebAuthn/FIDO2 prompt was cancelled or returned no data")
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Download Security Receiver
        val database = BrowserApplication.database
        val downloadRepository = DownloadRepository(database.downloadDao())
        val preferencesRepository = PreferencesRepository(application)
        downloadReceiver = DownloadReceiver(downloadRepository, preferencesRepository)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                android.content.Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        enableEdgeToEdge()

        viewModel = androidx.lifecycle.ViewModelProvider(this)[BrowserViewModel::class.java]
        handleIntent(intent)

        // Wire WebAuthn/FIDO2 passkey support.
        // GeckoView requires an ActivityDelegate to bridge passkey requests from the web
        // engine to Android's FIDO2 API. Without this, navigator.credentials.* silently fails.
        BrowserApplication.runtime?.activityDelegate = object : GeckoRuntime.ActivityDelegate {
            override fun onStartActivityForResult(intent: PendingIntent): GeckoResult<Intent> {
                val result = GeckoResult<Intent>()
                pendingWebAuthnResult = result
                try {
                    webAuthnLauncher.launch(
                        IntentSenderRequest.Builder(intent.intentSender).build()
                    )
                } catch (e: Exception) {
                    result.completeExceptionally(e)
                    pendingWebAuthnResult = null
                }
                return result
            }
        }

        setContent {
            val viewModel: BrowserViewModel = viewModel()
            val themePreset by viewModel.themePreset.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val darkMode by viewModel.darkMode.collectAsStateWithLifecycle(initialValue = true)
            val amoledBlackEnabled by viewModel.amoledBlackEnabled.collectAsStateWithLifecycle(initialValue = false)
            val customThemeColorHex by viewModel.customThemeColor.collectAsStateWithLifecycle(initialValue = "")
            val appFont by viewModel.appFont.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val backgroundPreset by viewModel.backgroundPreset.collectAsStateWithLifecycle(initialValue = "NONE")
            val uiVariant by viewModel.uiVariant.collectAsStateWithLifecycle(
                initialValue = com.jusdots.jusbrowse.ui.theme.BrowserUiVariant.DEFAULT.name
            )

            val customThemeColor = remember(customThemeColorHex) {
                if (customThemeColorHex.isNotBlank() && customThemeColorHex.startsWith("#")) {
                    try { Color(android.graphics.Color.parseColor(customThemeColorHex)) } catch (_: Exception) { null }
                } else null
            }

            JusBrowse2Theme(
                darkTheme = darkMode,
                themePreset = themePreset,
                amoledBlackEnabled = amoledBlackEnabled,
                customColor = customThemeColor,
                appFont = appFont,
                backgroundPreset = backgroundPreset,
                uiVariant = uiVariant
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val flagSecureEnabled by viewModel.flagSecureEnabled.collectAsStateWithLifecycle(initialValue = true)

                    LaunchedEffect(flagSecureEnabled) {
                        if (flagSecureEnabled) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                    }

                    BrowserScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::downloadReceiver.isInitialized) {
            downloadReceiver.cleanup()
            unregisterReceiver(downloadReceiver)
        }
        // Clear ActivityDelegate to prevent GeckoRuntime holding a reference to this Activity
        BrowserApplication.runtime?.activityDelegate = null
        pendingWebAuthnResult = null
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: android.content.Intent?) {
        intent?.data?.let { uri ->
            val scheme = uri.scheme?.lowercase()
            if (scheme == "http" || scheme == "https") {
                viewModel.handleIntentURL(uri.toString())
            }
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (::viewModel.isInitialized) {
            viewModel.onTrimMemory(level)
        }
    }
}