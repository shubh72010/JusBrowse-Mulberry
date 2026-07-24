package com.jusdots.jusbrowse.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.jusdots.jusbrowse.ui.components.JusBrowseIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import com.jusdots.jusbrowse.ui.components.ColorPickerDialog
import com.jusdots.jusbrowse.ui.components.DnsPresets
import com.jusdots.jusbrowse.ui.components.DnsProvider
import com.jusdots.jusbrowse.data.models.Sticker
import com.jusdots.jusbrowse.ui.theme.AppFont
import com.jusdots.jusbrowse.ui.theme.BackgroundPreset
import com.jusdots.jusbrowse.ui.theme.BrowserTheme

import com.jusdots.jusbrowse.ui.theme.previewColor
import com.jusdots.jusbrowse.BuildConfig
import com.jusdots.jusbrowse.ui.viewmodel.BrowserViewModel
import com.jusdots.jusbrowse.R
import coil.compose.AsyncImage
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BrowserViewModel,
    onBack: () -> Unit
) {
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle(initialValue = "DuckDuckGo")
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle(initialValue = true)
    val flagSecureEnabled by viewModel.flagSecureEnabled.collectAsStateWithLifecycle(initialValue = true)
    val showTabIcons by viewModel.showTabIcons.collectAsStateWithLifecycle(initialValue = false)
    val vtApiKey by viewModel.virusTotalApiKey.collectAsStateWithLifecycle(initialValue = "")
    val koodousApiKey by viewModel.koodousApiKey.collectAsStateWithLifecycle(initialValue = "")
    val customDohUrl by viewModel.customDohUrl.collectAsStateWithLifecycle(initialValue = "")
    val customSearchEngineUrl by viewModel.customSearchEngineUrl.collectAsStateWithLifecycle(initialValue = "")
    val amoledBlackEnabled by viewModel.amoledBlackEnabled.collectAsStateWithLifecycle(initialValue = false)
    val appFont by viewModel.appFont.collectAsStateWithLifecycle(initialValue = "SYSTEM")
    val stickers = viewModel.stickers
    val protectionWhitelist by viewModel.protectionWhitelist.collectAsStateWithLifecycle(initialValue = "")
    val maxCacheSizeMB by viewModel.maxCacheSizeMB.collectAsStateWithLifecycle(initialValue = 1024)
    val themePreset by viewModel.themePreset.collectAsStateWithLifecycle(initialValue = "SYSTEM")
    val wallpaperUri by viewModel.startPageWallpaperUri.collectAsStateWithLifecycle(initialValue = null)
    val blurAmount by viewModel.startPageBlurAmount.collectAsStateWithLifecycle(initialValue = 0f)
    val customThemeColorHex by viewModel.customThemeColor.collectAsStateWithLifecycle(initialValue = "")
    val backgroundPreset by viewModel.backgroundPreset.collectAsStateWithLifecycle(initialValue = "NONE")
    val homePage by viewModel.homePage.collectAsStateWithLifecycle(initialValue = "about:blank")
    val alwaysShowUrl by viewModel.alwaysShowUrl.collectAsStateWithLifecycle(initialValue = true)
    val reduceAnim by viewModel.reducedAnimations.collectAsStateWithLifecycle(initialValue = false)
    val pillBottomMargin by viewModel.pillBottomMargin.collectAsStateWithLifecycle(initialValue = 90)
    val pillCollapsedWidth by viewModel.pillCollapsedWidth.collectAsStateWithLifecycle(initialValue = 260)
    val desktopMode by viewModel.globalDesktopMode.collectAsStateWithLifecycle(initialValue = false)
    val newTabPos by viewModel.newTabPosition.collectAsStateWithLifecycle(initialValue = "end")
    val tabChipHeight by viewModel.tabChipHeight.collectAsStateWithLifecycle(initialValue = "normal")
    val activeTabStyle by viewModel.activeTabStyle.collectAsStateWithLifecycle(initialValue = "gradient")
    val scrimDarkness by viewModel.scrimDarkness.collectAsStateWithLifecycle(initialValue = "normal")
    val showProgressBar by viewModel.showProgressBar.collectAsStateWithLifecycle(initialValue = true)
    val startPageBranding by viewModel.startPageBranding.collectAsStateWithLifecycle(initialValue = "full")
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle(initialValue = true)
    val pillBlurOpacity by viewModel.pillBlurOpacity.collectAsStateWithLifecycle(initialValue = 0.7f)
    val httpsOnly by viewModel.httpsOnly.collectAsStateWithLifecycle(initialValue = true)
    val cookieBlockerEnabled by viewModel.cookieBlockerEnabled.collectAsStateWithLifecycle(initialValue = true)
    val popupBlockerEnabled by viewModel.popupBlockerEnabled.collectAsStateWithLifecycle(initialValue = true)

    var editingSticker by remember { mutableStateOf<Sticker?>(null) }
    var stickerLinkText by remember { mutableStateOf("") }
    var showDnsPresets by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
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
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Select DNS Provider", style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary)
                }
                val groupedPresets = DnsPresets.providers.groupBy { it.category }
                groupedPresets.forEach { (category, providers) ->
                    item {
                        Text(category, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                    }
                    items(providers) { provider ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                dnsUrlLocal = provider.dohUrl
                                viewModel.setCustomDohUrl(provider.dohUrl)
                                showDnsPresets = false
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(provider.icon, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(provider.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    Text(provider.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(JusBrowseIcons.Check, null, tint = if (customDohUrl == provider.dohUrl) MaterialTheme.colorScheme.primary else Color.Transparent)
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
                        Icon(JusBrowseIcons.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                // ========== GENERAL ==========
                SettingsGroupHeader("General")

                var expanded by remember { mutableStateOf(false) }
                Text("Search Engine", style = MaterialTheme.typography.titleSmall)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = searchEngine, onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("DuckDuckGo", "Google", "Bing", "Brave", "Custom").forEach { engine ->
                            DropdownMenuItem(text = { Text(engine) }, onClick = { viewModel.setSearchEngine(engine); expanded = false })
                        }
                    }
                }
                AnimatedVisibility(visible = searchEngine.lowercase() == "custom") {
                    var customUrlText by remember(customSearchEngineUrl) { mutableStateOf(customSearchEngineUrl) }
                    OutlinedTextField(
                        value = customUrlText, onValueChange = { customUrlText = it },
                        label = { Text("Custom Search URL") }, placeholder = { Text("https://example.com/search?q=%s") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        trailingIcon = {
                            if (customUrlText != customSearchEngineUrl) {
                                IconButton(onClick = { viewModel.setCustomSearchEngineUrl(customUrlText.trim()) }) { Icon(JusBrowseIcons.Check, contentDescription = "Save") }
                            }
                        }
                    )
                }

                var homePageText by remember(homePage) { mutableStateOf(homePage) }
                OutlinedTextField(
                    value = homePageText, onValueChange = { homePageText = it },
                    label = { Text("Start Page") }, placeholder = { Text("https://example.com") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    trailingIcon = {
                        if (homePageText != homePage) {
                            IconButton(onClick = { viewModel.setHomePage(if (homePageText.isBlank()) "about:blank" else homePageText) }) { Icon(JusBrowseIcons.Check, contentDescription = "Save") }
                        }
                    }
                )

                // ========== APPEARANCE ==========t
                SettingsGroupHeader("Appearance")

                SettingsSwitch(title = "Dark Mode", checked = darkMode, onCheckedChange = { viewModel.setDarkMode(it) })
                if (darkMode) {
                    SettingsSwitch(title = "extra dark mode", checked = amoledBlackEnabled, onCheckedChange = { viewModel.setAmoledBlackEnabled(it) })
                }

                // Theme Preset
                var showColorPicker by remember { mutableStateOf(false) }
                val customThemeColor = remember(customThemeColorHex) {
                    if (customThemeColorHex.isNotBlank() && customThemeColorHex.startsWith("#")) {
                        try { Color(android.graphics.Color.parseColor(customThemeColorHex)) } catch (_: Exception) { Color(0xFF8B5CF6) }
                    } else Color(0xFF8B5CF6)
                }

                Text("Theme", style = MaterialTheme.typography.bodyLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(BrowserTheme.values().size) { index ->
                        val theme = BrowserTheme.values()[index]
                        ThemePreviewItem(
                            theme = theme,
                            isSelected = themePreset == theme.name,
                            customColor = customThemeColor,
                            onClick = {
                                if (theme == BrowserTheme.CUSTOM_COLOR) {
                                    showColorPicker = true
                                } else {
                                    viewModel.setThemePreset(theme.name)
                                }
                            }
                        )
                    }
                }

                if (showColorPicker) {
                    ColorPickerDialog(
                        initialColor = customThemeColor,
                        onColorSelected = { color ->
                            val r = (color.red * 255).toInt().coerceIn(0, 255)
                            val g = (color.green * 255).toInt().coerceIn(0, 255)
                            val b = (color.blue * 255).toInt().coerceIn(0, 255)
                            val hex = "#%02X%02X%02X".format(r, g, b)
                            viewModel.setCustomThemeColor(hex)
                            viewModel.setThemePreset(BrowserTheme.CUSTOM_COLOR.name)
                            showColorPicker = false
                        },
                        onDismiss = { showColorPicker = false }
                    )
                }

                SettingsSwitch(title = "Show Tab Icons", checked = showTabIcons, onCheckedChange = { viewModel.setShowTabIcons(it) })
                SettingsSwitch(title = "Always Show URL", checked = alwaysShowUrl, onCheckedChange = { viewModel.setAlwaysShowUrl(it) })
                SettingsSwitch(title = "Reduce Animations", checked = reduceAnim, onCheckedChange = { viewModel.setReducedAnimations(it) })
                Text("Pill Position", style = MaterialTheme.typography.bodyLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("60" to ("pill_low.webp" to "Low"), "90" to ("pill_default.webp" to "Default"), "120" to ("pill_high.webp" to "High"))) { (value, img) ->
                        val (file, label) = img
                        val isSelected = pillBottomMargin.toString() == value
                        Surface(
                            onClick = { viewModel.setPillBottomMargin(value.toInt()) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.width(120.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/buttons/Pill_Position/$file").crossfade(true).build(),
                                    contentDescription = label,
                                    modifier = Modifier.height(80.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                Text("Pill Width", style = MaterialTheme.typography.bodyLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("200" to ("pill_narrow.webp" to "Narrow"), "260" to ("pill_default_width.webp" to "Default"), "320" to ("pill_wide.webp" to "Wide"))) { (value, img) ->
                        val (file, label) = img
                        val isSelected = pillCollapsedWidth.toString() == value
                        Surface(
                            onClick = { viewModel.setPillCollapsedWidth(value.toInt()) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.width(120.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/buttons/Pill_Width/$file").crossfade(true).build(),
                                    contentDescription = label,
                                    modifier = Modifier.height(80.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                SettingsSwitch(title = "Desktop Mode", checked = desktopMode, onCheckedChange = { viewModel.setGlobalDesktopMode(it) })
                SettingsSelector(title = "New Tab Position", selected = newTabPos, options = listOf("end" to "End", "after_current" to "After current"), onSelect = { viewModel.setNewTabPosition(it) })
                Text("Tab Chip Height", style = MaterialTheme.typography.bodyLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("compact" to ("tab_chip_height_compact.webp" to "Compact"), "normal" to ("tab_chip_height_default.webp" to "Default"), "large" to ("tab_chip_height_large.webp" to "Large"))) { (value, img) ->
                        val (file, label) = img
                        val isSelected = tabChipHeight == value
                        Surface(
                            onClick = { viewModel.setTabChipHeight(value) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.width(120.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/buttons/Tab_Chip_Height/$file").crossfade(true).build(),
                                    contentDescription = label,
                                    modifier = Modifier.height(80.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                SettingsSelector(title = "Active Tab Style", selected = activeTabStyle, options = listOf("gradient" to "Gradient", "solid" to "Solid", "outline" to "Outline"), onSelect = { viewModel.setActiveTabStyle(it) })
                SettingsSelector(title = "Scrim Darkness", selected = scrimDarkness, options = listOf("light" to "Light", "normal" to "Normal", "dark" to "Dark"), onSelect = { viewModel.setScrimDarkness(it) })
                SettingsSwitch(title = "Progress Bar", checked = showProgressBar, onCheckedChange = { viewModel.setShowProgressBar(it) })
                SettingsSelector(title = "Start Page Branding", selected = startPageBranding, options = listOf("full" to "Full", "logo_only" to "Logo only", "clean" to "Clean"), onSelect = { viewModel.setStartPageBranding(it) })

                // Pill opacity
                Text("Pill Opacity", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = pillBlurOpacity, onValueChange = { viewModel.setPillBlurOpacity(it) },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )

                // Font
                Text("Font", style = MaterialTheme.typography.bodyLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AppFont.values().size) { index ->
                        val font = AppFont.values()[index]
                        val isSelected = appFont == font.name
                        Surface(
                            onClick = { viewModel.setAppFont(font.name) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier.width(120.dp).height(80.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Ag", style = androidx.compose.ui.text.TextStyle(fontFamily = font.fontFamily, fontSize = 24.sp, fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(font.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                    }
                }

                // Background Presets
                Text("Background", style = MaterialTheme.typography.bodyLarge)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(BackgroundPreset.values().size) { index ->
                        val preset = BackgroundPreset.values()[index]
                        BackgroundPresetCard(
                            preset = preset,
                            isSelected = backgroundPreset == preset.name,
                            onClick = { viewModel.setBackgroundPreset(preset.name) }
                        )
                    }
                }

                // Wallpaper
                WallpaperSection(viewModel, wallpaperUri, blurAmount)

                // ========== EXTENSIONS ==========
                SettingsGroupHeader("Extensions")
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateToScreen(com.jusdots.jusbrowse.ui.screens.Screen.EXTENSIONS) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(JusBrowseIcons.Extension, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Manage Extensions", style = MaterialTheme.typography.bodyLarge)
                            Text("Install and manage WebExtensions from addons.mozilla.org", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(JusBrowseIcons.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // ========== DNS ==========
                SettingsGroupHeader("DNS")
                OutlinedTextField(
                    value = dnsUrlLocal,
                    onValueChange = { dnsUrlLocal = it; viewModel.setCustomDohUrl(it) },
                    label = { Text("DoH URL") },
                    placeholder = { Text("https://cloudflare-dns.com/dns-query") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    trailingIcon = { IconButton(onClick = { showDnsPresets = true }) { Icon(JusBrowseIcons.Language, contentDescription = "Presets") } }
                )

                // ========== PRIVACY ==========
                SettingsGroupHeader("Privacy")
                SettingsSwitch(title = "Screenshot Protection", checked = flagSecureEnabled, onCheckedChange = { viewModel.setFlagSecureEnabled(it) })
                SettingsSwitch(title = "Ad Block", checked = adBlockEnabled, onCheckedChange = { viewModel.setAdBlockEnabled(it) })
                SettingsSwitch(title = "HTTPS-Only Mode", checked = httpsOnly, onCheckedChange = { viewModel.setHttpsOnly(it) })
                SettingsSwitch(title = "Cookie Blocker", checked = cookieBlockerEnabled, onCheckedChange = { viewModel.setCookieBlockerEnabled(it) })
                SettingsSwitch(title = "Popup Blocker", checked = popupBlockerEnabled, onCheckedChange = { viewModel.setPopupBlockerEnabled(it) })

                var whitelistText by remember(protectionWhitelist) { mutableStateOf(protectionWhitelist) }
                OutlinedTextField(
                    value = whitelistText, onValueChange = { whitelistText = it },
                    label = { Text("Protection Whitelist") }, placeholder = { Text("google.com, github.com") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (whitelistText != protectionWhitelist) {
                            IconButton(onClick = { viewModel.setProtectionWhitelist(whitelistText) }) { Icon(JusBrowseIcons.Check, contentDescription = "Save") }
                        }
                    }
                )

                OutlinedTextField(
                    value = vtApiKey, onValueChange = { viewModel.setVirusTotalApiKey(it) },
                    label = { Text("VirusTotal API Key") }, placeholder = { Text("VT key") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = koodousApiKey, onValueChange = { viewModel.setKoodousApiKey(it) },
                    label = { Text("Koodous API Key") }, placeholder = { Text("Koodous key") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )

                // ========== STORAGE ==========
                SettingsGroupHeader("Storage")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Disk Cache", style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = { viewModel.clearAllCache() }) { Text("Clear") }
                }
                Slider(
                    value = maxCacheSizeMB.toFloat(), onValueChange = { viewModel.setMaxCacheSizeMB(it.toInt()) },
                    valueRange = 20f..2500f,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )

                // Stickers
                StickersSection(viewModel, stickers, screenWidth, screenHeight, editingSticker, stickerLinkText,
                    onEditingStickerChange = { editingSticker = it },
                    onStickerLinkChange = { stickerLinkText = it })

                // ========== ABOUT ==========
                val updateState by viewModel.updateState.collectAsStateWithLifecycle()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(R.drawable.ic_launcher_playstore).crossfade(true).build(),
                            contentDescription = "JusBrowse Logo",
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("JusBrowse", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Privacy-First Browsing", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Made with ❤️ by JusDots", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        when (updateState) {
                            is BrowserViewModel.UpdateState.Available -> {
                                val info = (updateState as BrowserViewModel.UpdateState.Available).info
                                TextButton(onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                }) {
                                    Text("Update available: v${info.latestVersion}", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            is BrowserViewModel.UpdateState.UpToDate -> {
                                Text("Up to date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                            }
                            is BrowserViewModel.UpdateState.Checking -> {
                                Text("Checking...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                            }
                            is BrowserViewModel.UpdateState.Failed -> {
                                Text("Check failed. Tap to retry.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.clickable {
                                    viewModel.forceCheckForUpdates()
                                })
                            }
                            is BrowserViewModel.UpdateState.Idle -> {
                                Text("Check for updates", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f), modifier = Modifier.clickable {
                                    viewModel.forceCheckForUpdates()
                                })
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsGroupHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
}

@Composable
fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Switch)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
private fun SettingsSelector(
    title: String,
    selected: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == selected }?.second ?: selected
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedLabel, onValueChange = {}, readOnly = true,
            label = { Text(title) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label, fontWeight = if (key == selected) FontWeight.Bold else FontWeight.Normal) },
                    onClick = { onSelect(key); expanded = false },
                    trailingIcon = { if (key == selected) Icon(JusBrowseIcons.Check, null, tint = MaterialTheme.colorScheme.primary) }
                )
            }
        }
    }
}

@Composable
private fun WallpaperSection(
    viewModel: BrowserViewModel,
    wallpaperUri: String?,
    blurAmount: Float
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) { e.printStackTrace() }
            viewModel.setStartPageWallpaperUri(it.toString())
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Start Page Wallpaper", style = MaterialTheme.typography.titleSmall)
        if (wallpaperUri != null) {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(wallpaperUri).crossfade(true).build(),
                    contentDescription = "Wallpaper Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().blur(blurAmount.dp)
                )
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("JusBrowse", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(
                    onClick = { viewModel.setStartPageWallpaperUri(null) },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                ) { Icon(JusBrowseIcons.Delete, "Remove Wallpaper") }
            }
            Column {
                Text("Blur: ${blurAmount.toInt()}%", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = blurAmount,
                    onValueChange = { viewModel.setStartPageBlurAmount(it) },
                    valueRange = 0f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                )
            }
        } else {
            OutlinedButton(onClick = { launcher.launch(arrayOf("image/*", "video/*")) }, modifier = Modifier.fillMaxWidth()) {
                Icon(JusBrowseIcons.Image, null, modifier = Modifier.padding(end = 8.dp))
                Text("Select Wallpaper (Image/Video)")
            }
        }
    }
}

@Composable
private fun StickersSection(
    viewModel: BrowserViewModel,
    stickers: androidx.compose.runtime.snapshots.SnapshotStateList<com.jusdots.jusbrowse.data.models.Sticker>,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    editingSticker: com.jusdots.jusbrowse.data.models.Sticker?,
    stickerLinkText: String,
    onEditingStickerChange: (com.jusdots.jusbrowse.data.models.Sticker?) -> Unit,
    onStickerLinkChange: (String) -> Unit
) {
    val context = LocalContext.current
    val stickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) { e.printStackTrace() }
            viewModel.addSticker(it.toString())
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Stickers", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
        Text("Decorate your start page.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (stickers.isEmpty()) {
            Text("No stickers added yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 8.dp))
        } else {
            LazyRow(contentPadding = PaddingValues(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                items(count = stickers.size, key = { index -> stickers[index].id }) { index ->
                    val sticker = stickers[index]
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .pointerInput(sticker.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragEnd = { viewModel.saveStickers() },
                                    onDragCancel = { viewModel.saveStickers() },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val d = this@pointerInput.density
                                        val deltaX = dragAmount.x / (screenWidth.value * d)
                                        val deltaY = dragAmount.y / (screenHeight.value * d)
                                        viewModel.updateStickerTransform(sticker.id,
                                            (sticker.x + deltaX).coerceIn(0f, 1f),
                                            (sticker.y + deltaY).coerceIn(0f, 1f),
                                            sticker.widthDp, sticker.heightDp, sticker.rotation)
                                    }
                                )
                            }
                            .clickable { onEditingStickerChange(sticker); onStickerLinkChange(sticker.link ?: "") }
                        ) {
                            AsyncImage(model = sticker.imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            IconButton(onClick = { viewModel.removeSticker(sticker.id) }, modifier = Modifier.size(24.dp).align(Alignment.TopEnd).background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f), CircleShape)) {
                                Icon(JusBrowseIcons.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(if (sticker.link != null) "🔗 Link" else "No Link", style = MaterialTheme.typography.labelSmall, color = if (sticker.link != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
        Button(onClick = { stickerLauncher.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Icon(JusBrowseIcons.Add, null, modifier = Modifier.padding(end = 8.dp))
            Text("Add Sticker")
        }
    }

    if (editingSticker != null) {
        AlertDialog(
            onDismissRequest = { onEditingStickerChange(null) },
            title = { Text("Edit Sticker Link") },
            text = {
                Column {
                    Text("Enter a URL to open when this sticker is tapped.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = stickerLinkText, onValueChange = onStickerLinkChange, placeholder = { Text("https://example.com") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editingSticker?.let { st -> viewModel.updateStickerLink(st.id, if (stickerLinkText.isBlank()) null else stickerLinkText) }
                    onEditingStickerChange(null)
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { onEditingStickerChange(null) }) { Text("Cancel") } }
        )
    }
}

@Composable
fun ThemePreviewItem(
    theme: BrowserTheme,
    isSelected: Boolean,
    customColor: Color? = null,
    onClick: () -> Unit
) {
    val color = theme.previewColor(customColor = customColor)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(color).then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier)) {
            if (isSelected) { Icon(JusBrowseIcons.Check, "Selected", tint = Color.White, modifier = Modifier.align(Alignment.Center)) }
        }
        Text(theme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun BackgroundPresetCard(
    preset: BackgroundPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(72.dp, 52.dp).clip(RoundedCornerShape(8.dp)).then(
            if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)) else Modifier
        )) {
            com.jusdots.jusbrowse.ui.components.BackgroundRenderer(preset = preset, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClick))
            if (isSelected) {
                Icon(JusBrowseIcons.Check, "Selected", tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(2.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape).padding(2.dp).size(14.dp))
            }
        }
        Text(preset.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
