package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.ui.runtime.CachedDimensions
import com.jusdots.jusbrowse.ui.runtime.PillGestureEngine
import com.jusdots.jusbrowse.ui.runtime.PillSnapState

private const val ALPHA_COLLAPSED = 0.75f
private const val ALPHA_EXPANDED = 1f
private const val SCRIM_THRESHOLD = 0.5f

private val SHAPE_COLLAPSED = RectangleShape
private val SHAPE_EXPANDED = RoundedCornerShape(CachedDimensions.PILL_CORNER_EXPANDED_DP.dp)

@Composable
fun rememberPillGestureEngine(): PillGestureEngine {
    return androidx.compose.runtime.remember { PillGestureEngine() }
}

@Composable
fun SnapPillMenu(
    gestureEngine: PillGestureEngine = rememberPillGestureEngine(),
    onExpandChanged: (Boolean) -> Unit = {},
    onMenuToggle: (Boolean) -> Unit = {},
    collapsedContent: @Composable (alpha: Float) -> Unit = {},
    menuContent: @Composable (alpha: Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val snapState = gestureEngine.snapState
    val isDragging = gestureEngine.isDragging
    val dragProgress = gestureEngine.dragProgress

    val effectiveProgress = if (isDragging) dragProgress else gestureEngine.gestureProgress

    val collapsedHeightDp = CachedDimensions.PILL_COLLAPSED_HEIGHT_DP.dp
    val expandedHeightDp = CachedDimensions.PILL_EXPANDED_HEIGHT_DP.dp
    val pillWidth = CachedDimensions.PILL_MAX_WIDTH_DP.dp

    val collapsedOffsetPx = with(density) {
        (expandedHeightDp - collapsedHeightDp).toPx()
    }
    val bottomPaddingPx = with(density) { 90.dp.toPx() }

    val isVisuallyExpanded = effectiveProgress > 0.5f
            || snapState == PillSnapState.EXPANDED
            || snapState == PillSnapState.MENU_OPEN

    val showScrim = isVisuallyExpanded || (isDragging && effectiveProgress > SCRIM_THRESHOLD)
    val scrimAlpha = if (isVisuallyExpanded) 0.7f else if (isDragging) 0.2f else 0f

    LaunchedEffect(snapState) {
        onExpandChanged(gestureEngine.isExpandedOrMenu)
        onMenuToggle(gestureEngine.isMenuOpen)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showScrim) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = scrimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        gestureEngine.dismissMenu()
                    }
            )
        }

        Box(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .size(width = pillWidth, height = expandedHeightDp)
                .graphicsLayer {
                    translationX = 0f
                    translationY = -(effectiveProgress * collapsedOffsetPx * 0.2f) + bottomPaddingPx
                    transformOrigin = TransformOrigin(0.5f, 1f)

                    if (isDragging) {
                        shape = SHAPE_COLLAPSED
                        clip = false
                        alpha = ALPHA_COLLAPSED
                    } else {
                        shape = if (isVisuallyExpanded) SHAPE_EXPANDED else SHAPE_COLLAPSED
                        clip = isVisuallyExpanded
                        alpha = ALPHA_COLLAPSED + (ALPHA_EXPANDED - ALPHA_COLLAPSED) * effectiveProgress
                    }
                }
                .background(
                    if (isVisuallyExpanded) MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                    else MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                )
                .pointerInput(snapState) {
                    detectDragGestures(
                        onDragStart = { gestureEngine.onDragStart() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            gestureEngine.onDrag(dragAmount.y, dragAmount.x)
                        },
                        onDragEnd = { gestureEngine.onDragEnd() }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (!isDragging) {
                            translationY = -(collapsedOffsetPx * (1f - effectiveProgress))
                        }
                    }
            ) {
                menuContent(if (isDragging) 0f else effectiveProgress)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (!isDragging) {
                                alpha = 1f - effectiveProgress
                            }
                        }
                ) {
                    collapsedContent(if (isDragging) 1f else 1f - effectiveProgress)
                }
            }
        }
    }
}
