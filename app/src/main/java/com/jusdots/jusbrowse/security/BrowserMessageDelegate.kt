package com.jusdots.jusbrowse.security

import android.util.Log
import org.json.JSONObject
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.WebExtension
import kotlinx.coroutines.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import com.jusdots.jusbrowse.data.repository.PreferencesRepository

/**
 * Modern Messaging Bridge for GeckoView WebExtensions.
 * Replaces legacy JavascriptInterface.
 */
class BrowserMessageDelegate(private val context: android.content.Context) : WebExtension.MessageDelegate {
    private var scope: CoroutineScope? = null

    companion object {
        var activePort: WebExtension.Port? = null
    }

    override fun onConnect(port: WebExtension.Port) {
        activePort = port
        Log.d("BrowserMessageDelegate", "WebExtension Port Connected")

        val portScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        scope = portScope

        // Watch for extraction requests from app
        portScope.launch {
            AirlockDiscoveryBus.extractionRequests.collect { tabId ->
                Log.d("BrowserMessageDelegate", "Airlock Request: Extracting media for tab $tabId")
                val msg = JSONObject().put("type", "extract_media")
                port.postMessage(msg)
            }
        }

        // Watch for Boomer Mode from preferences
        val prefs = PreferencesRepository(context)
        portScope.launch {
            prefs.boomerModeEnabled.collect { enabled ->
                Log.d("BrowserMessageDelegate", "Boomer Mode Toggle Sent: $enabled")
                val msg = JSONObject().put("type", "toggle_boomer").put("enabled", enabled)
                port.postMessage(msg)
            }
        }

        // Watch for Ad Block from preferences
        portScope.launch {
            prefs.adBlockEnabled.collect { enabled ->
                Log.d("BrowserMessageDelegate", "AdBlock Toggle Sent: $enabled")
                val msg = JSONObject().put("type", "set_adblock").put("enabled", enabled)
                port.postMessage(msg)
            }
        }

        port.setDelegate(object : WebExtension.PortDelegate {
            override fun onPortMessage(message: Any, port: WebExtension.Port) {
                // Defensive: GeckoView sometimes returns Map instead of JSONObject
                val json = message as? JSONObject ?: return
                val type = json.optString("type")
                Log.d("BrowserMessageDelegate", "Port Message Received: $type")

                if (type == "media_extracted") {
                    val dataJson = json.optJSONObject("media")
                    if (dataJson != null) {
                        try {
                            val data = Gson().fromJson(dataJson.toString(), com.jusdots.jusbrowse.ui.components.MediaData::class.java)
                            Log.d("BrowserMessageDelegate", "Media Extracted: ${data.images.size} imgs, ${data.videos.size} vids")
                            portScope.launch(Dispatchers.Main) {
                                AirlockDiscoveryBus.reportExtraction(data)
                            }
                        } catch (e: Exception) {
                            Log.e("BrowserMessageDelegate", "Failed to parse MediaData", e)
                        }
                    }
                } else if (type == "report_blocked_tracker") {
                    val domain = json.optString("domain")
                    val url = json.optString("url")
                    val tabId = json.optInt("tabId", -1)
                    if (domain.isNotEmpty() && url.isNotEmpty()) {
                        TrackerShieldBus.reportBlockedTracker(url, domain, tabId)
                    }
                }
            }
            override fun onDisconnect(port: WebExtension.Port) {
                Log.d("BrowserMessageDelegate", "WebExtension Port Disconnected")
                if (activePort == port) activePort = null
                portScope.cancel()
                scope = null
            }
        })
    }

    override fun onMessage(nativeApp: String, message: Any, sender: WebExtension.MessageSender): GeckoResult<Any>? {
        val json = message as? JSONObject ?: return null
        val type = json.optString("type")
        
        Log.d("BrowserMessageDelegate", "Received message: $type")
        
        return when (type) {
            "report_blocked_tracker" -> {
                val domain = json.optString("domain")
                val url = json.optString("url")
                val tabId = json.optInt("tabId", -1)
                if (domain.isNotEmpty() && url.isNotEmpty()) {
                    com.jusdots.jusbrowse.security.TrackerShieldBus.reportBlockedTracker(url, domain, tabId)
                }
                null
            }
            // media_extracted is now handled by the Port, but we leave this for fallback
            "media_extracted" -> {
                val dataJson = json.optJSONObject("media")
                if (dataJson != null) {
                    val data = Gson().fromJson(dataJson.toString(), com.jusdots.jusbrowse.ui.components.MediaData::class.java)
                    scope?.launch(Dispatchers.Main) {
                        com.jusdots.jusbrowse.security.AirlockDiscoveryBus.reportExtraction(data)
                    }
                }
                null
            }
            else -> null
        }
    }
}
