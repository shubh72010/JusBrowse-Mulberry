package com.jusdots.jusbrowse.security

import com.jusdots.jusbrowse.ui.components.MediaData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AirlockDiscoveryBus {
    private val _discoveryEvents = MutableSharedFlow<MediaData>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val discoveryEvents = _discoveryEvents.asSharedFlow()

    private val _extractionRequests = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ) // tabId
    val extractionRequests = _extractionRequests.asSharedFlow()

    suspend fun reportExtraction(data: MediaData) {
        _discoveryEvents.emit(data)
    }

    suspend fun requestExtraction(tabId: String) {
        _extractionRequests.emit(tabId)
    }
}
