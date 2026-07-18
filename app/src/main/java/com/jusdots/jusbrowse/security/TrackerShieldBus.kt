package com.jusdots.jusbrowse.security

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class BlockedTrackerEvent(
    val url: String,
    val domain: String,
    val tabId: Int = -1
)

/**
 * Event bus to route WebExtension background.js block events to the Compose UI layer.
 */
object TrackerShieldBus {
    private val _blockedTrackers = MutableSharedFlow<BlockedTrackerEvent>(
        replay = 0,
        extraBufferCapacity = 32,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val blockedTrackers = _blockedTrackers.asSharedFlow()

    fun reportBlockedTracker(url: String, domain: String, tabId: Int = -1) {
        _blockedTrackers.tryEmit(BlockedTrackerEvent(url, domain, tabId))
    }
}
