package com.jusdots.jusbrowse.ui.runtime

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.jusdots.jusbrowse.ui.theme.BackgroundPreset

@Stable
class OptimizedAnimationState(private val curve: PrecomputedCurve) {
    private var startTimeNanos = 0L
    private var animating = false
    private var _value = 0f
    private var _progress = 0f

    val value: Float get() = _value
    val progress: Float get() = _progress
    val isAnimating: Boolean get() = animating

    fun start(nowNanos: Long) {
        startTimeNanos = nowNanos
        animating = true
    }

    fun update(nowNanos: Long) {
        if (!animating) return
        val elapsed = ((nowNanos - startTimeNanos) / 1_000_000).toInt()
        if (elapsed >= curve.durationMs) {
            _progress = 1f
            _value = curve.keyframes.last()
            animating = false
            return
        }
        _progress = elapsed.toFloat() / curve.durationMs
        _value = curve.valueAt(_progress)
    }

    fun snapToEnd() {
        _progress = 1f
        _value = curve.keyframes.last()
        animating = false
    }

    fun snapToStart() {
        _progress = 0f
        _value = curve.keyframes.first()
        animating = false
    }
}

@Composable
fun rememberOptimizedAnimation(curve: PrecomputedCurve): OptimizedAnimationState {
    return remember(curve) { OptimizedAnimationState(curve) }
}

@Composable
fun OptimizedScaleAnimation(
    targetScale: Float,
    curve: PrecomputedCurve,
    modifier: Modifier = Modifier,
    content: @Composable (scale: Float) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = curve.toComposeSpec(),
        label = "optScale"
    )

    content(scale)
}

@Composable
fun OptimizedFadeTransition(
    visible: Boolean,
    curve: PrecomputedCurve = PrecomputedAnimations.fadeIn(),
    modifier: Modifier = Modifier,
    content: @Composable (alpha: Float) -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = curve.toComposeSpec(),
        label = "optFade"
    )

    content(alpha)
}

@Composable
fun OptimizedSlideIn(
    visible: Boolean,
    curve: PrecomputedCurve = PrecomputedAnimations.easeOutCubic(300, 20),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val slideFraction by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = curve.toComposeSpec(),
        label = "optSlide"
    )
    var contentWidth by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .onSizeChanged { contentWidth = it.width }
            .graphicsLayer { translationX = (1f - slideFraction) * contentWidth }
    ) {
        content()
    }
}

@Composable
fun CachedBackgroundRenderer(
    preset: BackgroundPreset,
    assets: CachedBackgroundAssets,
    modifier: Modifier = Modifier
) {
    if (preset == BackgroundPreset.NONE) return

    val bitmap = remember(preset) {
        assets.renderStaticGradient(
            colors = preset.colors,
            width = 256,
            height = 256,
            direction = when (preset) {
                BackgroundPreset.BALATRO -> GradientDirection.RADIAL
                BackgroundPreset.COLOR_BENDS -> GradientDirection.HORIZONTAL
                BackgroundPreset.DARK_VEIL -> GradientDirection.VERTICAL
                BackgroundPreset.DITHER -> GradientDirection.HORIZONTAL
                BackgroundPreset.FAULTY_TERMINAL -> GradientDirection.VERTICAL
                BackgroundPreset.PIXEL_BLAST -> GradientDirection.RADIAL
                BackgroundPreset.NONE -> GradientDirection.VERTICAL
            }
        )
    }

    val shader = remember(bitmap) {
        android.graphics.BitmapShader(
            bitmap,
            android.graphics.Shader.TileMode.CLAMP,
            android.graphics.Shader.TileMode.CLAMP
        )
    }
    val brush = remember(shader) { ShaderBrush(shader) }

    Box(modifier = modifier.fillMaxSize().background(brush))
}

@Composable
fun <T> rememberStableDerivedState(calculation: () -> T): State<T> {
    return remember { derivedStateOf(calculation) }
}

val STATIC_PILL_ANIMATION_SPEC: AnimationSpec<Float> = tween(durationMillis = 200)
val STATIC_TAB_ANIMATION_SPEC: AnimationSpec<Float> = tween(durationMillis = 180)
val STATIC_FADE_SPEC: AnimationSpec<Float> = tween(durationMillis = 150)
val STATIC_SECURITY_SPEC: AnimationSpec<Float> = tween(durationMillis = 200)

object CachedComposableKeys {
    const val GECKO_WEBVIEW = "geckoWebView"
    const val PILL_BAR = "pillBar"
    const val TAB_STRIP = "tabStrip"
    const val BACKGROUND_LAYER = "backgroundLayer"
    const val SECURITY_ICON = "securityIcon"
    const val ADDRESS_BAR = "addressBar"
    const val BOTTOM_BAR = "bottomBar"
}

object FrozenRegions {
    const val TOOLBAR = "toolbar"
    const val TAB_BAR = "tabBar"
    const val BACKGROUND = "background"
    const val SECURITY_INDICATORS = "securityIndicators"
    const val CONTENT_AREA = "contentArea"
}

fun Dp.cachedKey(): String = "dp_${this.value}"

fun MeasureScope.cachedMeasuredWidth(
    measurable: Measurable,
    constraints: Constraints,
    cache: LayoutMetricsCache,
    key: String
): Int {
    val width = cache.getOrCompute("measured_${key}_w") {
        measurable.measure(constraints).width.toFloat()
    }
    return width.toInt()
}
