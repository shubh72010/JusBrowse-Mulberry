package com.jusdots.jusbrowse.ui.runtime

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

data class Keyframe(val timeMs: Int, val value: Float)

class PrecomputedCurve(
    val keyframes: FloatArray,
    val durationMs: Int,
    val frameCount: Int
) {
    fun valueAt(progress: Float): Float {
        if (progress <= 0f) return keyframes[0]
        if (progress >= 1f) return keyframes[frameCount - 1]
        val index = (progress * (frameCount - 1)).toInt().coerceIn(0, frameCount - 2)
        val frac = (progress * (frameCount - 1)) - index
        return keyframes[index] + (keyframes[index + 1] - keyframes[index]) * frac
    }

    fun valueAtMs(timeMs: Int): Float {
        return valueAt(timeMs.toFloat() / durationMs)
    }
}

object PrecomputedAnimations {

    fun easeOutCubic(durationMs: Int = 200, frames: Int = 16): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            val t = i.toFloat() / (frames - 1)
            values[i] = 1f - (1f - t).pow(3)
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun easeInOutQuad(durationMs: Int = 200, frames: Int = 16): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            val t = i.toFloat() / (frames - 1)
            values[i] = if (t < 0.5f) 2f * t * t else 1f - (-2f * t + 2f).pow(2) / 2f
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun overshoot(durationMs: Int = 300, frames: Int = 24, magnitude: Float = 0.15f): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            val t = i.toFloat() / (frames - 1)
            val decay = 1f - t
            values[i] = 1f + magnitude * decay * sin(t * kotlin.math.PI.toFloat()) - decay * decay * decay
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun bounce(durationMs: Int = 400, frames: Int = 32): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            val t = i.toFloat() / (frames - 1)
            values[i] = when {
                t < 0.5f -> t * t * 2f
                t < 0.75f -> 1f - (1f - (t - 0.25f) * 2f).pow(2) * 2f
                else -> 1f - (1f - (t - 0.5f) * 2f).pow(2) * 2f * 0.5f
            }
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun linear(durationMs: Int = 200, frames: Int = 16): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            values[i] = i.toFloat() / (frames - 1)
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun springSnap(stiffness: Float = 0.4f, damping: Float = 0.7f, durationMs: Int = 300, frames: Int = 24): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            val t = i.toFloat() / (frames - 1)
            val decay = kotlin.math.exp(-damping * t * 5f)
            val oscillation = cos(stiffness * t * 10f)
            values[i] = 1f - decay * (1f - oscillation * 0.1f)
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun scalePulse(): PrecomputedCurve {
        val frames = 6
        val values = floatArrayOf(1.0f, 1.03f, 1.05f, 1.02f, 1.005f, 1.0f)
        return PrecomputedCurve(values, 80, frames)
    }

    fun fadeIn(durationMs: Int = 150, frames: Int = 8): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            values[i] = i.toFloat() / (frames - 1)
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun fadeOut(durationMs: Int = 100, frames: Int = 8): PrecomputedCurve {
        val values = FloatArray(frames)
        for (i in 0 until frames) {
            values[i] = 1f - (i.toFloat() / (frames - 1))
        }
        return PrecomputedCurve(values, durationMs, frames)
    }

    fun tabSwitchSlide(durationMs: Int = 180, frames: Int = 12): PrecomputedCurve {
        return easeOutCubic(durationMs, frames)
    }

    val DEFAULT_PRESETS = mapOf(
        "pillExpand" to easeOutCubic(200, 16),
        "pillCollapse" to easeOutCubic(180, 14),
        "pillSwipe" to easeOutCubic(250, 20),
        "tabSelect" to springSnap(0.5f, 0.8f, 200, 16),
        "tabHover" to scalePulse(),
        "fadeIn" to fadeIn(120, 8),
        "fadeOut" to fadeOut(80, 6),
        "securityMorph" to easeInOutQuad(200, 12),
        "bottomBarHide" to easeOutCubic(200, 16),
        "bottomBarReveal" to easeOutCubic(200, 16),
        "menuOpen" to easeOutCubic(220, 16),
        "menuClose" to easeOutCubic(150, 12)
    )
}

object PrecomputedScaleKeyframes {
    val TAB_SELECT: FloatArray = floatArrayOf(0.92f, 0.96f, 0.99f, 1.0f, 1.0f)
    val TAB_DESELECT: FloatArray = floatArrayOf(1.0f, 0.98f, 0.95f, 0.92f, 0.92f)
    val PILL_ACTIVATE: FloatArray = floatArrayOf(1.0f, 1.04f, 1.06f, 1.04f, 1.01f, 1.0f)
    val BUTTON_PRESS: FloatArray = floatArrayOf(1.0f, 0.92f, 0.88f, 0.92f, 1.0f)
}

class AnimationPlaybackEngine(private val curve: PrecomputedCurve) {
    private var startTime = 0L
    private var reversed = false
    private var running = false

    fun start(nowMs: Long, reverse: Boolean = false) {
        startTime = nowMs
        reversed = reverse
        running = true
    }

    fun stop() {
        running = false
    }

    fun isFinished(nowMs: Long): Boolean {
        return running && (nowMs - startTime) >= curve.durationMs
    }

    fun currentValue(nowMs: Long): Float {
        if (!running) return if (reversed) 0f else 1f
        val elapsed = (nowMs - startTime).toInt().coerceIn(0, curve.durationMs)
        val progress = elapsed.toFloat() / curve.durationMs
        val value = curve.valueAt(if (reversed) 1f - progress else progress)
        return value
    }

    fun currentProgress(nowMs: Long): Float {
        if (!running) return if (reversed) 0f else 1f
        val elapsed = (nowMs - startTime).toInt().coerceIn(0, curve.durationMs)
        return if (reversed) 1f - (elapsed.toFloat() / curve.durationMs)
        else elapsed.toFloat() / curve.durationMs
    }
}

fun PrecomputedCurve.toComposeSpec(): AnimationSpec<Float> {
    return tween(durationMillis = durationMs)
}

@Composable
fun rememberPrecomputedCurve(name: String): PrecomputedCurve {
    return remember(name) {
        PrecomputedAnimations.DEFAULT_PRESETS[name] ?: PrecomputedAnimations.easeOutCubic()
    }
}

@Composable
fun rememberPrecomputedCurve(curve: PrecomputedCurve): PrecomputedCurve {
    return remember(curve) { curve }
}
