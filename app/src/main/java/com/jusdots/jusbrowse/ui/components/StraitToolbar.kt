package com.jusdots.jusbrowse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.data.models.TabDescriptor
import com.jusdots.jusbrowse.data.models.toBrowserTab
import com.jusdots.jusbrowse.ui.runtime.OptimizedFadeTransition
import com.jusdots.jusbrowse.ui.runtime.PrecomputedAnimations
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

@Composable
fun StraitToolbar(
    viewModel: BrowserViewModel,
    tabDescriptors: List<TabDescriptor>,
    activeTab: BrowserTab?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTabSwitcher by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val session = activeTab?.id?.let { viewModel.getGeckoSession(it) }
    val isSecure = activeTab?.url?.startsWith("https://") == true
    val displayUrl = activeTab?.let {
        val url = it.url.removePrefix("https://").removePrefix("http://").trimEnd('/')
        if (url.isBlank() || url == "about:blank") "" else url
    } ?: ""

    if (showTabSwitcher) {
        val activeTabIndex = tabDescriptors.indexOfFirst { it.id == activeTab?.id }.coerceAtLeast(0)
        TabSwitcherSheet(
            tabs = tabDescriptors.map { it.toBrowserTab() }, activeTabIndex = activeTabIndex,
            onTabSelected = { index -> viewModel.switchTab(index); showTabSwitcher = false },
            onTabClosed = { index -> viewModel.closeTab(index) },
            onNewTab = { viewModel.createNewTab(); showTabSwitcher = false },
            onDismiss = { showTabSwitcher = false }
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { session?.goBack() }, enabled = activeTab?.canGoBack == true, modifier = Modifier.size(36.dp)) {
                Icon(JusBrowseIcons.ArrowBack, contentDescription = "Back",
                    tint = if (activeTab?.canGoBack == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
            IconButton(onClick = { session?.goForward() }, enabled = activeTab?.canGoForward == true, modifier = Modifier.size(36.dp)) {
                Icon(JusBrowseIcons.ArrowForward, contentDescription = "Forward",
                    tint = if (activeTab?.canGoForward == true) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
            Spacer(Modifier.width(2.dp))

            var searchText by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current
            var isFocused by remember { mutableStateOf(false) }
            LaunchedEffect(activeTab?.id) { searchText = "" }

            Surface(
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(22.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(start = 14.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OptimizedFadeTransition(
                        visible = isSecure && !isFocused && searchText.isEmpty(),
                        curve = PrecomputedAnimations.easeOutCubic(200, 12)
                    ) { alpha ->
                        Row(Modifier.alpha(alpha)) {
                            Icon(JusBrowseIcons.Security, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f).onFocusChanged { isFocused = it.isFocused },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = if (!isFocused && displayUrl.isNotBlank()) displayUrl else "Search or enter address",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (!isFocused && displayUrl.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go, keyboardType = KeyboardType.Uri),
                        keyboardActions = KeyboardActions(onGo = {
                            if (searchText.isNotBlank()) {
                                onNavigate(searchText); searchText = ""; focusManager.clearFocus()
                            }
                        })
                    )
                    OptimizedFadeTransition(
                        visible = searchText.isNotEmpty(),
                        curve = PrecomputedAnimations.easeOutCubic(150, 8)
                    ) { alpha ->
                        IconButton(onClick = { searchText = "" }, modifier = Modifier.size(28.dp).alpha(alpha)) {
                            Icon(JusBrowseIcons.Close, "Clear", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.width(6.dp))

            Surface(
                onClick = { showTabSwitcher = true },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${tabDescriptors.size}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(Modifier.width(4.dp))
                    Icon(JusBrowseIcons.GridView, "Tabs", modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                    Icon(JusBrowseIcons.MoreVert, "Menu")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false },
                    shape = RoundedCornerShape(16.dp), tonalElevation = 4.dp) {
                    DropdownMenuItem(text = { Text("New tab") }, onClick = { viewModel.createNewTab(); showMenu = false }, leadingIcon = { Icon(JusBrowseIcons.Add, null) })
                    DropdownMenuItem(text = { Text("New private tab") }, onClick = { viewModel.createNewTab(isPrivate = true); showMenu = false }, leadingIcon = { Icon(JusBrowseIcons.VpnKey, null) })
                    DropdownMenuItem(text = { Text("History") }, onClick = { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.HISTORY); showMenu = false }, leadingIcon = { Icon(JusBrowseIcons.History, null) })
                    DropdownMenuItem(text = { Text("Bookmarks") }, onClick = { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.BOOKMARKS); showMenu = false }, leadingIcon = { Icon(JusBrowseIcons.Bookmark, null) })
                    DropdownMenuItem(text = { Text("Downloads") }, onClick = { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.DOWNLOADS); showMenu = false }, leadingIcon = { Icon(JusBrowseIcons.Download, null) })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    DropdownMenuItem(text = { Text("Settings") }, onClick = { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.SETTINGS); showMenu = false }, leadingIcon = { Icon(JusBrowseIcons.Settings, null) })
                }
            }
        }
    }
}

@Composable
private fun TabSwitcherSheet(
    tabs: List<BrowserTab>,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabClosed: (Int) -> Unit,
    onNewTab: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(enabled = false) { }
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Tabs (${tabs.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(tabs) { index, tab ->
                    Surface(
                        onClick = { onTabSelected(index) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (index == activeTabIndex) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (tab.isPrivate) JusBrowseIcons.VpnKey else JusBrowseIcons.Language,
                                contentDescription = null,
                                tint = if (tab.isPrivate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = tab.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(text = tab.url.replace("https://", "").replace("http://", ""), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = { onTabClosed(index) }, modifier = Modifier.size(32.dp)) {
                                Icon(JusBrowseIcons.Close, "Close tab", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            Button(
                onClick = onNewTab,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(JusBrowseIcons.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New tab")
            }
        }
    }
}
