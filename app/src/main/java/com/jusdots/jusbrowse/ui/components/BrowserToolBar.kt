package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.layout.*
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserToolBar(
    viewModel: BrowserViewModel,
    currentTab: BrowserTab?,
    onOpenAirlockGallery: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        actions = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Navigation controls ──────────────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = { /* Back handled by WebView */ },
                        enabled = currentTab?.canGoBack == true,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            JusBrowseIcons.ArrowBack, "Back",
                            tint = if (currentTab?.canGoBack == true)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    IconButton(
                        onClick = { /* Forward handled by WebView */ },
                        enabled = currentTab?.canGoForward == true,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            JusBrowseIcons.ArrowForward, "Forward",
                            tint = if (currentTab?.canGoForward == true)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }


                    IconButton(
                        onClick = { viewModel.toggleMultiViewMode() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(JusBrowseIcons.GridView, "Multi-View")
                    }

                    IconButton(
                        onClick = { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.BROWSER) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(JusBrowseIcons.Home, "Home")
                    }


                }

                // ── Menu ─────────────────────────────────────────────────────
                Box {
                    FilledTonalIconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(38.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(JusBrowseIcons.MoreVert, "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("History") },
                            onClick = {
                                viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.HISTORY)
                                showMenu = false
                            },
                            leadingIcon = { Icon(JusBrowseIcons.DateRange, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Downloads") },
                            onClick = {
                                viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.DOWNLOADS)
                                showMenu = false
                            },
                            leadingIcon = { Icon(JusBrowseIcons.Download, null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("New private tab") },
                            onClick = {
                                viewModel.createNewTab(isPrivate = true)
                                showMenu = false
                            },
                            leadingIcon = { Icon(JusBrowseIcons.VpnKey, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Close all tabs") },
                            onClick = {
                                viewModel.closeAllTabs()
                                showMenu = false
                            },
                            leadingIcon = { Icon(JusBrowseIcons.Close, null) }
                        )
                        HorizontalDivider()
                        var showContainerSubMenu by remember { mutableStateOf(false) }
                        DropdownMenuItem(
                            text = { Text("New container tab") },
                            onClick = { showContainerSubMenu = !showContainerSubMenu },
                            leadingIcon = { Icon(JusBrowseIcons.Layers, null) },
                            trailingIcon = {
                                Icon(
                                    if (showContainerSubMenu) JusBrowseIcons.ArrowDropUp else JusBrowseIcons.ArrowDropDown,
                                    null
                                )
                            }
                        )
                        if (showContainerSubMenu) {
                            com.jusdots.jusbrowse.security.ContainerManager.AVAILABLE_CONTAINERS
                                .filter { it != "default" }
                                .forEach { container ->
                                    DropdownMenuItem(
                                        text = { Text(com.jusdots.jusbrowse.security.ContainerManager.getContainerName(container)) },
                                        onClick = {
                                            viewModel.createNewTab(containerId = container)
                                            showMenu = false
                                            showContainerSubMenu = false
                                        },
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.SETTINGS)
                                showMenu = false
                            },
                            leadingIcon = { Icon(JusBrowseIcons.Settings, null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Airlock Gallery") },
                            leadingIcon = { Icon(JusBrowseIcons.Security, contentDescription = null) },
                            onClick = {
                                onOpenAirlockGallery()
                                showMenu = false
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}
