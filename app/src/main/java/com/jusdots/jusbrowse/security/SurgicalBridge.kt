package com.jusdots.jusbrowse.security

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel

/**
 * Native bridge for intercepting POST requests and other payloads 
 * that are not visible to shouldInterceptRequest.
 */
class SurgicalBridge(
    private val tabId: String,
    private val viewModel: BrowserViewModel
) {

    @JavascriptInterface
    fun onInterceptPost(url: String, method: String, headersJson: String, body: String): String {
        // Phase 1: Block known tracker endpoints entirely
        if (PostBodySanitizer.shouldBlockPost(url)) {
            viewModel.recordBlockedTracker(tabId, "POST blocked: ${android.net.Uri.parse(url).host}")
            return "__BLOCKED__"
        }

        // Phase 1: Sanitize tracking fields from form data
        val cleanedBody = PostBodySanitizer.sanitize(url, body)
        if (cleanedBody != body) {
            viewModel.recordBlockedTracker(tabId, "POST cleansed: ${android.net.Uri.parse(url).host}")
        }

        return cleanedBody
    }

    @JavascriptInterface
    fun reportSuspicion(score: Int, reason: String) {
        viewModel.recordBlockedTracker(tabId, "Anomaly: $reason ($score pts)")
        SuspicionScorer.reportSuspiciousActivity(score)
    }
}

