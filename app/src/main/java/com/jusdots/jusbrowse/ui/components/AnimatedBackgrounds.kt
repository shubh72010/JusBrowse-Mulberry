package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.jusdots.jusbrowse.ui.theme.BackgroundPreset

@Composable
fun BackgroundRenderer(
    preset: BackgroundPreset,
    modifier: Modifier = Modifier,
    reduceAnim: Boolean = false,
    forceStatic: Boolean = false
) {
    if (preset == BackgroundPreset.NONE) return

    if (preset.imageFile != null) {
        WallpaperBackground(
            imageFile = preset.imageFile,
            modifier = modifier
        )
        return
    }

    StaticGradientBackground(colors = preset.colors, modifier = modifier)
}

@Composable
private fun StaticGradientBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    if (colors.isEmpty()) return
    val brush = remember(colors) {
        if (colors.size == 1) {
            Brush.verticalGradient(listOf(colors[0], colors[0]))
        } else {
            Brush.verticalGradient(colors)
        }
    }
    Box(modifier = modifier.fillMaxSize().background(brush))
}

@Composable
private fun WallpaperBackground(imageFile: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(imageFile) {
        try {
            val assetManager = context.assets
            assetManager.open("wallpapers/$imageFile").use { stream ->
                bitmap = android.graphics.BitmapFactory.decodeStream(stream)
            }
        } catch (_: Exception) {
            bitmap = null
        }
    }

    if (bitmap != null) {
        val imageBitmap = bitmap!!.asImageBitmap()
        Canvas(modifier = modifier.fillMaxSize()) {
            val imgWidth = imageBitmap.width.toFloat()
            val imgHeight = imageBitmap.height.toFloat()
            val canvasWidth = size.width
            val canvasHeight = size.height
            val scale = maxOf(canvasWidth / imgWidth, canvasHeight / imgHeight)
            val drawWidth = imgWidth * scale
            val drawHeight = imgHeight * scale
            val offsetX = (canvasWidth - drawWidth) / 2f
            val offsetY = (canvasHeight - drawHeight) / 2f
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
                dstSize = IntSize(drawWidth.toInt(), drawHeight.toInt())
            )
        }
    } else {
        Box(modifier = modifier.fillMaxSize().background(Color.Black))
    }
}
