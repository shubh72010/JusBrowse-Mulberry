package com.jusdots.jusbrowse.ui.components.safe

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons

@Composable
fun SafeBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onTabs: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onBack,
            enabled = canGoBack,
            icon = {
                Icon(
                    imageVector = JusBrowseIcons.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onForward,
            enabled = canGoForward,
            icon = {
                Icon(
                    imageVector = JusBrowseIcons.ArrowForward,
                    contentDescription = "Forward",
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onHome,
            icon = {
                Icon(
                    imageVector = JusBrowseIcons.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onTabs,
            icon = {
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
                        contentDescription = "Tabs",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onMenu,
            icon = {
                Icon(
                    imageVector = JusBrowseIcons.MoreVert,
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
