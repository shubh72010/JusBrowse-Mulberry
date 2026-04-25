package com.jusdots.jusbrowse.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Masks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Language
import com.jusdots.jusbrowse.ui.components.DnsPresets
import com.jusdots.jusbrowse.ui.components.DnsProvider
import coil.request.ImageRequest
import com.jusdots.jusbrowse.security.FakeModeManager

import com.jusdots.jusbrowse.ui.components.FakeModeDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Image
import com.jusdots.jusbrowse.data.models.Sticker
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.jusdots.jusbrowse.ui.theme.BrowserTheme
import com.jusdots.jusbrowse.ui.theme.AppFont
import com.jusdots.jusbrowse.ui.theme.BackgroundPreset
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import com.jusdots.jusbrowse.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit
) {
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle(initialValue = "DuckDuckGo")
    val javascriptEnabled by viewModel.javascriptEnabled.collectAsStateWithLifecycle(initialValue = true)
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle(initialValue = false)
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle(initialValue = true)
    val advancedAdBlockEnabled by viewModel.advancedAdBlockEnabled.collectAsStateWithLifecycle(initialValue = false)
    val httpsOnly by viewModel.httpsOnly.collectAsStateWithLifecycle(initialValue = false)
    val flagSecureEnabled by viewModel.flagSecureEnabled.collectAsStateWithLifecycle(initialValue = true)
    val doNotTrackEnabled by viewModel.doNotTrackEnabled.collectAsStateWithLifecycle(initialValue = false)
    val analyticsEnabled by viewModel.analyticsEnabled.collectAsStateWithLifecycle(initialValue = false)
    val cookieBlockerEnabled by viewModel.cookieBlockerEnabled.collectAsStateWithLifecycle(initialValue = false)
    val popupBlockerEnabled by viewModel.popupBlockerEnabled.collectAsStateWithLifecycle(initialValue = true)
    val showTabIcons by viewModel.showTabIcons.collectAsStateWithLifecycle(initialValue = false)
    val vtApiKey by viewModel.virusTotalApiKey.collectAsStateWithLifecycle(initialValue = "")
    val koodousApiKey by viewModel.koodousApiKey.collectAsStateWithLifecycle(initialValue = "")
    val customDohUrl by viewModel.customDohUrl.collectAsStateWithLifecycle(initialValue = "")
    val customSearchEngineUrl by viewModel.customSearchEngineUrl.collectAsStateWithLifecycle(initialValue = "")
    val follianMode by viewModel.follianMode.collectAsStateWithLifecycle(initialValue = false)
    val amoledBlackEnabled by viewModel.amoledBlackEnabled.collectAsStateWithLifecycle(initialValue = false)
    val appFont by viewModel.appFont.collectAsStateWithLifecycle(initialValue = "SYSTEM")
    val stickers = viewModel.stickers
    val wallColorExtracted by viewModel.extractedWallColor.collectAsStateWithLifecycle()

    // Engines
    val defaultEngineEnabled by viewModel.defaultEngineEnabled.collectAsStateWithLifecycle(initialValue = true)
    val jusFakeEngineEnabled by viewModel.jusFakeEngineEnabled.collectAsStateWithLifecycle(initialValue = false)
    val boringEngineEnabled by viewModel.boringEngineEnabled.collectAsStateWithLifecycle(initialValue = false)
    val multiMediaPlaybackEnabled by viewModel.multiMediaPlaybackEnabled.collectAsStateWithLifecycle(initialValue = false)
    val protectionWhitelist by viewModel.protectionWhitelist.collectAsStateWithLifecycle(initialValue = "")
    val maxCacheSizeMB by viewModel.maxCacheSizeMB.collectAsStateWithLifecycle(initialValue = 1024)
    val cachePolicyWipeOnFull by viewModel.cachePolicyWipeOnFull.collectAsStateWithLifecycle(initialValue = false)
    val cachePolicyLRU by viewModel.cachePolicyLRU.collectAsStateWithLifecycle(initialValue = true)
    
    // Fake Mode state
    val fakeModeEnabled by FakeModeManager.isEnabled.collectAsStateWithLifecycle()
    val currentPersona by FakeModeManager.currentPersona.collectAsStateWithLifecycle()
    var showFakeModeDialog by remember { mutableStateOf(false) }

    var editingSticker by remember { mutableStateOf<Sticker?>(null) }
    var stickerLinkText by remember { mutableStateOf("") }
    
    // Context for FakeModeManager (App Restart)
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // DNS Presets state
    var showDnsPresets by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    // Local state for DNS URL to fix text reversal
    var dnsUrlLocal by remember(customDohUrl) { mutableStateOf(customDohUrl) }

    if (showDnsPresets) {
        ModalBottomSheet(
            onDismissRequest = { showDnsPresets = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Select DNS Provider",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                val groupedPresets = DnsPresets.providers.groupBy { it.category }
                
                groupedPresets.forEach { (category, providers) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    
                    items(providers) { provider ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    dnsUrlLocal = provider.dohUrl
                                    viewModel.setCustomDohUrl(provider.dohUrl)
                                    showDnsPresets = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(provider.icon, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(provider.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    Text(provider.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.Check, null, tint = if (customDohUrl == provider.dohUrl) MaterialTheme.colorScheme.primary else Color.Transparent)
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.65f))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Storage & Cache (New Section)
            Text(
                text = "Storage & Cache (Experimental)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Disk Cache Limit",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (maxCacheSizeMB >= 1000) 
                                String.format("%.1f GB", maxCacheSizeMB / 1024f) 
                            else 
                                "$maxCacheSizeMB MB",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    TextButton(onClick = { viewModel.clearAllCache() }) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Clear Cache")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Slider(
                    value = maxCacheSizeMB.toFloat(),
                    onValueChange = { viewModel.setMaxCacheSizeMB(it.toInt()) },
                    valueRange = 20f..2500f,
                    steps = 0, // Continuous
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("20 MB", style = MaterialTheme.typography.labelSmall)
                    Text("2.5 GB", style = MaterialTheme.typography.labelSmall)
                }
                
                Text(
                    text = "Requires browser restart to apply new limits.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cache Management Policy",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                SettingsSwitch(
                    title = "Smart Eviction",
                    subtitle = "Auto-delete oldest cache entries when limit is full (LRU)",
                    checked = cachePolicyLRU,
                    onCheckedChange = { viewModel.setCachePolicyLRU(it) }
                )

                SettingsSwitch(
                    title = "Nuclear Wipe",
                    subtitle = "Instantly clear ALL cache when limit is reached (High Privacy)",
                    checked = cachePolicyWipeOnFull,
                    onCheckedChange = { viewModel.setCachePolicyWipeOnFull(it) }
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // General
            Text(
                text = "General",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchEngine,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Search Engine") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("DuckDuckGo", "Google", "Bing", "Brave", "Custom").forEach { engine ->
                        DropdownMenuItem(
                            text = { Text(engine) },
                            onClick = { 
                                viewModel.setSearchEngine(engine)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            AnimatedVisibility(visible = searchEngine.lowercase() == "custom") {
                var customUrlText by remember(customSearchEngineUrl) { mutableStateOf(customSearchEngineUrl) }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customUrlText,
                        onValueChange = { customUrlText = it },
                        label = { Text("Custom Search URL Template") },
                        placeholder = { Text("https://example.com/search?q=%s") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = { Text("Use %s where the search query should be placed.") },
                        trailingIcon = {
                            if (customUrlText != customSearchEngineUrl) {
                                IconButton(onClick = { 
                                    viewModel.setCustomSearchEngineUrl(customUrlText.trim())
                                }) {
                                    Icon(Icons.Default.Check, contentDescription = "Save Custom URL")
                                }
                            }
                        }
                    )
                }
            }

            // Custom Start Page
            val homePage by viewModel.homePage.collectAsStateWithLifecycle(initialValue = "about:blank")
            var homePageText by remember(homePage) { mutableStateOf(homePage) }
            
            OutlinedTextField(
                value = homePageText,
                onValueChange = { homePageText = it },
                label = { Text("Custom Start Page") },
                placeholder = { Text("https://example.com or anything, babe") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Leave empty or use 'about:blank' for the default new tab page") },
                trailingIcon = {
                    if (homePageText != homePage) {
                        IconButton(onClick = { 
                            val finalUrl = if (homePageText.isBlank()) "about:blank" else homePageText
                            viewModel.setHomePage(finalUrl)
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // Appearance & Customization
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Wallpaper Picker
            val wallpaperUri by viewModel.startPageWallpaperUri.collectAsStateWithLifecycle(initialValue = null)
            val blurAmount by viewModel.startPageBlurAmount.collectAsStateWithLifecycle(initialValue = 0f)
            
            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let {
                    // Take persistable permission
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            it,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    viewModel.setStartPageWallpaperUri(it.toString())
                }
            }

            val stickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            it,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    viewModel.addSticker(it.toString())
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Start Page Wallpaper",
                    style = MaterialTheme.typography.titleSmall
                )
                
                if (wallpaperUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(wallpaperUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Wallpaper Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(blurAmount.dp)
                        )
                        
                        // Overlay sample text to show effect
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "JusBrowse",
                                    style = MaterialTheme.typography.displayMedium, // Smaller than actual for preview
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.setStartPageWallpaperUri(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, "Remove Wallpaper")
                        }
                    }
                    
                    // Blur Slider
                    Column {
                        Text(
                            text = "Blur: ${blurAmount.toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Slider(
                            value = blurAmount,
                            onValueChange = { viewModel.setStartPageBlurAmount(it) },
                            valueRange = 0f..25f,
                            steps = 24
                        )
                    }

                } else {
                    OutlinedButton(
                        onClick = { launcher.launch(arrayOf("image/*", "video/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Select Wallpaper (Image/Video)")
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // Background Presets
            val backgroundPreset by viewModel.backgroundPreset.collectAsStateWithLifecycle(initialValue = "NONE")
            Text(
                text = "Background Presets",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Add animated gradients to your browser interface",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(com.jusdots.jusbrowse.ui.theme.BackgroundPreset.values().size) { index ->
                    val preset = com.jusdots.jusbrowse.ui.theme.BackgroundPreset.values()[index]
                    BackgroundPresetCard(
                        preset = preset,
                        isSelected = backgroundPreset == preset.name,
                        onClick = { viewModel.setBackgroundPreset(preset.name) }
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // Stickers Section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Stickers",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Decorate your start page with custom images. Drag to position, tap to peel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (stickers.size == 0) {
                    Text(
                        text = "No stickers added yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            count = stickers.size,
                            key = { index -> stickers[index].id }
                        ) { index ->
                            val sticker = stickers[index]
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .pointerInput(sticker.id) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = { },
                                                onDragEnd = { viewModel.saveStickers() },
                                                onDragCancel = { viewModel.saveStickers() },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    val d = this@pointerInput.density
                                                    // Convert drag relative to screen dims
                                                    val deltaX = dragAmount.x / (screenWidth.value * d)
                                                    val deltaY = dragAmount.y / (screenHeight.value * d)
                                                    
                                                    viewModel.updateStickerTransform(
                                                        sticker.id,
                                                        (sticker.x + deltaX).coerceIn(0f, 1f),
                                                        (sticker.y + deltaY).coerceIn(0f, 1f),
                                                        sticker.widthDp,
                                                        sticker.heightDp,
                                                        sticker.rotation
                                                    )
                                                }
                                            )
                                        }
                                        .clickable { 
                                            editingSticker = sticker
                                            stickerLinkText = sticker.link ?: ""
                                        }
                                ) {
                                    AsyncImage(
                                        model = sticker.imageUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeSticker(sticker.id) },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (sticker.link != null) "🔗 Link" else "No Link",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (sticker.link != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { stickerLauncher.launch(arrayOf("image/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Add Sticker")
                }
            }

            if (editingSticker != null) {
                AlertDialog(
                    onDismissRequest = { editingSticker = null },
                    title = { Text("Edit Sticker Link") },
                    text = {
                        Column {
                            Text("Enter a URL to open when this sticker is tapped on the start page.")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stickerLinkText,
                                onValueChange = { stickerLinkText = it },
                                placeholder = { Text("https://example.com") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val st = editingSticker
                            if (st != null) {
                                viewModel.updateStickerLink(
                                    st.id,
                                    if (stickerLinkText.isBlank()) null else stickerLinkText
                                )
                                editingSticker = null
                            }
                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingSticker = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // Privacy & Security
            Text(
                text = "Privacy & Security",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            // 🛡️ Protection Engines
            Text(
                text = "Fingerprinting Protection Engines",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            SettingsSwitch(
                title = "Default Engine",
                subtitle = "Standard JusBrowse fingerprinting protection",
                checked = defaultEngineEnabled,
                onCheckedChange = { viewModel.setDefaultEngineEnabled(it) }
            )

            // 🎭 JusFake Engine Card (replaces old Fake Mode card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (jusFakeEngineEnabled) {
                            viewModel.setJusFakeEngineEnabled(context, false)
                        } else {
                            showFakeModeDialog = true
                        }
                    }
                    .padding(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (jusFakeEngineEnabled) 
                        Color(0xFF7C4DFF).copy(alpha = 0.1f) 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mask icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (jusFakeEngineEnabled) Color(0xFF7C4DFF) 
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎭", fontSize = 24.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "JusFake Engine",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        if (jusFakeEngineEnabled && currentPersona != null) {
                            Text(
                                text = "${currentPersona!!.flagEmoji} ${currentPersona!!.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF7C4DFF)
                            )
                        } else {
                            Text(
                                text = "Priv8 + RLEngine (Persona Based)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Switch(
                        checked = jusFakeEngineEnabled,
                        onCheckedChange = {
                            if (it) {
                                showFakeModeDialog = true
                            } else {
                                viewModel.setJusFakeEngineEnabled(context, false)
                            }
                        }
                    )
                }
            }
            
            val isFollianActive by viewModel.follianModeState.collectAsState()
            
            SettingsSwitch(
                title = "Follian Protocol (Ultra Stealth)",
                subtitle = if (isFollianActive)
                    "ACTIVE — Letterboxing ON, WebRTC/WebGL killed, JS spoofing disabled. Restart app to deactivate."
                else
                    "OFF — No letterboxing, standard protections. Restart app to activate.",
                checked = isFollianActive,
                onCheckedChange = { viewModel.setFollianModeEnabled(it) }
            )

            // 🛡️ Protection Whitelist
            var whitelistText by remember(protectionWhitelist) { mutableStateOf(protectionWhitelist) }
            
            OutlinedTextField(
                value = whitelistText,
                onValueChange = { whitelistText = it },
                label = { Text("Protection Whitelist") },
                placeholder = { Text("google.com, github.com") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Comma-separated domains to skip aggressive fingerprinting protection (Fixes broken logins)") },
                trailingIcon = {
                    if (whitelistText != protectionWhitelist) {
                        IconButton(onClick = { 
                            viewModel.setProtectionWhitelist(whitelistText)
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save Whitelist")
                        }
                    }
                }
            )

            SettingsSwitch(
                title = "Enable JavaScript",
                subtitle = "Allow sites to run JavaScript",
                checked = javascriptEnabled,
                onCheckedChange = { viewModel.setJavascriptEnabled(it) }
            )

            SettingsSwitch(
                title = "Ad Blocker",
                subtitle = "Block ads and trackers",
                checked = adBlockEnabled,
                onCheckedChange = { viewModel.setAdBlockEnabled(it) }
            )

            SettingsSwitch(
                title = "Advanced ADBlock",
                subtitle = "Inject CSP to block inline scripts and trackers (May break sites)",
                checked = advancedAdBlockEnabled,
                onCheckedChange = { viewModel.setAdvancedAdBlockEnabled(it) }
            )

            SettingsSwitch(
                title = "HTTPS Only Mode",
                subtitle = "Upgrade insecure connections to HTTPS",
                checked = httpsOnly,
                onCheckedChange = { viewModel.setHttpsOnly(it) }
            )

            SettingsSwitch(
                title = "Screenshot Protection",
                subtitle = "Prevent screenshots and hide content in recents",
                checked = flagSecureEnabled,
                onCheckedChange = { viewModel.setFlagSecureEnabled(it) }
            )

            SettingsSwitch(
                title = "Share Anonymous Analytics",
                subtitle = "Help improve JusBrowse by sharing basic installation and streak data. No URLs or fingerprinting data is sent.",
                checked = analyticsEnabled,
                onCheckedChange = { viewModel.setAnalyticsEnabled(it) }
            )

            SettingsSwitch(
                title = "Block Cookie Pop-ups",
                subtitle = "Hide annoying cookie consent banners",
                checked = cookieBlockerEnabled,
                onCheckedChange = { viewModel.setCookieBlockerEnabled(it) }
            )

            SettingsSwitch(
                title = "Popup Blocker",
                subtitle = "Block pop-ups and window.open() abuse",
                checked = popupBlockerEnabled,
                onCheckedChange = { viewModel.setPopupBlockerEnabled(it) }
            )

            SettingsSwitch(
                title = "Multi-Media Playback",
                subtitle = "Allow multiple tabs to play audio/video simultaneously",
                checked = multiMediaPlaybackEnabled,
                onCheckedChange = { viewModel.setMultiMediaPlaybackEnabled(it) }
            )

            // 🚫 Follian Mode - Hard JS Kill
            SettingsSwitch(
                title = "Follian Mode (JS Off)",
                subtitle = "⚠️ Hard JavaScript kill - sites WILL break",
                checked = follianMode,
                onCheckedChange = { viewModel.setFollianMode(it) }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // Appearance
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val themePreset by viewModel.themePreset.collectAsStateWithLifecycle(initialValue = "SYSTEM")

            Text(
                text = "Theme Preset",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(com.jusdots.jusbrowse.ui.theme.BrowserTheme.values().size) { index ->
                    val theme = com.jusdots.jusbrowse.ui.theme.BrowserTheme.values()[index]
                    ThemePreviewItem(
                        theme = theme,
                        isSelected = themePreset == theme.name,
                        wallColor = wallColorExtracted,
                        onClick = { viewModel.setThemePreset(theme.name) }
                    )
                }
            }


            SettingsSwitch(
                title = "Dark Mode",
                subtitle = "Use dark theme",
                checked = darkMode,
                onCheckedChange = { viewModel.setDarkMode(it) }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

            // Font Selection
            Text(
                text = "Application Font",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Choose a font style for the browser interface (Live Preview)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AppFont.values().size) { index ->
                    val font = AppFont.values()[index]
                    val isSelected = appFont == font.name
                    
                    Surface(
                        onClick = { viewModel.setAppFont(font.name) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .width(140.dp)
                            .height(100.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ag",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = font.fontFamily,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = font.displayName,
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = font.fontFamily,
                                    fontSize = 12.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (darkMode) {
                SettingsSwitch(
                    title = "Extra Dark Mode",
                    subtitle = "(Read the title bruh)",
                    checked = amoledBlackEnabled,
                    onCheckedChange = { viewModel.setAmoledBlackEnabled(it) }
                )
            }

            SettingsSwitch(
                title = "Show Tab Icons",
                subtitle = "Display website favicons instead of titles in tab bar",
                checked = showTabIcons,
                onCheckedChange = { viewModel.setShowTabIcons(it) }
            )

            HorizontalDivider()

            // Security API Keys
            Text(
                text = "Security API Keys (experimental)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Bring Your Own Key for zero-day malware protection. Your keys are stored only on this device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = vtApiKey,
                onValueChange = { viewModel.setVirusTotalApiKey(it) },
                label = { Text("VirusTotal API Key") },
                placeholder = { Text("Enter your VT key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = koodousApiKey,
                onValueChange = { viewModel.setKoodousApiKey(it) },
                label = { Text("Koodous API Key") },
                placeholder = { Text("Enter your Koodous key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = dnsUrlLocal,
                onValueChange = { 
                    dnsUrlLocal = it
                    viewModel.setCustomDohUrl(it) 
                },
                label = { Text("Custom DNS over HTTPS (DoH) URL") },
                placeholder = { Text("https://cloudflare-dns.com/dns-query") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("Leave empty for default (Google DoH). Note: Must support JSON formatting.") },
                trailingIcon = {
                    IconButton(onClick = { showDnsPresets = true }) {
                        Icon(Icons.Default.Language, contentDescription = "Presets")
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Made by JusDots Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo / Icon
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.ic_launcher_playstore) 
                            .crossfade(true)
                            .build(),
                        contentDescription = "JusBrowse Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "JusBrowse",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "Privacy-First Browsing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Made with ❤️ by JusDots",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Version 0.0.6-3",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                    )
                }
            }
            }
        }
    }

    if (showFakeModeDialog) {
        FakeModeDialog(
            onDismiss = { showFakeModeDialog = false },
            onEnable = { persona ->
                showFakeModeDialog = false
                viewModel.activateJusFakeEngine(context, persona)
            }
        )
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = null // Handled by parent toggleable
        )
    }
}

@Composable
fun ThemePreviewItem(
    theme: com.jusdots.jusbrowse.ui.theme.BrowserTheme,
    isSelected: Boolean,
    wallColor: Color? = null,
    onClick: () -> Unit
) {
    val color = when (theme) {
        BrowserTheme.VIVALDI_RED -> Color(0xFFD32F2F)
        BrowserTheme.OCEAN_BLUE -> Color(0xFF0288D1)
        BrowserTheme.FOREST_GREEN -> Color(0xFF388E3C)
        BrowserTheme.MIDNIGHT_PURPLE -> Color(0xFF7B1FA2)
        BrowserTheme.SUNSET_ORANGE -> Color(0xFFF57C00)
        BrowserTheme.ABYSS_BLACK -> Color(0xFF000000)
        BrowserTheme.NORD_ICE -> Color(0xFF5E81AC)
        BrowserTheme.DRACULA -> Color(0xFFBD93F9)
        BrowserTheme.SOLARIZED -> Color(0xFF268BD2)
        BrowserTheme.CYBERPUNK -> Color(0xFFFF00FF)
        BrowserTheme.MINT_FRESH -> Color(0xFF00BFA5)
        BrowserTheme.ROSE_GOLD -> Color(0xFFB76E79)
        BrowserTheme.SYSTEM -> MaterialTheme.colorScheme.primary
        BrowserTheme.MATERIAL_YOU -> Color(0xFF6750A4)
        BrowserTheme.WALL_THEME -> wallColor ?: Color(0xFF3F51B5)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(color)
                .then(
                    if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, androidx.compose.foundation.shape.CircleShape)
                    else Modifier
                )
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = theme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun BackgroundPresetCard(
    preset: BackgroundPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp, 60.dp)
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (isSelected) Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    )
                    else Modifier
                )
        ) {
            // Mini preview of the background
            com.jusdots.jusbrowse.ui.components.BackgroundRenderer(
                preset = preset,
                modifier = Modifier.fillMaxSize()
            )
            
            // Transparent overlay to capture clicks (WebView steals them otherwise)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onClick)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            CircleShape
                        )
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = preset.displayName,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
