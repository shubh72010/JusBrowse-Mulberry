package com.jusdots.jusbrowse.ui.runtime

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jusdots.jusbrowse.ui.theme.BackgroundPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CachedBackgroundAssets(private val maxByteSize: Int = 8 * 1024 * 1024) {

    private val bitmapCache = object : LruCache<String, Bitmap>(maxByteSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.allocationByteCount
        }
    }

    private val shaderCache = object : LruCache<String, Shader>(64) {}

    suspend fun getOrRenderBackground(
        preset: BackgroundPreset,
        width: Int,
        height: Int,
        renderer: suspend (Int, Int) -> Bitmap
    ): Bitmap {
        val key = "${preset.name}_${width}x${height}"
        return bitmapCache.get(key) ?: withContext(Dispatchers.Default) {
            val bitmap = renderer(width, height)
            bitmapCache.put(key, bitmap)
            bitmap
        }
    }

    fun getShader(key: String, factory: () -> Shader): Shader {
        return shaderCache.get(key) ?: run {
            val shader = factory()
            shaderCache.put(key, shader)
            shader
        }
    }

    fun renderStaticGradient(
        colors: List<Color>,
        width: Int,
        height: Int,
        direction: GradientDirection = GradientDirection.VERTICAL
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (direction) {
            GradientDirection.VERTICAL -> {
                val argbColors = colors.map { it.toArgb() }.toIntArray()
                val positions = if (colors.size == 2) null
                else FloatArray(colors.size) { it.toFloat() / (colors.size - 1) }
                val gradient = android.graphics.LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    argbColors, positions, Shader.TileMode.CLAMP
                )
                paint.shader = gradient
            }
            GradientDirection.HORIZONTAL -> {
                val argbColors = colors.map { it.toArgb() }.toIntArray()
                val gradient = android.graphics.LinearGradient(
                    0f, 0f, width.toFloat(), 0f,
                    argbColors, null, Shader.TileMode.CLAMP
                )
                paint.shader = gradient
            }
            GradientDirection.RADIAL -> {
                val argbColors = colors.map { it.toArgb() }.toIntArray()
                val cx = width / 2f
                val cy = height / 2f
                val radius = kotlin.math.sqrt((cx * cx + cy * cy).toDouble()).toFloat()
                val gradient = RadialGradient(
                    cx, cy, radius,
                    argbColors, null, Shader.TileMode.CLAMP
                )
                paint.shader = gradient
            }
        }

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun clear() {
        bitmapCache.evictAll()
        shaderCache.evictAll()
    }
}

enum class GradientDirection { VERTICAL, HORIZONTAL, RADIAL }

fun BackgroundPreset.toStaticBitmap(width: Int, height: Int, cache: CachedBackgroundAssets): Bitmap {
    return when (this) {
        BackgroundPreset.NONE -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        BackgroundPreset.DARK_VEIL -> cache.renderStaticGradient(colors, width, height, GradientDirection.VERTICAL)
        BackgroundPreset.COLOR_BENDS -> cache.renderStaticGradient(colors, width, height, GradientDirection.HORIZONTAL)
        BackgroundPreset.BALATRO -> cache.renderStaticGradient(colors, width, height, GradientDirection.RADIAL)
        BackgroundPreset.DITHER -> cache.renderStaticGradient(colors, width, height, GradientDirection.HORIZONTAL)
        BackgroundPreset.FAULTY_TERMINAL -> cache.renderStaticGradient(colors, width, height, GradientDirection.VERTICAL)
        BackgroundPreset.PIXEL_BLAST -> cache.renderStaticGradient(colors, width, height, GradientDirection.RADIAL)
    }
}

@Composable
fun rememberCachedBackgroundAssets(): CachedBackgroundAssets {
    return remember { CachedBackgroundAssets() }
}

@Composable
fun DisposableCachedBackgroundAssets(cache: CachedBackgroundAssets) {
    DisposableEffect(Unit) {
        onDispose { cache.clear() }
    }
}
