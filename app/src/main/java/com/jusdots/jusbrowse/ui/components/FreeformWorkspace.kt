package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel

@Composable
fun FreeformWorkspace(
    viewModel: BrowserViewModel,
    tabs: List<BrowserTab>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // Desktop Wallpaper Gradient
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C3E50), // Dark Blue
                        Color(0xFF4CA1AF)  // Teal
                    )
                )
            )
    ) {
        // Desktop Icons Layer (Z-Index 0)
        val shortcuts = viewModel.pinnedShortcuts
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(shortcuts) { shortcut ->
                DesktopIcon(
                    shortcut = shortcut,
                    onClick = {
                        // Open pinned site in new window
                        viewModel.createNewTab(url = shortcut.url)
                    }
                )
            }
        }

        // Window Layer (Z-Index 1+)
        tabs.forEachIndexed { index, tab ->
            key(tab.id) {
                TabWindow(
                    viewModel = viewModel,
                    tab = tab,
                    tabIndex = index,
                    onClose = { viewModel.closeTab(index) },
                    onFocus = { viewModel.switchTab(index) }
                )
            }
        }
    }
}
