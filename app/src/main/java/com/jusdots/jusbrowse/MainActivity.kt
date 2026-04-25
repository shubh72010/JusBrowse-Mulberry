package com.jusdots.jusbrowse

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jusdots.jusbrowse.ui.screens.BrowserScreen
import com.jusdots.jusbrowse.ui.theme.JusBrowse2Theme
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import com.jusdots.jusbrowse.security.DownloadReceiver
import com.jusdots.jusbrowse.data.repository.DownloadRepository
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import com.jusdots.jusbrowse.utils.AnalyticsManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.IntentFilter
import android.app.DownloadManager

class MainActivity : ComponentActivity() {
    private lateinit var downloadReceiver: DownloadReceiver
    private lateinit var viewModel: BrowserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure FakeModeManager state is loaded
        com.jusdots.jusbrowse.security.FakeModeManager.init(this)
        
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
        
        setContent {
            val viewModel: BrowserViewModel = viewModel()
            val themePreset by viewModel.themePreset.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val darkMode by viewModel.darkMode.collectAsStateWithLifecycle(initialValue = true)
            val amoledBlackEnabled by viewModel.amoledBlackEnabled.collectAsStateWithLifecycle(initialValue = false)
            val wallColor by viewModel.extractedWallColor.collectAsStateWithLifecycle()
            val appFont by viewModel.appFont.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val backgroundPreset by viewModel.backgroundPreset.collectAsStateWithLifecycle(initialValue = "NONE")

            JusBrowse2Theme(
                darkTheme = darkMode,
                themePreset = themePreset,
                amoledBlackEnabled = amoledBlackEnabled,
                wallColor = wallColor,
                appFont = appFont,
                backgroundPreset = backgroundPreset
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

        lifecycleScope.launch {
            AnalyticsManager.trackAppOpen(preferencesRepository)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::downloadReceiver.isInitialized) {
            unregisterReceiver(downloadReceiver)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: android.content.Intent?) {
        intent?.data?.let { uri ->
            // Only accept http/https schemes to prevent javascript: and file:// injection
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