package com.jusdots.jusbrowse.ui.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember

@Immutable
data class PrecomputedTransition(
    val name: String,
    val durationMs: Int,
    val curveName: String,
    val fromValue: Float,
    val toValue: Float,
    val keyframes: FloatArray
) {
    fun valueAt(progress: Float): Float {
        if (keyframes.isEmpty()) return fromValue + (toValue - fromValue) * progress
        val idx = (progress * (keyframes.size - 1)).toInt().coerceIn(0, keyframes.size - 2)
        val frac = progress * (keyframes.size - 1) - idx
        val v0 = fromValue + (toValue - fromValue) * keyframes[idx]
        val v1 = fromValue + (toValue - fromValue) * keyframes[idx + 1]
        return v0 + (v1 - v0) * frac
    }

    fun isFinished(progress: Float): Boolean = progress >= 1f

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrecomputedTransition) return false
        return name == other.name
    }

    override fun hashCode(): Int = name.hashCode()
}

object DeterministicTransitions {

    val TAB_SWITCH_FADE = PrecomputedTransition(
        name = "tabSwitchFade",
        durationMs = 150,
        curveName = "fadeIn",
        fromValue = 0f,
        toValue = 1f,
        keyframes = floatArrayOf(0f, 0.3f, 0.7f, 0.9f, 1.0f)
    )

    val TAB_SWITCH_SLIDE_IN = PrecomputedTransition(
        name = "tabSwitchSlideIn",
        durationMs = 180,
        curveName = "easeOutCubic",
        fromValue = 30f,
        toValue = 0f,
        keyframes = floatArrayOf(1.0f, 0.7f, 0.35f, 0.1f, 0.0f)
    )

    val TAB_SWITCH_SLIDE_OUT = PrecomputedTransition(
        name = "tabSwitchSlideOut",
        durationMs = 150,
        curveName = "easeOutCubic",
        fromValue = 0f,
        toValue = -20f,
        keyframes = floatArrayOf(0f, 0.1f, 0.35f, 0.7f, 1.0f)
    )

    val PILL_EXPAND = PrecomputedTransition(
        name = "pillExpand",
        durationMs = 200,
        curveName = "easeOutCubic",
        fromValue = 260f,
        toValue = 360f,
        keyframes = floatArrayOf(0f, 0.1f, 0.4f, 0.75f, 1.0f)
    )

    val PILL_COLLAPSE = PrecomputedTransition(
        name = "pillCollapse",
        durationMs = 180,
        curveName = "easeOutCubic",
        fromValue = 360f,
        toValue = 260f,
        keyframes = floatArrayOf(0f, 0.15f, 0.5f, 0.85f, 1.0f)
    )

    val BOTTOM_BAR_HIDE = PrecomputedTransition(
        name = "bottomBarHide",
        durationMs = 200,
        curveName = "easeOutCubic",
        fromValue = 0f,
        toValue = 200f,
        keyframes = floatArrayOf(0f, 0.1f, 0.3f, 0.6f, 1.0f)
    )

    val BOTTOM_BAR_REVEAL = PrecomputedTransition(
        name = "bottomBarReveal",
        durationMs = 200,
        curveName = "easeOutCubic",
        fromValue = 200f,
        toValue = 0f,
        keyframes = floatArrayOf(0f, 0.1f, 0.35f, 0.7f, 1.0f)
    )

    val SECURITY_STATE_MORPH = PrecomputedTransition(
        name = "securityMorph",
        durationMs = 200,
        curveName = "easeInOutQuad",
        fromValue = 0.75f,
        toValue = 1.0f,
        keyframes = floatArrayOf(0f, 0.25f, 0.65f, 0.9f, 1.0f)
    )

    val TAB_CHIP_ACTIVATE = PrecomputedTransition(
        name = "tabChipActivate",
        durationMs = 200,
        curveName = "springSnap",
        fromValue = 0.92f,
        toValue = 1.0f,
        keyframes = floatArrayOf(0f, 0.15f, 0.5f, 0.85f, 1.0f)
    )

    val TRACKER_BADGE_APPEAR = PrecomputedTransition(
        name = "trackerBadge",
        durationMs = 300,
        curveName = "bounce",
        fromValue = 0f,
        toValue = 1f,
        keyframes = floatArrayOf(0f, 0f, 0.3f, 0.7f, 0.9f, 1.0f, 1.0f)
    )

    val MENU_OPEN = PrecomputedTransition(
        name = "menuOpen",
        durationMs = 220,
        curveName = "easeOutCubic",
        fromValue = 0.92f,
        toValue = 1.0f,
        keyframes = floatArrayOf(0f, 0.1f, 0.35f, 0.7f, 1.0f)
    )

    val MENU_CLOSE = PrecomputedTransition(
        name = "menuClose",
        durationMs = 150,
        curveName = "easeOutCubic",
        fromValue = 1.0f,
        toValue = 0.92f,
        keyframes = floatArrayOf(0f, 0.2f, 0.5f, 0.85f, 1.0f)
    )

    val TRANSITIONS_BY_NAME = mapOf(
        "tabSwitchFade" to TAB_SWITCH_FADE,
        "tabSwitchSlideIn" to TAB_SWITCH_SLIDE_IN,
        "tabSwitchSlideOut" to TAB_SWITCH_SLIDE_OUT,
        "pillExpand" to PILL_EXPAND,
        "pillCollapse" to PILL_COLLAPSE,
        "bottomBarHide" to BOTTOM_BAR_HIDE,
        "bottomBarReveal" to BOTTOM_BAR_REVEAL,
        "securityMorph" to SECURITY_STATE_MORPH,
        "tabChipActivate" to TAB_CHIP_ACTIVATE,
        "trackerBadge" to TRACKER_BADGE_APPEAR,
        "menuOpen" to MENU_OPEN,
        "menuClose" to MENU_CLOSE
    )
}

@Composable
fun rememberTransition(name: String): PrecomputedTransition {
    return remember(name) {
        DeterministicTransitions.TRANSITIONS_BY_NAME[name]
            ?: DeterministicTransitions.TAB_SWITCH_FADE
    }
}
