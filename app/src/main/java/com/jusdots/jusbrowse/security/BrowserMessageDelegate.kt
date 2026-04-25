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

    companion object {
        var activePort: WebExtension.Port? = null
    }

    override fun onConnect(port: WebExtension.Port) {
        activePort = port
        Log.d("BrowserMessageDelegate", "WebExtension Port Connected")

        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        
        // Watch for extraction requests from app
        scope.launch {
            AirlockDiscoveryBus.extractionRequests.collect { tabId ->
                Log.d("BrowserMessageDelegate", "Airlock Request: Extracting media for tab $tabId")
                val msg = JSONObject().put("type", "extract_media")
                port.postMessage(msg)
            }
        }

        // Watch for Boomer Mode from preferences
        val prefs = PreferencesRepository(context)
        scope.launch {
            prefs.boomerModeEnabled.collect { enabled ->
                Log.d("BrowserMessageDelegate", "Boomer Mode Toggle Sent: $enabled")
                val msg = JSONObject().put("type", "toggle_boomer").put("enabled", enabled)
                port.postMessage(msg)
            }
        }

        // Watch for Ad Block from preferences
        scope.launch {
            prefs.adBlockEnabled.collect { enabled ->
                Log.d("BrowserMessageDelegate", "AdBlock Toggle Sent: $enabled")
                val msg = JSONObject().put("type", "set_adblock").put("enabled", enabled)
                port.postMessage(msg)
            }
        }

        // Watch for Advanced Ad Block from preferences
        scope.launch {
            prefs.advancedAdBlockEnabled.collect { enabled ->
                Log.d("BrowserMessageDelegate", "Advanced AdBlock Toggle Sent: $enabled")
                val msg = JSONObject().put("type", "set_advanced_adblock").put("enabled", enabled)
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
                            GlobalScope.launch(Dispatchers.Main) {
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
                scope.cancel()
            }
        })
    }

    override fun onMessage(nativeApp: String, message: Any, sender: WebExtension.MessageSender): GeckoResult<Any>? {
        val json = message as? JSONObject ?: return null
        val type = json.optString("type")
        
        Log.d("BrowserMessageDelegate", "Received message: $type")
        
        return when (type) {
            "get_persona" -> {
                val prefs = com.jusdots.jusbrowse.data.repository.PreferencesRepository(context)
                val isFollian = kotlinx.coroutines.runBlocking { prefs.follianMode.first() }
                
                val response = JSONObject()
                if (isFollian) {
                    // Follian Mode: Bypass all JS spoofing. Let native Gecko handle it.
                    response.put("enabled", false)
                } else {
                    val persona = FakeModeManager.currentPersona.value
                    if (persona != null) {
                        response.put("enabled", true)
                        response.put("userAgent", persona.userAgent)
                        response.put("platform", persona.platform)
                        response.put("vendor", persona.videoCardVendor)
                        response.put("renderer", persona.videoCardRenderer)
                        response.put("hardwareConcurrency", 4) // Standardized
                        response.put("deviceMemory", 4) // Standardized
                    } else {
                        response.put("enabled", false)
                    }
                }
                GeckoResult.fromValue(response)
            }
            "report_suspicion" -> {
                val pts = json.optInt("points", 1)
                SuspicionScorer.reportSuspiciousActivity(pts)
                null
            }
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
                    GlobalScope.launch(Dispatchers.Main) {
                        com.jusdots.jusbrowse.security.AirlockDiscoveryBus.reportExtraction(data)
                    }
                }
                null
            }
            else -> null
        }
    }
}
