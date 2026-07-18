package com.jusdots.jusbrowse.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jusdots.jusbrowse.data.models.BrowserTab
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.jusdots.jusbrowse.ui.theme.ContainerBanking
import com.jusdots.jusbrowse.ui.theme.ContainerPersonal
import com.jusdots.jusbrowse.ui.theme.ContainerSandbox
import com.jusdots.jusbrowse.ui.theme.ContainerShopping
import com.jusdots.jusbrowse.ui.theme.ContainerWork

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun BottomTabBar(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onTabSelected: (Int) -> Unit,
    onTabClosed: (Int) -> Unit,
    onNewTab: (String) -> Unit,
    onGroupTabs: (String, String) -> Unit = { _, _ -> },
    onUngroupTab: (String) -> Unit = {},
    onOpenTabGroup: (String?) -> Unit = {},
    showIcons: Boolean = false,
    showNewTabButton: Boolean = true,
    groupIdToShow: String? = null,
    activeGroupId: String? = null,
    compact: Boolean = false,
    chipHeight: String = "normal",
    activeStyle: String = "gradient",
    forceStatic: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showContainerMenu by remember { mutableStateOf(false) }
    val tabBounds = remember { mutableStateMapOf<String, androidx.compose.ui.geometry.Rect>() }
    val primary = MaterialTheme.colorScheme.primary

    // Filter to only show root tabs or tabs for the specified group
    // Filter logic:
    // If we are looking at a specific group (groupIdToShow), show its children.
    // Otherwise, if an activeGroupId is set GLOBALLY, show that group's children.
    // Otherwise, show root tabs.
    val effectiveGroupId = groupIdToShow ?: activeGroupId
    val currentTabs = if (effectiveGroupId == null) {
        tabs.filter { it.parentGroupId == null }
    } else {
        tabs.filter { it.parentGroupId == effectiveGroupId }
    }

    // Glass surface — NO BACKGROUND
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Divider removed for transparency

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Tab chips ────────────────────────────────────────────────────
            
            // Apply custom 900ms delay for Drag & Drop long press
            val currentViewConfig = androidx.compose.ui.platform.LocalViewConfiguration.current
            val customViewConfig = remember(currentViewConfig) {
                object : androidx.compose.ui.platform.ViewConfiguration by currentViewConfig {
                    override val longPressTimeoutMillis: Long
                        get() = 900L
                }
            }
            
            CompositionLocalProvider(androidx.compose.ui.platform.LocalViewConfiguration provides customViewConfig) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
                ) {
                    if (activeGroupId != null) {
                        // Back to Root button
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                                .clickable { onOpenTabGroup(null) }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = JusBrowseIcons.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    currentTabs.forEach { tab ->
                        val originalIndex = tabs.indexOf(tab)
                        var dragOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
                        var isDragging by remember { mutableStateOf(false) }
                        
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    tabBounds[tab.id] = coords.boundsInWindow()
                                }
                                .offset { androidx.compose.ui.unit.IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
                                .zIndex(if (isDragging) 1f else 0f)
                                .pointerInput(tab.id) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { isDragging = true },
                                        onDragCancel = {
                                            isDragging = false
                                            dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                        },
                                        onDragEnd = {
                                            isDragging = false
                                            // Swipe up to remove threshold
                                            if (dragOffset.y < -150f && tab.parentGroupId != null) {
                                                onUngroupTab(tab.id)
                                            } else {
                                                val myBounds = tabBounds[tab.id]
                                                if (myBounds != null) {
                                                    val dropPoint = myBounds.center + dragOffset
                                                    // Use 1D horizontal intersection since users scroll
                                                    val target = tabBounds.entries.find { 
                                                        it.key != tab.id && 
                                                        dropPoint.x >= it.value.left && 
                                                        dropPoint.x <= it.value.right &&
                                                        // Require y to just be within a generous overlapping box
                                                        dropPoint.y >= it.value.top - 100f &&
                                                        dropPoint.y <= it.value.bottom + 100f
                                                    }?.key
                                                    
                                                    if (target != null) {
                                                        onGroupTabs(tab.id, target)
                                                    }
                                                }
                                            }
                                            dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                        },
                                        onDrag = { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: androidx.compose.ui.geometry.Offset ->
                                            change.consume()
                                            dragOffset += dragAmount
                                        }
                                    )
                                }
                        ) {
                            val isDragTarget = tabBounds.entries.any {
                                it.key != tab.id &&
                                (tabBounds[tab.id]?.center?.plus(dragOffset) ?: androidx.compose.ui.geometry.Offset.Unspecified).let { p ->
                                    p.x >= it.value.left && p.x <= it.value.right &&
                                    p.y >= it.value.top - 50f && p.y <= it.value.bottom + 50f
                                } && it.key == tab.id // Wait, logic checked against all entries
                            }

                            // Corrected drag target logic: we are rendering 'tab'. 
                            // We want to know if SOME OTHER dragging tab is currently over US.
                            // This requires external state or a shared map.
                            // For simplicity, let's just use the dragOffset if WE are being dragged, 
                            // and a visual feedback on the chip itself.
                            
                            TabChip(
                                tab = tab,
                                isActive = tab.id == activeTabId,
                                onClick = { 
                                    if (tab.isGroupMaster) {
                                        onOpenTabGroup(tab.id)
                                    } else {
                                        onTabSelected(originalIndex)
                                    }
                                },
                                onLongClick = { }, // Empty for pure drag & drop
                                onClose = { onTabClosed(originalIndex) },
                                onUngroup = { onUngroupTab(tab.id) },
                                isInsideGroup = effectiveGroupId != null,
                                showIcon = showIcons,
                                compact = compact,
                                chipHeight = chipHeight,
                                activeStyle = activeStyle,
                                forceStatic = forceStatic
                            )
                        }
                    }
                }
            }

            // ── New Tab button ───────────────────────────────────────────────
            if (showNewTabButton) {
                Box {
                    FilledTonalIconButton(
                        onClick = { onNewTab("default") },
                        modifier = Modifier
                            .padding(start = if (compact) 4.dp else 6.dp)
                            .size(when (chipHeight) { "compact" -> 24.dp; "large" -> 44.dp; else -> 36.dp }),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.6f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = JusBrowseIcons.Add,
                            contentDescription = "New Tab",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Tiny arrow for container sub-menu
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .padding(end = 1.dp, bottom = 1.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.6f))
                            .clickable { showContainerMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = JusBrowseIcons.ArrowDropDown,
                            contentDescription = "Containers",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showContainerMenu,
                        onDismissRequest = { showContainerMenu = false }
                    ) {
                        com.jusdots.jusbrowse.security.ContainerManager.AVAILABLE_CONTAINERS.forEach { container ->
                            DropdownMenuItem(
                                text = { Text(com.jusdots.jusbrowse.security.ContainerManager.getContainerName(container)) },
                                onClick = {
                                    onNewTab(container)
                                    showContainerMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (container == "default") JusBrowseIcons.Public else JusBrowseIcons.Layers,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TabChip(
    tab: BrowserTab,
    isActive: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onClose: () -> Unit,
    onUngroup: () -> Unit = {},
    isInsideGroup: Boolean = false,
    showIcon: Boolean = false,
    compact: Boolean = false,
    chipHeight: String = "normal",
    activeStyle: String = "gradient",
    forceStatic: Boolean = false,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    val chipContent: @Composable (Boolean) -> Unit = { active ->
        val chipBackground = if (active) {
            when (activeStyle) {
                "solid" -> Brush.linearGradient(
                    colors = listOf(primary.copy(alpha = 0.85f), primary.copy(alpha = 0.85f))
                )
                "outline" -> Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                )
                else -> Brush.linearGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.85f),
                        primary.copy(alpha = 0.75f)
                    )
                )
            }
        } else
            Brush.linearGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.65f),
                    Color.Black.copy(alpha = 0.55f)
                )
            )

        val borderColor = if (active) primary.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f)
        val borderWidth = if (active) (if (activeStyle == "outline") 2.dp else 1.5.dp) else 1.dp

        val chipHeightDp = when (chipHeight) {
            "compact" -> 24.dp
            "large" -> 44.dp
            else -> 36.dp
        }
        val chipPadding = if (compact) 8.dp else 12.dp

        Row(
            modifier = modifier
                .height(chipHeightDp)
                .clip(CircleShape)
                .background(chipBackground)
                .border(borderWidth, borderColor, CircleShape)
                .then(
                    if (active) Modifier.drawBehind {
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primary.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                radius = size.width
                            )
                        )
                    } else Modifier
                )
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(horizontal = chipPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
        ) {
            if (tab.isGroupMaster) {
                Icon(
                    imageVector = JusBrowseIcons.FolderOpen,
                    contentDescription = "Group",
                    modifier = Modifier.size(16.dp),
                    tint = primary.copy(alpha = 1.0f)
                )
            }

            if (tab.isPrivate) {
                Icon(
                    imageVector = JusBrowseIcons.VpnKey,
                    contentDescription = "Private",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            val currentContainerId = tab.containerId ?: "default"
            if (currentContainerId != "default") {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            when (currentContainerId) {
                                "work"     -> ContainerWork
                                "personal" -> ContainerPersonal
                                "banking"  -> ContainerBanking
                                "sandbox"  -> ContainerSandbox
                                else       -> primary
                            }
                        )
                )
            }

            if (showIcon) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(primary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (tab.favicon != null) {
                        AsyncImage(
                            model = tab.favicon,
                            contentDescription = tab.title,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        val initial = try {
                            val url = if (tab.url == "about:blank") "N" else tab.url
                            android.net.Uri.parse(url).host?.firstOrNull()?.uppercase() ?: "N"
                        } catch (e: Exception) { "N" }
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = primary
                        )
                    }
                }
            } else {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.widthIn(max = 110.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isInsideGroup) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                        .clickable { onUngroup() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = JusBrowseIcons.UnfoldLess,
                        contentDescription = "Ungroup",
                        modifier = Modifier.size(11.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (active) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = JusBrowseIcons.Close,
                        contentDescription = "Close Tab",
                        modifier = Modifier.size(11.dp),
                        tint = primary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    if (forceStatic) {
        chipContent(isActive)
    } else {
        AnimatedContent(
            targetState = isActive,
            transitionSpec = {
                (fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.92f, animationSpec = tween(200)))
                    .togetherWith(fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.92f, animationSpec = tween(150)))
            },
            label = "tabChip"
        ) { active ->
            chipContent(active)
        }
    }
}

