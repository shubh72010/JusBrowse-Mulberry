package com.jusdots.jusbrowse.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.jusdots.jusbrowse.ui.theme.BackgroundPreset
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BackgroundRenderer(
    preset: BackgroundPreset,
    modifier: Modifier = Modifier,
    reduceAnim: Boolean = false,
    forceStatic: Boolean = false
) {
    if (preset == BackgroundPreset.NONE) return

    if (forceStatic || reduceAnim) {
        StaticGradientBackground(colors = preset.colors, modifier = modifier)
        return
    }

    when (preset) {
        BackgroundPreset.NONE -> Unit
        BackgroundPreset.BALATRO -> BalatroBackground(
            colors = listOf(Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF1A1A2E)),
            modifier = modifier
        )
        BackgroundPreset.COLOR_BENDS -> ColorBendsBackground(
            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2), Color(0xFF1A1A2E)),
            modifier = modifier
        )
        BackgroundPreset.DARK_VEIL -> DarkVeilBackground(
            colors = listOf(Color(0xFF0F0F23), Color(0xFF1A1A3E), Color(0xFF2D1B69)),
            modifier = modifier
        )
        BackgroundPreset.DITHER -> DitherBackground(
            colors = listOf(Color(0xFF2D2D2D), Color(0xFF4A4A4A), Color(0xFF6B6B6B)),
            modifier = modifier
        )
        BackgroundPreset.FAULTY_TERMINAL -> FaultyTerminalBackground(
            colors = listOf(Color(0xFF00FF41), Color(0xFF0D0D0D)),
            modifier = modifier
        )
        BackgroundPreset.PIXEL_BLAST -> PixelBlastBackground(
            colors = listOf(Color(0xFFFF006E), Color(0xFF8338EC), Color(0xFF3A86FF)),
            modifier = modifier
        )
    }
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
private fun BalatroBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "balatro")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "balatroRotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        rotate(rotation, Offset(centerX, centerY)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = Offset(centerX, centerY),
                    radius = size.maxDimension
                ),
                center = Offset(centerX, centerY),
                radius = size.maxDimension
            )
        }
    }
}

@Composable
private fun ColorBendsBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "colorbends")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorBendsOffset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val angle = 45f + (offset * 90f)
        val rad = Math.toRadians(angle.toDouble())
        val startX = width * 0.5f + (cos(rad) * width * 0.5f).toFloat()
        val startY = height * 0.5f + (sin(rad) * height * 0.5f).toFloat()
        val endX = width * 0.5f - (cos(rad) * width * 0.5f).toFloat()
        val endY = height * 0.5f - (sin(rad) * height * 0.5f).toFloat()

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            )
        )
    }
}

@Composable
private fun DarkVeilBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = colors)
            )
    )
}

@Composable
private fun DitherBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dither")
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ditherWave"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.horizontalGradient(
                colors = colors,
                startX = width * wave,
                endX = width * (wave + 0.5f)
            )
        )
    }
}

@Composable
private fun FaultyTerminalBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "terminal")
    val scanline by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "terminalScanline"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val height = size.height

        drawRect(color = colors[1])

        drawRect(
            color = colors[0].copy(alpha = 0.3f),
            topLeft = Offset(0f, height * scanline - 20f),
            size = androidx.compose.ui.geometry.Size(size.width, 40f)
        )
    }
}

@Composable
private fun PixelBlastBackground(colors: List<Color>, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pixelblast")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pixelBlastPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = size.maxDimension * pulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = colors,
                center = Offset(centerX, centerY),
                radius = maxRadius
            ),
            center = Offset(centerX, centerY),
            radius = maxRadius
        )
    }
}
