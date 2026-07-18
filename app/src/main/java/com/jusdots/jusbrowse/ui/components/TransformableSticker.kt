package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import coil.compose.AsyncImage
import com.jusdots.jusbrowse.data.models.Sticker
import kotlin.math.*

@Composable
fun TransformableSticker(
    sticker: Sticker,
    isSelected: Boolean,
    onTransform: (x: Float, y: Float, width: Float, height: Float, rotation: Float) -> Unit,
    onClick: () -> Unit,
    screenWidth: Float,
    screenHeight: Float,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // Internal state for smooth gestures
    var currentX by remember(sticker.id) { mutableFloatStateOf(sticker.x) }
    var currentY by remember(sticker.id) { mutableFloatStateOf(sticker.y) }
    var currentWidth by remember(sticker.id) { mutableFloatStateOf(sticker.widthDp) }
    var currentHeight by remember(sticker.id) { mutableFloatStateOf(sticker.heightDp) }
    var currentRotation by remember(sticker.id) { mutableFloatStateOf(sticker.rotation) }

    Box(
        modifier = modifier
            .offset(
                x = (currentX * screenWidth).dp - (currentWidth / 2).dp,
                y = (currentY * screenHeight).dp - (currentHeight / 2).dp
            )
            .size(currentWidth.dp, currentHeight.dp)
            .pointerInput(sticker.id) {
                detectDragGestures(
                    onDragEnd = {
                        onTransform(currentX, currentY, currentWidth, currentHeight, currentRotation)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    // Convert pixel drag to normalized coordinate shift
                    currentX += (dragAmount.x / density.density) / screenWidth
                    currentY += (dragAmount.y / density.density) / screenHeight
                }
            }
            .clickable { onClick() }
    ) {
        // 1. The Rotated & Clipped Sticker Frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20)) // 👈 Adaptive rounding (20%)
                .rotate(currentRotation)
        ) {
            AsyncImage(
                model = sticker.imageUri,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Interaction Layer (Stays axis-aligned, not rotated or clipped)
        if (isSelected) {
            Handle(
                Alignment.TopStart,
                onDrag = { delta ->
                    val diff = (delta.x + delta.y) / 2f / density.density
                    currentWidth = (currentWidth - diff).coerceAtLeast(100f)
                    currentHeight = currentWidth
                },
                onDragEnd = {
                    onTransform(currentX, currentY, currentWidth, currentHeight, currentRotation)
                }
            )
            Handle(
                Alignment.TopEnd,
                onDrag = { delta ->
                    val diff = (delta.x - delta.y) / 2f / density.density
                    currentWidth = (currentWidth + diff).coerceAtLeast(100f)
                    currentHeight = currentWidth
                },
                onDragEnd = {
                    onTransform(currentX, currentY, currentWidth, currentHeight, currentRotation)
                }
            )
            Handle(
                Alignment.BottomStart,
                onDrag = { delta ->
                    val diff = (-delta.x + delta.y) / 2f / density.density
                    currentWidth = (currentWidth + diff).coerceAtLeast(100f)
                    currentHeight = currentWidth
                },
                onDragEnd = {
                    onTransform(currentX, currentY, currentWidth, currentHeight, currentRotation)
                }
            )
            Handle(
                Alignment.BottomEnd,
                onDrag = { delta ->
                    val diff = (delta.x + delta.y) / 2f / density.density
                    currentWidth = (currentWidth + diff).coerceAtLeast(100f)
                    currentHeight = currentWidth
                },
                onDragEnd = {
                    onTransform(currentX, currentY, currentWidth, currentHeight, currentRotation)
                }
            )

            // Rotation Handle (Top Center of static bounding box)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-32).dp)
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .pointerInput(sticker.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            currentRotation += dragAmount.x / 2f
                        }
                    }
            )

            // Material You Toolbar (Bottom Center)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                tonalElevation = 6.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            val side = (currentWidth + currentHeight) / 2
                            currentWidth = side
                            currentHeight = side
                            onTransform(currentX, currentY, currentWidth, currentHeight, currentRotation)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(JusBrowseIcons.CropSquare, null, modifier = Modifier.size(18.dp))
                    }
                    
                    FilledTonalIconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(JusBrowseIcons.Delete, "Delete", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun BoxScope.Handle(
    alignment: Alignment,
    onDrag: (androidx.compose.ui.geometry.Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .size(28.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = onDragEnd
                ) { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
    )
}
