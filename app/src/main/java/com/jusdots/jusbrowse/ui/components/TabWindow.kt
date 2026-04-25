package com.jusdots.jusbrowse.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import com.jusdots.jusbrowse.security.GeckoSessionFactory
import kotlin.math.roundToInt

@SuppressLint("RestrictedApi")
@Composable
fun TabWindow(
    viewModel: BrowserViewModel,
    tab: BrowserTab,
    tabIndex: Int,
    onClose: () -> Unit,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val windowState = viewModel.tabWindowStates[tab.id] ?: return
    
    val isPassive = viewModel.passiveTabIds[tab.id] ?: false
    
    // Get or create GeckoSession conditionally but yielded to prevent UI thread lock
    val sessionState = remember { mutableStateOf<org.mozilla.geckoview.GeckoSession?>(null) }
    
    androidx.compose.runtime.LaunchedEffect(tab.id, isPassive) {
        if (isPassive) {
            sessionState.value = null
        } else {
            var existing = viewModel.getGeckoSession(tab.id)
            if (existing == null) {
                // The viewmodel handles preemptive reaping and yield-delaying
                existing = viewModel.getOrCreateGeckoSession(tab.id, tab.isPrivate, tab.containerId)
                if (tab.url != "about:blank") {
                    existing.loadUri(tab.url)
                }
            }
            sessionState.value = existing
        }
    }
    val session = sessionState.value

    // Gesture State
    var offsetX by remember { mutableFloatStateOf(windowState.x) }
    var offsetY by remember { mutableFloatStateOf(windowState.y) }
    var scale by remember { mutableFloatStateOf(windowState.scale) }

    // Sync back to ViewModel
    LaunchedEffect(offsetX, offsetY, tab.id) {
        viewModel.updateWindowPosition(tab.id, offsetX, offsetY)
    }
    LaunchedEffect(scale, tab.id) {
        viewModel.updateWindowScale(tab.id, scale)
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().zIndex(windowState.zIndex)) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val density = androidx.compose.ui.platform.LocalDensity.current
        val winWidthPx = with(density) { (360.dp * scale).toPx() }
        val winHeightPx = with(density) { (600.dp * scale).toPx() }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(360.dp * scale, 600.dp * scale)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .clickable { onFocus() }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                // Window Title Bar (Drag Handle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxWidthPx - winWidthPx)
                                offsetY = (offsetY + dragAmount.y).coerceIn(0f, maxHeightPx - winHeightPx)
                                onFocus()
                            }
                        }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (tab.isPrivate) {
                        PrivateTabIndicator()
                    } else {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = "Tab",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                        )
                    }
                    
                    Text(
                        text = tab.title.take(20),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                }
                
                IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, "Close Window", modifier = Modifier.size(18.dp))
                }
            }

            // GeckoView Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (tab.url != "about:blank" && !isPassive && session != null) {
                    GeckoWebView(
                        session = session,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (isPassive) {
                    // Passive Mode Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { viewModel.switchTab(tabIndex) }, // Click to wake up
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Pause,
                                contentDescription = "Suspended",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Hibernating to save RAM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("JusBrowse Gecko", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                // Progress Indicator
                if (tab.isLoading) {
                    LinearProgressIndicator(
                        progress = { tab.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.TopCenter),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }
            }
        }
    }

    // Resize Handle
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(24.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val scaleChange = 1f + (dragAmount.x + dragAmount.y) / 500f
                    scale = (scale * scaleChange).coerceIn(0.5f, 3.0f)
                    onFocus()
                }
            }
    )
}
}

@Composable
private fun PrivateTabIndicator() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            "PRIVATE",
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
