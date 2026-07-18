package com.jusdots.jusbrowse.ui.runtime

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class PillSnapState {
    CLOSED,
    EXPANDING,
    EXPANDED,
    MENU_OPEN,
    DISMISSING
}

@Stable
class PillGestureEngine {
    var snapState by mutableStateOf(PillSnapState.CLOSED)
        private set
    var isDragging by mutableStateOf(false)
        private set
    var dragProgress by mutableFloatStateOf(0f)
        private set
    var gestureProgress by mutableFloatStateOf(0f)
        private set

    private var rawOffset = 0f
    private var lastFrameMs = 0L

    private val snapThreshold = 100f
    private val menuThreshold = 80f
    private val throttleIntervalMs = 16L

    fun onDragStart() {
        rawOffset = 0f
        dragProgress = 0f
        isDragging = true
        snapState = PillSnapState.CLOSED
        lastFrameMs = System.currentTimeMillis()
    }

    fun onDrag(deltaY: Float, deltaX: Float) {
        val now = System.currentTimeMillis()
        if (now - lastFrameMs < throttleIntervalMs) return

        rawOffset += deltaY

        val absUp = (-rawOffset).coerceAtLeast(0f)
        dragProgress = (absUp / snapThreshold).coerceAtMost(1f)

        if (snapState == PillSnapState.CLOSED && dragProgress > 0.05f) {
            snapState = PillSnapState.EXPANDING
        }

        lastFrameMs = now
    }

    fun onDragEnd(): Float {
        isDragging = false
        val wasUpSwipe = -rawOffset > snapThreshold
        val wasMenuSwipe = -rawOffset > menuThreshold + snapThreshold

        val target = when {
            wasMenuSwipe -> {
                snapState = PillSnapState.MENU_OPEN
                gestureProgress = 1f
                1f
            }
            wasUpSwipe -> {
                snapState = PillSnapState.EXPANDED
                gestureProgress = 1f
                1f
            }
            else -> {
                snapState = PillSnapState.CLOSED
                gestureProgress = 0f
                0f
            }
        }
        return target
    }

    fun onSnapComplete(progress: Float) {
        gestureProgress = progress
    }

    fun dismissMenu() {
        snapState = PillSnapState.CLOSED
        gestureProgress = 0f
        dragProgress = 0f
        rawOffset = 0f
    }

    fun toggleExpand() {
        if (snapState == PillSnapState.EXPANDED || snapState == PillSnapState.MENU_OPEN) {
            snapState = PillSnapState.CLOSED
            gestureProgress = 0f
        } else {
            snapState = PillSnapState.EXPANDED
            gestureProgress = 1f
        }
    }

    fun forceMenuOpen() {
        snapState = PillSnapState.MENU_OPEN
        gestureProgress = 1f
        dragProgress = 1f
        rawOffset = -(snapThreshold + menuThreshold + 1f)
    }

    val isExpandedOrMenu: Boolean
        get() = snapState == PillSnapState.EXPANDED || snapState == PillSnapState.MENU_OPEN

    val isMenuOpen: Boolean
        get() = snapState == PillSnapState.MENU_OPEN

    fun reset() {
        snapState = PillSnapState.CLOSED
        gestureProgress = 0f
        dragProgress = 0f
        isDragging = false
        rawOffset = 0f
    }
}
