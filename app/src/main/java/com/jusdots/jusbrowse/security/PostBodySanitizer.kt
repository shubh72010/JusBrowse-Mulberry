package com.jusdots.jusbrowse.security

/**
 * Sanitizes POST request bodies by stripping known tracking/analytics fields.
 * Used by SurgicalBridge to cleanse intercepted POST payloads.
 */
object PostBodySanitizer {

    // Known tracker POST field patterns (Google Analytics, Facebook Pixel, etc.)
    private val TRACKING_FIELDS = setOf(
        "cid", "tid", "uid", "_ga", "_gid", "_gat",
        "fbp", "fbc", "fbclid",
        "client_id", "visitor_id", "device_id", "session_id",
        "screen_resolution", "viewport_size", "user_agent",
        "sr", "sd", "ul", "de",   // GA shorthand fields
        "dl", "dp", "dt", "dr",   // GA page fields
        "ec", "ea", "el", "ev",   // GA event fields
        "_fv", "_ss", "_nsi"      // GA4 session fields
    )

    // Known tracker endpoints — block entire POST to these
    private val TRACKER_ENDPOINTS = setOf(
        "google-analytics.com/collect",
        "google-analytics.com/g/collect",
        "analytics.google.com",
        "facebook.com/tr",
        "connect.facebook.net",
        "bat.bing.com",
        "analytics.tiktok.com",
        "snap.licdn.com",
        "t.co/i/adsct"
    )

    /**
     * Returns true if the entire POST should be blocked (tracker endpoint).
     */
    fun shouldBlockPost(url: String): Boolean {
        val noScheme = url.substringAfter("://")
        val hostPath = noScheme.substringBefore("?")
        return TRACKER_ENDPOINTS.any { hostPath.contains(it) }
    }

    /**
     * Sanitize a POST body by stripping known tracking fields.
     * Handles URL-encoded form data. JSON bodies are passed through
     * (JSON sanitization is complex and risks breaking legitimate payloads).
     */
    fun sanitize(url: String, body: String): String {
        // For URL-encoded form data (key=value&key2=value2)
        if (body.contains("=") && !body.trimStart().startsWith("{") && !body.trimStart().startsWith("[")) {
            val cleaned = body.split("&").filter { param ->
                val key = param.substringBefore("=").lowercase().trim()
                TRACKING_FIELDS.none { tracker -> key == tracker || key.endsWith("_$tracker") }
            }.joinToString("&")
            return cleaned
        }
        // JSON and other formats: pass through (conservative approach)
        return body
    }
}
