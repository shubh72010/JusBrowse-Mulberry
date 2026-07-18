package com.jusdots.jusbrowse.ui.components.safe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeTopBar(
    currentUrl: String,
    title: String,
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    isSecure: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onTabsClick: () -> Unit,
    onMenuAction: (SafeMenuAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        TopAppBar(
            title = {
                SafeUrlChip(
                    currentUrl = currentUrl,
                    title = title,
                    isSecure = isSecure,
                    onClick = { onMenuAction(SafeMenuAction.EditUrl) }
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, enabled = canGoBack) {
                    Icon(
                        imageVector = JusBrowseIcons.ArrowBack,
                        contentDescription = "Back",
                        tint = if (canGoBack) MaterialTheme.colorScheme.onSurface
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            },
            actions = {
                IconButton(onClick = onForward, enabled = canGoForward) {
                    Icon(
                        imageVector = JusBrowseIcons.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (canGoForward) MaterialTheme.colorScheme.onSurface
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
                IconButton(onClick = onTabsClick) {
                    BadgedBox(
                        badge = {
                            if (tabCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text(
                                        text = if (tabCount > 99) "99+" else tabCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = JusBrowseIcons.TabUnselected,
                            contentDescription = "Tabs"
                        )
                    }
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = JusBrowseIcons.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        SafeMenuItem(JusBrowseIcons.Add, "New tab") { onMenuAction(SafeMenuAction.NewTab); menuExpanded = false }
                        SafeMenuItem(JusBrowseIcons.Security, "New private tab") { onMenuAction(SafeMenuAction.NewPrivateTab); menuExpanded = false }
                        HorizontalDivider()
                        SafeMenuItem(JusBrowseIcons.Bookmark, "Bookmarks") { onMenuAction(SafeMenuAction.Bookmarks); menuExpanded = false }
                        SafeMenuItem(JusBrowseIcons.History, "History") { onMenuAction(SafeMenuAction.History); menuExpanded = false }
                        SafeMenuItem(JusBrowseIcons.Download, "Downloads") { onMenuAction(SafeMenuAction.Downloads); menuExpanded = false }
                        HorizontalDivider()
                        SafeMenuItem(JusBrowseIcons.Share, "Share page") { onMenuAction(SafeMenuAction.Share); menuExpanded = false }
                        HorizontalDivider()
                        SafeMenuItem(JusBrowseIcons.Settings, "Settings") { onMenuAction(SafeMenuAction.Settings); menuExpanded = false }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun SafeUrlChip(
    currentUrl: String,
    title: String,
    isSecure: Boolean,
    onClick: () -> Unit
) {
    val displayText = remember(currentUrl, title) {
        when {
            currentUrl.isBlank() || currentUrl == "about:blank" -> "Search or type URL"
            title.isNotBlank() && currentUrl.contains(title, ignoreCase = false) -> currentUrl
            else -> title.ifBlank { currentUrl }
        }
    }
    val hostOnly = remember(currentUrl) { extractHost(currentUrl) }
    val showHostOnly = displayText == currentUrl && hostOnly.isNotBlank() && hostOnly != currentUrl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSecure) JusBrowseIcons.Lock else JusBrowseIcons.LockOpen,
            contentDescription = if (isSecure) "Secure" else "Not secure",
            tint = if (isSecure) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (showHostOnly) hostOnly else displayText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SafeMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        onClick = onClick
    )
}

private fun extractHost(url: String): String {
    if (url.isBlank() || url == "about:blank") return ""
    return runCatching {
        val withScheme = if (url.contains("://")) url else "https://$url"
        val cleaned = withScheme.substringAfter("://").substringBefore('/')
        cleaned.substringBefore('?').substringBefore('#')
    }.getOrDefault(url)
}

enum class SafeMenuAction {
    EditUrl,
    NewTab,
    NewPrivateTab,
    Bookmarks,
    History,
    Downloads,
    Share,
    Settings
}
