package com.jusdots.jusbrowse.security

import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel

class SurgicalBridge(
    private val tabId: String,
    private val viewModel: BrowserViewModel
) {
    fun onInterceptPost(url: String, method: String, headersJson: String, body: String): String {
        if (PostBodySanitizer.shouldBlockPost(url)) {
            viewModel.recordBlockedTracker(tabId, "POST blocked: ${android.net.Uri.parse(url).host}")
            return "__BLOCKED__"
        }

        val cleanedBody = PostBodySanitizer.sanitize(url, body)
        if (cleanedBody != body) {
            viewModel.recordBlockedTracker(tabId, "POST cleansed: ${android.net.Uri.parse(url).host}")
        }

        return cleanedBody
    }

    fun reportSuspicion(score: Int, reason: String) {
        viewModel.recordBlockedTracker(tabId, "Anomaly: $reason ($score pts)")
    }
}
