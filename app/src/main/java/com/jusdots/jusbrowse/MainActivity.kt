package com.jusdots.jusbrowse

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    companion object {
        private const val TAG = "MainActivity"
        private const val WEB_AUTHN_REQUEST_CODE = 0x4A75
    }

    private lateinit var downloadReceiver: DownloadReceiver
    private lateinit var viewModel: BrowserViewModel

    // Holds the pending GeckoResult for an in-flight WebAuthn/FIDO2 request via
    // GeckoRuntime.ActivityDelegate. GeckoView handles the WebAuthn protocol internally
    // and delegates to Android Credential Manager through this PendingIntent bridge.
    private var pendingWebAuthnResult: GeckoResult<Intent>? = null

    // Bridges GeckoView's native WebAuthn PendingIntent result back to the engine.
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WEB_AUTHN_REQUEST_CODE) {
            val geckoResult = pendingWebAuthnResult
            pendingWebAuthnResult = null
            if (geckoResult == null) {
                Log.w(TAG, "WebAuthn result with no pending request (requestCode=$WEB_AUTHN_REQUEST_CODE)")
                return
            }
            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "WebAuthn result OK")
                geckoResult.complete(data)
            } else {
                Log.w(TAG, "WebAuthn result cancelled or no data: resultCode=$resultCode")
                geckoResult.completeExceptionally(
                    IllegalStateException("WebAuthn/FIDO2 prompt was cancelled or returned no data")
                )
            }
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

        BrowserApplication.setCurrentActivity(this)

        // GeckoView native WebAuthn/passkey bridge.
        // GeckoView handles the FIDO2/WebAuthn protocol internally and produces a
        // PendingIntent that starts Android Credential Manager. The result is returned
        // via onActivityResult and fed back to the engine to complete the WebAuthn flow.
        BrowserApplication.runtime?.activityDelegate = object : GeckoRuntime.ActivityDelegate {
            override fun onStartActivityForResult(pendingIntent: PendingIntent): GeckoResult<Intent> {
                val result = GeckoResult<Intent>()
                pendingWebAuthnResult = result
                try {
                    Log.d(TAG, "Launching WebAuthn PendingIntent: $pendingIntent")
                    @Suppress("DEPRECATION")
                    startIntentSenderForResult(
                        pendingIntent.intentSender,
                        WEB_AUTHN_REQUEST_CODE,
                        null,
                        0, 0, 0
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to launch WebAuthn PendingIntent", e)
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
                      val hideStatusBar by viewModel.hideStatusBar.collectAsStateWithLifecycle(initialValue = false)

                      LaunchedEffect(flagSecureEnabled) {
                          if (flagSecureEnabled) {
                              window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                          } else {
                              window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                          }
                      }

                      val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                      LaunchedEffect(isLandscape, hideStatusBar) {
                          val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                          if (isLandscape || hideStatusBar) {
                              insetsController.hide(WindowInsets.Type.statusBars())
                          } else {
                              insetsController.show(WindowInsets.Type.statusBars())
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
        BrowserApplication.setCurrentActivity(null)
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
