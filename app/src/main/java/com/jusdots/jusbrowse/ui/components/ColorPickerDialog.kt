package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val initialHsv = FloatArray(3).also { android.graphics.Color.colorToHSV(initialColor.toArgb(), it) }
    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }
    var hexText by remember { mutableStateOf(initialColor.toHexString()) }

    val currentColor = remember(hue, saturation, value) {
        Color.hsv(hue, saturation, value)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text("Custom Theme Color", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Preview
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(currentColor)
                )

                // Hex input
                OutlinedTextField(
                    value = hexText,
                    onValueChange = { input ->
                        hexText = input
                        if (input.length == 7 && input.startsWith("#")) {
                            try {
                                val c = Color(android.graphics.Color.parseColor(input))
                                val parsed = FloatArray(3).also { android.graphics.Color.colorToHSV(c.toArgb(), it) }
                                hue = parsed[0]
                                saturation = parsed[1]
                                value = parsed[2]
                            } catch (e: Exception) {
                                Log.e("ColorPickerDialog", "Failed to parse hex color: $input", e)
                            }
                        }
                    },
                    label = { Text("Hex Color") },
                    placeholder = { Text("#FF6B6B") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Hue slider
                Text("Hue", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = currentColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Saturation slider
                Text("Saturation", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = currentColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Value/Brightness slider
                Text("Brightness", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = currentColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick presets row
                Text("Quick Colors", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val presets = listOf(
                        Color(0xFFE53935), Color(0xFFFF6F00), Color(0xFFFDD835),
                        Color(0xFF43A047), Color(0xFF00ACC1), Color(0xFF1E88E5),
                        Color(0xFF8E24AA), Color(0xFFD81B60), Color(0xFF6D4C41)
                    )
                    presets.forEach { presetColor ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(presetColor)
                                .then(
                                    if (presetColor == currentColor) {
                                        Modifier.background(
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                            CircleShape
                                        )
                                    } else Modifier
                                )
                                .then(
                                    if (presetColor == currentColor) {
                                        Modifier.padding(2.dp)
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (presetColor == currentColor) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(presetColor)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onColorSelected(currentColor) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun Color.toHexString(): String {
    val r = (red * 255).roundToInt()
    val g = (green * 255).roundToInt()
    val b = (blue * 255).roundToInt()
    return "#%02X%02X%02X".format(r, g, b)
}
