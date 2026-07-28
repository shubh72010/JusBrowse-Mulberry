package com.jusdots.jusbrowse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.jusdots.jusbrowse.ui.components.AddressBarWithGeckoView
import com.jusdots.jusbrowse.ui.components.BottomTabBar
import com.jusdots.jusbrowse.ui.components.BrowserToolBar
import com.jusdots.jusbrowse.ui.components.FreeformWorkspace
import com.jusdots.jusbrowse.ui.components.TabChip
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import com.jusdots.jusbrowse.ui.components.AirlockGallery
import com.jusdots.jusbrowse.ui.components.AirlockViewer
import com.jusdots.jusbrowse.ui.components.MediaData
import com.jusdots.jusbrowse.utils.MediaExtractor
import com.google.gson.Gson
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import com.jusdots.jusbrowse.ui.components.BackgroundRenderer
import com.jusdots.jusbrowse.ui.components.TransformableSticker
import androidx.compose.ui.input.pointer.pointerInput
import android.net.Uri
import android.widget.VideoView
import androidx.compose.ui.layout.ContentScale
import com.jusdots.jusbrowse.BuildConfig
import androidx.compose.ui.viewinterop.AndroidView
import android.content.Intent
import android.content.res.Configuration
import com.jusdots.jusbrowse.utils.UpdateInfo
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    // Collect active tab state - though we use Independent Windows mostly now
    val activeTabIndex by viewModel.activeTabIndex.collectAsStateWithLifecycle()
    val tabs = viewModel.tabs
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isMultiView by viewModel.isMultiViewMode.collectAsStateWithLifecycle()
    val showTabIcons by viewModel.showTabIcons.collectAsStateWithLifecycle(initialValue = false)
    val alwaysShowUrl by viewModel.alwaysShowUrl.collectAsStateWithLifecycle(initialValue = true)
    val reduceAnim by viewModel.reducedAnimations.collectAsStateWithLifecycle(initialValue = false)
    val showProgressBar by viewModel.showProgressBar.collectAsStateWithLifecycle(initialValue = true)
    val pillBottomMargin by viewModel.pillBottomMargin.collectAsStateWithLifecycle(initialValue = 90)
    val pillCollapsedWidth by viewModel.pillCollapsedWidth.collectAsStateWithLifecycle(initialValue = 260)
    val startPageBranding by viewModel.startPageBranding.collectAsStateWithLifecycle(initialValue = "full")
    val scrimDarkness by viewModel.scrimDarkness.collectAsStateWithLifecycle(initialValue = "normal")
    val pillBlurOpacity by viewModel.pillBlurOpacity.collectAsStateWithLifecycle(initialValue = 0.7f)
    val contentCornerRadius by viewModel.contentCornerRadius.collectAsStateWithLifecycle(initialValue = 25)
    val contentPadding by viewModel.contentPadding.collectAsStateWithLifecycle(initialValue = 6)
    val tabChipHeight by viewModel.tabChipHeight.collectAsStateWithLifecycle(initialValue = "normal")
    val activeTabStyle by viewModel.activeTabStyle.collectAsStateWithLifecycle(initialValue = "gradient")

    val context = LocalContext.current
    
    // Helper to trigger extraction
    fun openAirlockGallery() {
        val currentTab = if (activeTabIndex in tabs.indices) tabs[activeTabIndex] else null
        if (currentTab != null) {
            viewModel.triggerMediaExtraction(currentTab.id)
        }
    }

    // Handle Back Press at high level
    androidx.activity.compose.BackHandler(enabled = true) {
        when (currentScreen) {
            Screen.SETTINGS, Screen.HISTORY, Screen.BOOKMARKS, Screen.DOWNLOADS, Screen.EXTENSIONS -> {
                viewModel.navigateToScreen(Screen.BROWSER)
            }
            Screen.EXTENSION_DETAIL -> {
                viewModel.navigateToScreen(Screen.EXTENSIONS)
            }
            Screen.BROWSER -> {
                // If in multi-view, maybe exit multi-view?
                if (isMultiView) {
                    // For now, let it exit app or user logic preference
                    // Could toggle toggleMultiViewMode()
                } else {
                     // In single view, try to go back in active Webview
                    val currentTab = if (activeTabIndex in tabs.indices) tabs[activeTabIndex] else null
                    if (currentTab != null) {
                        val session = viewModel.getGeckoSession(currentTab.id)
                        // Note: GeckoSession.goBack() is available, canGoBack check usually async
                        // but navigation state is tracked in ViewModel from onLocationChange
                        if (currentTab.canGoBack) {
                            session?.goBack()
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val wallpaperUri by viewModel.startPageWallpaperUri.collectAsStateWithLifecycle(initialValue = null)
        val blurAmount by viewModel.startPageBlurAmount.collectAsStateWithLifecycle(initialValue = 0f)
        val backgroundPresetName by viewModel.backgroundPreset.collectAsStateWithLifecycle(initialValue = "NONE")
        
        val backgroundPreset = try {
            com.jusdots.jusbrowse.ui.theme.BackgroundPreset.valueOf(backgroundPresetName)
        } catch (e: Exception) {
            com.jusdots.jusbrowse.ui.theme.BackgroundPreset.NONE
        }

        // Dynamic Blur for Secondary Screens
        val secondaryScreenBlur = if (currentScreen != Screen.BROWSER) 25.dp else 0.dp
        val totalBlurAmount = (blurAmount.dp + secondaryScreenBlur)

        val stickers = viewModel.stickers
        val selectedStickerId by viewModel.selectedStickerId.collectAsStateWithLifecycle()

        // Global Background Layer
        Box(modifier = Modifier.fillMaxSize()) {
            // Animated Background Preset (if no custom wallpaper)
            if (wallpaperUri == null && backgroundPreset != com.jusdots.jusbrowse.ui.theme.BackgroundPreset.NONE) {
                BackgroundRenderer(
                    preset = backgroundPreset,
                    modifier = Modifier.fillMaxSize().blur(totalBlurAmount)
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
            }
            
            // Custom Wallpaper (Image or Video)
            if (wallpaperUri != null) {
                val uri = Uri.parse(wallpaperUri)
                val isVideo = context.contentResolver.getType(uri)?.startsWith("video/") == true || 
                              wallpaperUri!!.lowercase().endsWith(".mp4") || 
                              wallpaperUri!!.lowercase().endsWith(".mkv") ||
                              wallpaperUri!!.lowercase().endsWith(".webm")

                if (isVideo) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(uri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    mp.setVolume(0f, 0f)
                                    // Scale to fit center crop style
                                    val videoWidth = mp.videoWidth.toFloat()
                                    val videoHeight = mp.videoHeight.toFloat()
                                    val viewWidth = width.toFloat()
                                    val viewHeight = height.toFloat()
                                    val scale = Math.max(viewWidth / videoWidth, viewHeight / videoHeight)
                                    // This scaling is tricky with VideoView, usually requires an overlay or custom layout
                                    // For now, simple VideoView is enough
                                    start()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize().blur(totalBlurAmount),
                        update = { view ->
                             // Ensure it's still playing/correct URI if it changes
                             // view.setVideoURI(uri) // Careful with restarts
                        },
                        onRelease = { view ->
                            view.stopPlayback()
                            view.setVideoURI(null)
                        }
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(wallpaperUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().blur(totalBlurAmount)
                    )
                }
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            }
        }

        // Global Hide/Reveal Animation Logic
        LaunchedEffect(Unit) {
            merge(
                viewModel.revealBottomBarEvent.map { 0f },
                viewModel.hideBottomBarEvent.map { 
                    // Use a reasonable default or the last known height if available
                    // For now, 150f is a safe bet for the full bar + padding
                    150f * context.resources.displayMetrics.density 
                }
            ).collectLatest { targetValue ->
                val currentOffset = viewModel.bottomBarOffsetHeightPx.value
                androidx.compose.animation.core.animate(
                    initialValue = currentOffset,
                    targetValue = targetValue,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { value, _ ->
                    viewModel.updateBottomBarOffset(value)
                }
            }
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                // In Multi-View, keep a minimal toolbar at the top
                if (isMultiView) {
                    BrowserToolBar(
                        viewModel = viewModel,
                        currentTab = null,
                        onOpenAirlockGallery = { openAirlockGallery() }
                    )
                }
            },
            bottomBar = {
                // Moved BottomTabBar out of scaffold bottomBar slot to prevent touch blocking
                // It will now be placed as an overlay in the main Box below
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    Screen.BROWSER -> {
                         val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                        if (isLandscape && !isMultiView) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                // Left: slim vertical tab strip — tabs are the dominant companion
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(top = 8.dp, bottom = 8.dp)
                                    ) {
                                        tabs.forEachIndexed { index, tab ->
                                            val isActive = tabs.getOrNull(activeTabIndex)?.id == tab.id
                                            TabChip(
                                                tab = tab,
                                                isActive = isActive,
                                                onClick = { viewModel.switchTab(index) },
                                                onClose = { viewModel.closeTab(index) },
                                                onUngroup = { viewModel.ungroupTab(tab.id) },
                                                showIcon = showTabIcons,
                                                chipHeight = "normal",
                                                activeStyle = activeTabStyle,
                                                compact = true,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 1.dp)
                                            )
                                        }
                                        // New Tab button
                                        FilledTonalIconButton(
                                            onClick = { viewModel.createNewTab() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                                contentColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = JusBrowseIcons.Add,
                                                contentDescription = "New Tab",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Right: web renderer — dominant element, fills all available space
                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    if (activeTabIndex in tabs.indices) {
                                        AddressBarWithGeckoView(
                                            viewModel = viewModel,
                                            tab = tabs.getOrNull(activeTabIndex),
                                            onOpenAirlockGallery = { openAirlockGallery() },
                                            alwaysShowUrl = alwaysShowUrl,
                                            reduceAnim = reduceAnim,
                                            showProgressBar = showProgressBar,
                                            pillBottomMarginDp = pillBottomMargin,
                                            pillCollapsedWidthDp = pillCollapsedWidth,
                                            startPageBranding = startPageBranding,
                                            scrimDarkness = scrimDarkness,
                                            pillBlurOpacity = pillBlurOpacity,
                                            contentCornerRadius = contentCornerRadius,
                                            contentPadding = contentPadding,
                                            contentBottomPadding = 8,
                                            modifier = Modifier.fillMaxSize(),
                                            stickerContent = {
                                                val stickersEnabled by viewModel.stickersEnabled.collectAsStateWithLifecycle(initialValue = true)
                                                val activeTab = tabs.getOrNull(activeTabIndex)
                                                val isStartPage = activeTab?.url == "about:blank" || activeTab?.url?.isEmpty() == true
                                                if (stickersEnabled && currentScreen == Screen.BROWSER && !isMultiView && isStartPage) {
                                                    BoxWithConstraints(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .pointerInput(Unit) {
                                                                detectTapGestures {
                                                                    viewModel.setSelectedStickerId(null)
                                                                }
                                                            }
                                                    ) {
                                                        val sWidth = maxWidth.value
                                                        val sHeight = maxHeight.value
                                                        stickers.forEach { sticker ->
                                                            TransformableSticker(
                                                                sticker = sticker,
                                                                isSelected = selectedStickerId == sticker.id,
                                                                screenWidth = sWidth,
                                                                screenHeight = sHeight,
                                                                onTransform = { x, y, w, h, r ->
                                                                    viewModel.updateStickerTransform(sticker.id, x, y, w, h, r)
                                                                },
                                                                onClick = {
                                                                    viewModel.setSelectedStickerId(sticker.id)
                                                                    sticker.link?.let { link ->
                                                                        viewModel.navigateToUrlByTabId(activeTab?.id ?: "", link)
                                                                    }
                                                                },
                                                                onDelete = {
                                                                    viewModel.removeSticker(sticker.id)
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isMultiView) {
                                    FreeformWorkspace(
                                        viewModel = viewModel,
                                        tabs = tabs,
                                        modifier = Modifier.fillMaxSize().padding(paddingValues)
                                    )
                                } else {
                                    if (activeTabIndex in tabs.indices) {
                                        AddressBarWithGeckoView(
                                            viewModel = viewModel,
                                            tab = tabs.getOrNull(activeTabIndex),
                                            onOpenAirlockGallery = { openAirlockGallery() },
                                            alwaysShowUrl = alwaysShowUrl,
                                            reduceAnim = reduceAnim,
                                            showProgressBar = showProgressBar,
                                            pillBottomMarginDp = pillBottomMargin,
                                            pillCollapsedWidthDp = pillCollapsedWidth,
                                            startPageBranding = startPageBranding,
                                            scrimDarkness = scrimDarkness,
                                            pillBlurOpacity = pillBlurOpacity,
                                            contentCornerRadius = contentCornerRadius,
                                            contentPadding = contentPadding,
                                            modifier = Modifier.fillMaxSize(),
                                            stickerContent = {
                                                val stickersEnabled by viewModel.stickersEnabled.collectAsStateWithLifecycle(initialValue = true)
                                                val activeTab = tabs.getOrNull(activeTabIndex)
                                                val isStartPage = activeTab?.url == "about:blank" || activeTab?.url?.isEmpty() == true
                                                if (stickersEnabled && currentScreen == Screen.BROWSER && !isMultiView && isStartPage) {
                                                    BoxWithConstraints(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .pointerInput(Unit) {
                                                                detectTapGestures {
                                                                    viewModel.setSelectedStickerId(null)
                                                                }
                                                            }
                                                    ) {
                                                        val sWidth = maxWidth.value
                                                        val sHeight = maxHeight.value
                                                        stickers.forEach { sticker ->
                                                            TransformableSticker(
                                                                sticker = sticker,
                                                                isSelected = selectedStickerId == sticker.id,
                                                                screenWidth = sWidth,
                                                                screenHeight = sHeight,
                                                                onTransform = { x, y, w, h, r ->
                                                                    viewModel.updateStickerTransform(sticker.id, x, y, w, h, r)
                                                                },
                                                                onClick = {
                                                                    viewModel.setSelectedStickerId(sticker.id)
                                                                    sticker.link?.let { link ->
                                                                        viewModel.navigateToUrlByTabId(activeTab?.id ?: "", link)
                                                                    }
                                                                },
                                                                onDelete = {
                                                                    viewModel.removeSticker(sticker.id)
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                val isKeyboardVisible = WindowInsets.ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0
                                if (!isMultiView && !isKeyboardVisible) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .navigationBarsPadding()
                                    ) {
                                        val activeGroupId by viewModel.activeGroupId.collectAsStateWithLifecycle()
                                        if (activeGroupId != null) {
                                            val groupChildrenCount = tabs.count { it.parentGroupId == activeGroupId }
                                            if (groupChildrenCount > 0) {
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    BottomTabBar(
                                                        tabs = tabs,
                                                        activeTabId = tabs.getOrNull(activeTabIndex)?.id ?: "",
                                                        onTabSelected = { index -> viewModel.switchTab(index) },
                                                        onTabClosed = { index -> viewModel.closeTab(index) },
                                                        onNewTab = { containerId ->
                                                            val currentGroupId = activeGroupId
                                                            viewModel.createNewTab(containerId = containerId)
                                                            val newTabIndex = tabs.lastIndex
                                                            if (newTabIndex >= 0 && currentGroupId != null) {
                                                                viewModel.groupTabs(tabs[newTabIndex].id, currentGroupId)
                                                            }
                                                        },
                                                        onGroupTabs = { draggedId, targetId -> viewModel.groupTabs(draggedId, targetId) },
                                                        onUngroupTab = { tabId -> viewModel.ungroupTab(tabId) },
                                                        groupIdToShow = activeGroupId,
                                                        showIcons = showTabIcons,
                                                        showNewTabButton = false,
                                                        chipHeight = tabChipHeight,
                                                        activeStyle = activeTabStyle
                                                    )
                                                    IconButton(
                                                        onClick = { viewModel.openTabGroup(null) },
                                                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                                                    ) {
                                                        Icon(JusBrowseIcons.Close, contentDescription = "Close Group")
                                                    }
                                                }
                                            } else {
                                                viewModel.openTabGroup(null)
                                            }
                                        }
                                        BottomTabBar(
                                            tabs = tabs,
                                            activeTabId = tabs.getOrNull(activeTabIndex)?.id ?: "",
                                            onTabSelected = { index ->
                                                val tab = tabs.getOrNull(index)
                                                if (tab?.isGroupMaster == true) {
                                                    viewModel.switchTab(index)
                                                    if (activeGroupId == tab.id) {
                                                        viewModel.openTabGroup(null)
                                                    } else {
                                                        viewModel.openTabGroup(tab.id)
                                                    }
                                                } else {
                                                    viewModel.switchTab(index)
                                                }
                                            },
                                            onTabClosed = { index -> viewModel.closeTab(index) },
                                            onNewTab = { containerId -> viewModel.createNewTab(containerId = containerId) },
                                            onGroupTabs = { draggedId, targetId -> viewModel.groupTabs(draggedId, targetId) },
                                            onUngroupTab = { tabId -> viewModel.ungroupTab(tabId) },
                                            onOpenTabGroup = { viewModel.openTabGroup(it) },
                                            activeGroupId = activeGroupId,
                                            showIcons = showTabIcons,
                                            chipHeight = tabChipHeight,
                                            activeStyle = activeTabStyle,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Screen.BOOKMARKS -> {
                        BookmarksScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToScreen(Screen.BROWSER) }
                        )
                    }
                    Screen.HISTORY -> {
                        HistoryScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToScreen(Screen.BROWSER) }
                        )
                    }
                    Screen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToScreen(Screen.BROWSER) }
                        )
                    }
                    Screen.DOWNLOADS -> {
                        DownloadsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateToScreen(Screen.BROWSER) }
                        )
                    }
                    Screen.EXTENSIONS -> {
                        val extMan = com.jusdots.jusbrowse.BrowserApplication.extensionManager
                        if (extMan != null) {
                            ExtensionsScreen(
                                extensionManager = extMan,
                                onBack = { viewModel.navigateToScreen(Screen.BROWSER) },
                                onExtensionClick = { ext ->
                                    viewModel.selectedExtension = ext
                                    viewModel.navigateToScreen(Screen.EXTENSION_DETAIL)
                                }
                            )
                        }
                    }
                    Screen.EXTENSION_DETAIL -> {
                        val ext = viewModel.selectedExtension
                        val extMan = com.jusdots.jusbrowse.BrowserApplication.extensionManager
                        if (ext != null && extMan != null) {
                            ExtensionDetailScreen(
                                extension = ext,
                                extensionManager = extMan,
                                onBack = { viewModel.navigateToScreen(Screen.EXTENSIONS) },
                                onUninstall = { viewModel.navigateToScreen(Screen.EXTENSIONS) },
                                onToggleEnabled = { id, enabled -> viewModel.setExtensionEnabled(id, enabled) }
                            )
                        }
                    }
                }

                // Global Overlays
                if (viewModel.showGallery && viewModel.galleryMediaData != null) {
                    AirlockGallery(
                        mediaData = viewModel.galleryMediaData!!,
                        isVaulting = viewModel.isVaulting,
                        vaultProgress = viewModel.vaultProgress,
                        onMediaClick = { url, mimeType, list, index ->
                            viewModel.openAirlockViewer(url, mimeType, list, index)
                            viewModel.showGallery = false
                        },
                        onClose = { viewModel.closeAirlock() },
                        modifier = Modifier.fillMaxSize() 
                    )
                }

                if (viewModel.showAirlock) {
                    AirlockViewer(
                        initialUrl = viewModel.airlockUrl,
                        initialMimeType = viewModel.airlockMimeType,
                        mediaList = viewModel.viewerMediaList,
                        initialIndex = viewModel.viewerInitialIndex,
                        onDismiss = { 
                            viewModel.showAirlock = false
                            viewModel.showGallery = true 
                        },
                        onDownload = { url ->
                            val fileName = url.substringAfterLast("/").substringBefore("?").ifEmpty { "downloaded_file" }
                            viewModel.startDownload(context, url, fileName)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Update Available Dialog
                val updateState by viewModel.updateState.collectAsStateWithLifecycle()
                if (updateState is BrowserViewModel.UpdateState.Available) {
                    val info = (updateState as BrowserViewModel.UpdateState.Available).info
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissUpdateDialog() },
                        icon = { Icon(JusBrowseIcons.Info, contentDescription = null) },
                        title = { Text("Update Available") },
                        text = {
                            Text("Version ${info.latestVersion} is available (current: ${BuildConfig.VERSION_NAME}).")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl)).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                                viewModel.dismissUpdateDialog()
                            }) { Text("Download") }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.dismissUpdateDialog() }) { Text("Later") }
                        }
                    )
                }

                // 3. Global Reveal Trigger (Bypass Overlays) - Hardened
                // catches upward swipes even if BottomTabBar or other overlays are absorbing touches.
                // increased height to cover the tab bar region and navigation bar.
                val bottomBarOffsetHeightPxState by viewModel.bottomBarOffsetHeightPx.collectAsStateWithLifecycle()
                
                if (currentScreen == Screen.BROWSER && !isMultiView && bottomBarOffsetHeightPxState > 10f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Bottom-Right corner
                            .size(120.dp) // Swipe up zone size
                            .pointerInput(bottomBarOffsetHeightPxState) {
                                detectVerticalDragGestures { _: PointerInputChange, dragAmount: Float ->
                                    if (dragAmount < -10f) {
                                        viewModel.triggerRevealBottomBar()
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}


