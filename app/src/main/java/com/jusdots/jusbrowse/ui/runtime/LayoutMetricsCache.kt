package com.jusdots.jusbrowse.ui.runtime

import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class TextMetricsKey(
    val text: String,
    val styleHash: Int,
    val maxWidth: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextMetricsKey) return false
        return text == other.text && styleHash == other.styleHash && maxWidth == other.maxWidth
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + styleHash
        result = 31 * result + maxWidth.hashCode()
        return result
    }
}

class ShapeMetricsKey(
    val widthPx: Float,
    val heightPx: Float,
    val cornerRadiusPx: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShapeMetricsKey) return false
        return widthPx == other.widthPx && heightPx == other.heightPx && cornerRadiusPx == other.cornerRadiusPx
    }

    override fun hashCode(): Int {
        var result = widthPx.hashCode()
        result = 31 * result + heightPx.hashCode()
        result = 31 * result + cornerRadiusPx.hashCode()
        return result
    }
}

class LayoutMetricsCache(private val maxSize: Int = 128) {

    private val textCache = LruCache<TextMetricsKey, Size>(maxSize)
    private val shapeCache = LruCache<ShapeMetricsKey, Shape>(maxSize / 2)
    private val dimensionCache = object : LruCache<String, Float>(maxSize) {}
    private val dpCache = object : LruCache<String, Dp>(maxSize) {}

    fun getTextSize(key: TextMetricsKey, calculator: () -> Size): Size {
        return textCache.get(key) ?: run {
            val size = calculator()
            textCache.put(key, size)
            size
        }
    }

    fun getOrCompute(key: String, calculator: () -> Float): Float {
        return dimensionCache.get(key) ?: run {
            val value = calculator()
            dimensionCache.put(key, value)
            value
        }
    }

    fun getDp(key: String, calculator: () -> Dp): Dp {
        return dpCache.get(key) ?: run {
            val value = calculator()
            dpCache.put(key, value)
            value
        }
    }

    fun clear() {
        textCache.evictAll()
        shapeCache.evictAll()
        dimensionCache.evictAll()
        dpCache.evictAll()
    }
}

object CachedDimensions {
    const val TOOLBAR_ICON_SIZE_DP = 40f
    const val PILL_MIN_WIDTH_DP = 260f
    const val PILL_MAX_WIDTH_DP = 360f
    const val PILL_COLLAPSED_HEIGHT_DP = 56f
    const val PILL_EXPANDED_HEIGHT_DP = 580f
    const val PILL_CORNER_COLLAPSED_DP = 28f
    const val PILL_CORNER_EXPANDED_DP = 32f
    const val TAB_CHIP_HEIGHT_DP = 36f
    const val TAB_BAR_PADDING_H_DP = 10f
    const val TAB_BAR_PADDING_V_DP = 2f
    const val TAB_CHIP_PADDING_H_DP = 12f
    const val TAB_CHIP_MAX_WIDTH_DP = 110f
    const val BOTTOM_BAR_HEIGHT_DP = 200f
    const val SECURITY_ICON_SIZE_DP = 26f
    const val WINDOW_TITLE_BAR_HEIGHT_DP = 48f
    const val WINDOW_MIN_WIDTH_DP = 360f
    const val WINDOW_MIN_HEIGHT_DP = 600f
    const val WINDOW_CORNER_DP = 28f
    const val WINDOW_SHADOW_DP = 16f
    const val SWIPE_TRIGGER_THRESHOLD_PX = 150f
    const val DEFAULT_ANIMATION_FRAMES = 16
}

fun cachedPillWidth(
    cache: LayoutMetricsCache,
    isExpanded: Boolean,
    showMenu: Boolean
): Dp {
    return cache.getDp("pillWidth_${isExpanded}_$showMenu") {
        when {
            showMenu -> CachedDimensions.PILL_MAX_WIDTH_DP.dp
            isExpanded -> CachedDimensions.PILL_MAX_WIDTH_DP.dp
            else -> CachedDimensions.PILL_MIN_WIDTH_DP.dp
        }
    }
}

fun cachedPillHeight(
    cache: LayoutMetricsCache,
    showMenu: Boolean
): Dp {
    return cache.getDp("pillHeight_$showMenu") {
        if (showMenu) CachedDimensions.PILL_EXPANDED_HEIGHT_DP.dp
        else CachedDimensions.PILL_COLLAPSED_HEIGHT_DP.dp
    }
}

fun cachedPillCorner(
    cache: LayoutMetricsCache,
    showMenu: Boolean
): Dp {
    return cache.getDp("pillCorner_$showMenu") {
        if (showMenu) CachedDimensions.PILL_CORNER_EXPANDED_DP.dp
        else CachedDimensions.PILL_CORNER_COLLAPSED_DP.dp
    }
}

fun cachedTabChipWidth(
    cache: LayoutMetricsCache,
    title: String,
    isActive: Boolean,
    density: Float
): Float {
    return cache.getOrCompute("tabChip_${title.length}_${isActive}") {
        val baseWidth = title.length * 7f * density / 3f
        val padding = CachedDimensions.TAB_CHIP_PADDING_H_DP * density
        val closeButtonWidth = 18f * density
        baseWidth + padding * 2 + closeButtonWidth + 24f * density
    }
}

@Composable
fun rememberLayoutMetricsCache(): LayoutMetricsCache {
    return remember { LayoutMetricsCache() }
}
