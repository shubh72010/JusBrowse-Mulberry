package com.jusdots.jusbrowse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import android.util.Log
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.jusdots.jusbrowse.ui.components.MediaData
import com.jusdots.jusbrowse.ui.components.MediaItem
import com.jusdots.jusbrowse.data.models.TrackerInfo
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jusdots.jusbrowse.BrowserApplication
import com.jusdots.jusbrowse.data.models.Bookmark
import com.jusdots.jusbrowse.data.models.BrowserTab
import com.jusdots.jusbrowse.data.models.HistoryItem
import com.jusdots.jusbrowse.data.models.TabDescriptor
import com.jusdots.jusbrowse.data.models.toBrowserTab
import com.jusdots.jusbrowse.data.models.toDescriptor
import com.jusdots.jusbrowse.data.repository.BookmarkRepository
import com.jusdots.jusbrowse.data.repository.HistoryRepository
import com.jusdots.jusbrowse.data.repository.DownloadRepository
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import com.jusdots.jusbrowse.data.repository.SiteSettingsRepository
import com.jusdots.jusbrowse.data.models.DownloadItem
import com.jusdots.jusbrowse.data.models.Shortcut
import com.jusdots.jusbrowse.data.models.Sticker
import com.jusdots.jusbrowse.ui.screens.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.jusdots.jusbrowse.lifecycle.TabLifecycleState
import com.jusdots.jusbrowse.security.ContentBlocker
import kotlinx.coroutines.*
import java.util.UUID
import android.content.Context
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.jusdots.jusbrowse.BuildConfig
import com.jusdots.jusbrowse.utils.UpdateChecker
import com.jusdots.jusbrowse.utils.UpdateInfo

data class TabWindowState(
    var x: Float = 0f,
    var y: Float = 0f,
    var scale: Float = 1f,
    var zIndex: Float = 0f
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    val strait = com.jusdots.jusbrowse.StraitArchitecture(application)

    private val database = BrowserApplication.database
    private val bookmarkRepository = BookmarkRepository(database.bookmarkDao())
    private val historyRepository = HistoryRepository(database.historyDao())
    private val downloadRepository = DownloadRepository(database.downloadDao())
    private val preferencesRepository = PreferencesRepository(application)
    val siteSettingsRepository = SiteSettingsRepository(database.siteSettingsDao())
    private val contentBlocker = ContentBlocker(application)

    // Layer 12 Memory Stabilization
    private val _passiveTabIds = mutableStateMapOf<String, Boolean>()
    val passiveTabIds: Map<String, Boolean> = _passiveTabIds
    private var lastMemoryCheckTime = 0L


    fun triggerMediaExtraction(tabId: String) {
        viewModelScope.launch {
            com.jusdots.jusbrowse.security.TrackerShieldBus.blockedTrackers.collect { (url, domain) ->
                val normalizedUrl = normalizeUrlForMapping(url)
                val tab = _tabDescriptors.find { normalizeUrlForMapping(it.url) == normalizedUrl }
                if (tab != null) {
                    recordBlockedTracker(tab.id, domain)
                } else if (_tabDescriptors.isNotEmpty()) {
                    val activeId = _activeTabId.value
                    if (activeId != null) {
                        recordBlockedTracker(activeId, domain)
                    }
                }
            }
        }
    }

        // Tab Window management
    val tabWindowStates = mutableStateMapOf<String, TabWindowState>()
    
    // Blocked trackers per tab (unique list for UI)
    val blockedTrackers = mutableStateMapOf<String, SnapshotStateList<TrackerInfo>>()
    
    // Total blocked hits per tab (cumulative count)
    val blockedTrackersCount = mutableStateMapOf<String, Int>()
    
    // Tab descriptors — lightweight, always in Compose slot table
    // Only the active tab has a full BrowserTab in activeTabState.
    private val _tabDescriptors = mutableStateListOf<TabDescriptor>()
    val tabDescriptors: SnapshotStateList<TabDescriptor> = _tabDescriptors

    // Active tab identity — which tab is currently foregrounded
    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()

    // Full active tab state — single BrowserTab, NOT a list. Only this one
    // participates in Compose snapshot invalidation for the content area.
    private val _activeTabState = MutableStateFlow<BrowserTab?>(null)
    val activeTabState: StateFlow<BrowserTab?> = _activeTabState.asStateFlow()

    // Derived: reconstruct full tab list for multi-view mode
    val allTabs: List<BrowserTab>
        get() {
            val active = _activeTabState.value
            return _tabDescriptors.map { desc ->
                if (active?.id == desc.id) active
                else desc.toBrowserTab()
            }
        }

    val tabs: List<BrowserTab>
        get() = allTabs

    val activeTabIndex: StateFlow<Int> = combine(
        snapshotFlow { _tabDescriptors.toList() },
        _activeTabId
    ) { descs, activeId ->
        if (activeId == null) 0
        else descs.indexOfFirst { it.id == activeId }.coerceAtLeast(0)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Tab Group State
    private val _activeGroupId = MutableStateFlow<String?>(null)
    val activeGroupId: StateFlow<String?> = _activeGroupId.asStateFlow()

    // Current global URL (mainly for single-view mode)
    private val _currentUrl = MutableStateFlow("about:blank")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    // UI Layout State (Global Gesture Coordination)
    private val _bottomBarOffsetHeightPx = MutableStateFlow(0f)
    val bottomBarOffsetHeightPx = _bottomBarOffsetHeightPx.asStateFlow()

    private val _revealBottomBarEvent = MutableSharedFlow<Unit>()
    val revealBottomBarEvent = _revealBottomBarEvent.asSharedFlow()

    private val _hideBottomBarEvent = MutableSharedFlow<Unit>()
    val hideBottomBarEvent = _hideBottomBarEvent.asSharedFlow()

    fun updateBottomBarOffset(offset: Float) {
        _bottomBarOffsetHeightPx.value = offset
    }

    fun triggerRevealBottomBar() {
        viewModelScope.launch {
            _revealBottomBarEvent.emit(Unit)
        }
    }

    fun triggerHideBottomBar() {
        viewModelScope.launch {
            _hideBottomBarEvent.emit(Unit)
        }
    }

    // GeckoView Session Pool
    val geckoSessionPool = java.util.concurrent.ConcurrentHashMap<String, org.mozilla.geckoview.GeckoSession>()

    // Session save debounce
    private var sessionSaveJob: Job? = null

    // Bookmarks, History & Downloads
    val bookmarks = bookmarkRepository.getAllBookmarks()
    val history = historyRepository.getAllHistory()
    val recentHistory = historyRepository.getRecentHistory(10)
    val downloads = downloadRepository.allDownloads

    // Cached preferences for synchronous access
    private var _cachedDesktopMode: Boolean = false
    private var _cachedNewTabPosition: String = "end"

    // Desktop Shortcuts
    val pinnedShortcuts: SnapshotStateList<Shortcut> = mutableStateListOf()

    // Stickers
    val stickers: SnapshotStateList<Sticker> = mutableStateListOf()
    private val _selectedStickerId = MutableStateFlow<String?>(null)
    val selectedStickerId = _selectedStickerId.asStateFlow()

    fun setSelectedStickerId(id: String?) {
        _selectedStickerId.value = id
    }

    // Preferences
    val searchEngine = preferencesRepository.searchEngine
    val homePage: StateFlow<String> = preferencesRepository.homePage.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, "about:blank")
    val javascriptEnabled = preferencesRepository.javascriptEnabled
    val darkMode = preferencesRepository.darkMode
    val adBlockEnabled = preferencesRepository.adBlockEnabled
    val httpsOnly = preferencesRepository.httpsOnly
    val flagSecureEnabled = preferencesRepository.flagSecureEnabled
    val cookieBlockerEnabled = preferencesRepository.cookieBlockerEnabled
    val popupBlockerEnabled = preferencesRepository.popupBlockerEnabled
    val showTabIcons = preferencesRepository.showTabIcons
    val themePreset = preferencesRepository.themePreset
    val customThemeColor = preferencesRepository.customThemeColor
    val virusTotalApiKey = preferencesRepository.virusTotalApiKey
    val koodousApiKey = preferencesRepository.koodousApiKey
    val amoledBlackEnabled = preferencesRepository.amoledBlackEnabled
    val startPageWallpaperUri = preferencesRepository.startPageWallpaperUri
    val startPageBlurAmount = preferencesRepository.startPageBlurAmount
    val backgroundPreset = preferencesRepository.backgroundPreset
    val customDohUrl: StateFlow<String> = preferencesRepository.customDohUrl.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, "")
    val customSearchEngineUrl: StateFlow<String> = preferencesRepository.customSearchEngineUrl.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, "")
    val protectionWhitelist = preferencesRepository.protectionWhitelist
    val maxCacheSizeMB = preferencesRepository.maxCacheSizeMB
    val multiMediaPlaybackEnabled = preferencesRepository.multiMediaPlaybackEnabled
    val appFont = preferencesRepository.appFont
    val browserMode = preferencesRepository.browserMode
    val uiVariant = preferencesRepository.uiVariant

    // Combined preference groups to reduce individual collectAsStateWithLifecycle calls.
    // Each group emits a single data class when any member changes, avoiding cascading
    // recomposition from individual flow emissions.

    data class BrowserUiPrefs(
        val showTabIcons: Boolean = false,
        val compactMode: Boolean = false,
        val alwaysShowUrl: Boolean = true,
        val reduceAnim: Boolean = false,
        val showProgressBar: Boolean = true,
        val startPageBranding: String = "full",
        val scrimDarkness: String = "normal",
        val pillBlurOpacity: Float = 0.7f,
        val browserMode: String = "strait"
    )

    data class BrowserLayoutPrefs(
        val pillBottomMargin: Int = 90,
        val pillCollapsedWidth: Int = 260,
        val tabChipHeight: String = "normal",
        val activeTabStyle: String = "gradient"
    )

    data class BrowserSearchPrefs(
        val searchEngine: String = "DuckDuckGo",
        val customSearchEngineUrl: String = ""
    )

    private val _baseUiPrefs: StateFlow<BrowserUiPrefs> = combine(
        preferencesRepository.showTabIcons,
        preferencesRepository.compactMode,
        preferencesRepository.alwaysShowUrl,
        preferencesRepository.reducedAnimations,
        preferencesRepository.showProgressBar
    ) { a, b, c, d, e ->
        BrowserUiPrefs(
            showTabIcons = a, compactMode = b, alwaysShowUrl = c,
            reduceAnim = d, showProgressBar = e
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BrowserUiPrefs())

    private val _brandingPrefs: StateFlow<BrowserUiPrefs> = combine(
        _baseUiPrefs,
        preferencesRepository.startPageBranding,
        preferencesRepository.scrimDarkness,
        preferencesRepository.browserMode
    ) { prefs, startPageBranding, scrimDarkness, browserMode ->
        prefs.copy(
            startPageBranding = startPageBranding,
            scrimDarkness = scrimDarkness,
            browserMode = browserMode
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BrowserUiPrefs())

    val browserUiPrefs: StateFlow<BrowserUiPrefs> = combine(
        _brandingPrefs,
        preferencesRepository.pillBlurOpacity
    ) { prefs, pillBlurOpacity ->
        prefs.copy(pillBlurOpacity = pillBlurOpacity)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BrowserUiPrefs())

    val browserLayoutPrefs: StateFlow<BrowserLayoutPrefs> = combine(
        preferencesRepository.pillBottomMargin,
        preferencesRepository.pillCollapsedWidth,
        preferencesRepository.tabChipHeight,
        preferencesRepository.activeTabStyle
    ) { v1, v2, v3, v4 ->
        BrowserLayoutPrefs(
            pillBottomMargin = v1,
            pillCollapsedWidth = v2,
            tabChipHeight = v3,
            activeTabStyle = v4
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BrowserLayoutPrefs())

    val browserSearchPrefs: StateFlow<BrowserSearchPrefs> = combine(
        preferencesRepository.searchEngine,
        preferencesRepository.customSearchEngineUrl
    ) { v1, v2 ->
        BrowserSearchPrefs(
            searchEngine = v1,
            customSearchEngineUrl = v2
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BrowserSearchPrefs())

    // WallTheme Color Extraction
    private val _extractedWallColor = MutableStateFlow<androidx.compose.ui.graphics.Color?>(null)
    val extractedWallColor: StateFlow<androidx.compose.ui.graphics.Color?> = _extractedWallColor.asStateFlow()

    private val _isStickerMode = MutableStateFlow(false)
    val isStickerMode: StateFlow<Boolean> = _isStickerMode.asStateFlow()

    private val _isBoomerMode = MutableStateFlow(false)
    val isBoomerMode: StateFlow<Boolean> = _isBoomerMode.asStateFlow()

    init {
        strait.initialize()
        viewModelScope.launch {
            // contentBlocker.initialize() removed: ContentBlocker is deprecated — the WebExtension
            // pipeline (background.js) handles all blocking. Parsing filter lists at startup
            // wasted cold-start time and RAM for rules that never reached the GeckoView pipeline.
            loadSession()
        }
        viewModelScope.launch {
            val uiRuntime = com.jusdots.jusbrowse.ui.runtime.StraitUIRuntime.getInstance(getApplication())
            uiRuntime.setUserReducedAnimations(preferencesRepository.reducedAnimations.first())
        }
        // Listen for Native Tracker Blocks from WebExtension
        viewModelScope.launch {
            com.jusdots.jusbrowse.security.TrackerShieldBus.blockedTrackers.collect { (url, domain) ->
                val normalizedUrl = normalizeUrlForMapping(url)
                val tab = _tabDescriptors.find { normalizeUrlForMapping(it.url) == normalizedUrl }
                if (tab != null) {
                    recordBlockedTracker(tab.id, domain)
                } else if (_tabDescriptors.isNotEmpty()) {
                    val activeId = _activeTabId.value
                    if (activeId != null) {
                        recordBlockedTracker(activeId, domain)
                    }
                }
            }
        }

        // Listen for Airlock Discovery from WebExtension
        viewModelScope.launch {
            com.jusdots.jusbrowse.security.AirlockDiscoveryBus.discoveryEvents.collect { data ->
                openAirlockGallery(data)
            }
        }

        // Watch for wallpaper changes to extract color
        viewModelScope.launch {
            startPageWallpaperUri.collect { uri ->
                if (uri != null) {
                    extractColorFromUri(uri)
                } else {
                    _extractedWallColor.value = null
                }
            }
        }
        
        viewModelScope.launch {
            customDohUrl.collect { url ->
                contentBlocker.customDohUrl = url
                if (url.isNotBlank()) {
                    com.jusdots.jusbrowse.BrowserApplication.runtime?.settings?.setTrustedRecursiveResolverUri(url)
                }
            }
        }

        viewModelScope.launch {
            preferencesRepository.globalDesktopMode.collect { _cachedDesktopMode = it }
        }
        viewModelScope.launch {
            preferencesRepository.javascriptEnabled.collect { enabled ->
                geckoSessionPool.values.forEach { session ->
                    session.settings.allowJavascript = enabled
                }
            }
        }
        viewModelScope.launch {
            // popupBlockerEnabled is handled via PromptDelegate.onPopupPrompt
            // at the per-session level, not via session.settings
            preferencesRepository.popupBlockerEnabled.collect { /* no-op: handled at PromptDelegate level */ }
        }
        viewModelScope.launch {
            preferencesRepository.httpsOnly.collect { enabled ->
                val runtime = com.jusdots.jusbrowse.BrowserApplication.runtime
                if (runtime != null) {
                    runtime.settings.allowInsecureConnections = if (enabled) {
                        org.mozilla.geckoview.GeckoRuntimeSettings.HTTPS_ONLY
                    } else {
                        org.mozilla.geckoview.GeckoRuntimeSettings.ALLOW_ALL
                    }
                }
            }
        }
        viewModelScope.launch {
            preferencesRepository.newTabPosition.collect { _cachedNewTabPosition = it }
        }

        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.initFollianModeCache()
        }
        viewModelScope.launch {
            preferencesRepository.follianMode.collect { preferencesRepository.updateFollianModeCache(it) }
        }

        // Initial memory check
        viewModelScope.launch { reapSessionsIfNeeded() }

        // Background update check — run once on init, non-blocking
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val info = UpdateChecker.check(BuildConfig.VERSION_NAME)
                if (info != null && info.isNewer) {
                    _updateState.value = UpdateState.Available(info)
                }
            } catch (_: Exception) { }
        }
    }

    private fun normalizeUrlForMapping(url: String): String {
        return try {
            val uri = android.net.Uri.parse(url)
            val host = uri.host?.removePrefix("www.") ?: ""
            val path = uri.path?.trimEnd('/') ?: ""
            "$host$path"
        } catch (e: Exception) {
            url
        }
    }

    fun toggleBoomerMode() {
        val newState = !_isBoomerMode.value
        _isBoomerMode.value = newState
        setBoomerModeEnabled(newState)
    }

    fun setProtectionWhitelist(whitelist: String) {
        viewModelScope.launch {
            preferencesRepository.setProtectionWhitelist(whitelist)
        }
    }

    fun toggleDomainWhitelist(domain: String) {
        viewModelScope.launch {
            val currentWhitelist = preferencesRepository.protectionWhitelist.first()
            val domains = currentWhitelist.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
            
            if (domains.contains(domain)) {
                domains.remove(domain)
            } else {
                domains.add(domain)
            }
            
            preferencesRepository.setProtectionWhitelist(domains.joinToString(","))
        }
    }

    fun setMaxCacheSizeMB(size: Int) {
        viewModelScope.launch {
            preferencesRepository.setMaxCacheSizeMB(size)
        }
    }

    fun clearAllCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val cacheDir = getApplication<Application>().cacheDir
            try {
                // Delete common webview cache locations
                // Cleanup gecko cache if initialized
                java.io.File(cacheDir, "gecko_cache").deleteRecursively()
            } catch (e: Exception) {
                Log.e("BrowserViewModel", "Failed to clear gecko cache", e)
            }
        }
    }

    val stickersEnabled = preferencesRepository.stickersEnabled



    // Multi-View Mode
    private val _isMultiViewMode = MutableStateFlow(false)
    val isMultiViewMode: StateFlow<Boolean> = _isMultiViewMode.asStateFlow()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(Screen.BROWSER)

    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    sealed interface UpdateState {
        data object Idle : UpdateState
        data object Checking : UpdateState
        data class Available(val info: UpdateInfo) : UpdateState
        data object UpToDate : UpdateState
        data object Failed : UpdateState
    }

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun dismissUpdateDialog() {
        _updateState.value = UpdateState.Idle
    }

    fun forceCheckForUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            _updateState.value = UpdateState.Checking
            try {
                val info = UpdateChecker.check(BuildConfig.VERSION_NAME)
                _updateState.value = when {
                    info == null -> UpdateState.Failed
                    info.isNewer -> UpdateState.Available(info)
                    else -> UpdateState.UpToDate
                }
            } catch (_: Exception) {
                _updateState.value = UpdateState.Failed
            }
        }
    }
    // Intent Handling
    private var isSessionLoaded = false
    private var pendingIntentUrl: String? = null

    fun handleIntentURL(url: String) {
        if (!isSessionLoaded) {
            pendingIntentUrl = url
        } else {
            // Check if URL is already open in any tab to avoid duplicates? 
            // For now, just open new tab for every external intent
            createNewTab(url)
        }
    }

    // Scanning State
    var showScanResultDialog by mutableStateOf(false)
    var scanResultMessage by mutableStateOf("")

    // Extension management
    var selectedExtension by mutableStateOf<com.jusdots.jusbrowse.data.models.ExtensionEntity?>(null)

    private suspend fun downloadToTempFile(url: String, context: Context): java.io.File? = withContext(Dispatchers.IO) {
        try {
            val file = java.io.File(context.cacheDir, "temp_scan_" + java.util.UUID.randomUUID().toString())
            
            val okHttpClient = com.jusdots.jusbrowse.security.NetworkSurgeon.getSharedClient()
            val requestBuilder = okhttp3.Request.Builder().url(url)
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            if (response.isSuccessful) {
                response.body?.byteStream()?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                return@withContext file
            }
        } catch (e: Exception) {
            Log.e("BrowserViewModel", "Failed to download temp file from $url", e)
        }
        null
    }

    fun scanFile(url: String, scannerType: String, context: Context) {
        viewModelScope.launch {
            scanResultMessage = "Downloading file for scanning..."
            showScanResultDialog = true
            
            val tempFile = downloadToTempFile(url, context)
            if (tempFile == null) {
                scanResultMessage = "Error: Failed to download file for scanning."
                return@launch
            }

            scanResultMessage = "Scanning with $scannerType..."
            
            val result = if (scannerType == "VirusTotal") {
                val apiKey = virusTotalApiKey.first() ?: ""
                com.jusdots.jusbrowse.security.ApiScanner.scanWithVirusTotal(tempFile, apiKey)
            } else {
                val apiKey = koodousApiKey.first() ?: ""
                com.jusdots.jusbrowse.security.ApiScanner.scanWithKoodous(tempFile, apiKey)
            }
            
            tempFile.delete()
            scanResultMessage = result
        }
    }

    // Airlock State (Global Overlays)
    var showAirlock by mutableStateOf(false)
    var airlockUrl by mutableStateOf("")
    var airlockMimeType by mutableStateOf("")
    
    var showGallery by mutableStateOf(false)
    var galleryMediaData by mutableStateOf<MediaData?>(null)
    var isVaulting by mutableStateOf(false)
    var vaultProgress by mutableStateOf(0f)
    

    
    val anomalies = mutableStateMapOf<String, SnapshotStateList<String>>()
    private val trackerUpdateBatch = mutableMapOf<String, MutableSet<String>>()
    private var trackerBatchJob: kotlinx.coroutines.Job? = null

    fun recordBlockedTracker(tabId: String, domain: String) {
        // Increment total cumulative count immediately
        val currentCount = blockedTrackersCount[tabId] ?: 0
        blockedTrackersCount[tabId] = currentCount + 1

        synchronized(trackerUpdateBatch) {
            trackerUpdateBatch.getOrPut(tabId) { mutableSetOf() }.add(domain)
        }
        
        // Start a debounced/throttled update job if not already running or scheduled
        if (trackerBatchJob == null || trackerBatchJob?.isCompleted == true) {
            trackerBatchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(1000) // Batch for 1 second
                val updates = synchronized(trackerUpdateBatch) {
                    val copy = trackerUpdateBatch.toMap()
                    trackerUpdateBatch.clear()
                    copy
                }
                
                updates.forEach { (tid, domains) ->
                    val list = blockedTrackers.getOrPut(tid) { mutableStateListOf() }
                    domains.forEach { dom ->
                        if (list.none { it.domain == dom }) {
                            list.add(0, TrackerInfo(dom))
                        }
                    }
                }
            }
        }
    }

    fun recordAnomaly(tabId: String, detail: String) {
        viewModelScope.launch {
            val list = anomalies.getOrPut(tabId) { mutableStateListOf() }
            list.add(0, detail)
        }
    }
    
    // Viewer State
    var viewerMediaList by mutableStateOf<List<MediaItem>>(emptyList())
    var viewerInitialIndex by mutableStateOf(0)
    
    fun openAirlockViewer(url: String, mimeType: String, list: List<MediaItem> = emptyList(), index: Int = 0) {
        airlockUrl = url
        airlockMimeType = mimeType
        viewerMediaList = list
        viewerInitialIndex = index
        showAirlock = true
    }
    
    fun openAirlockGallery(data: MediaData) {
        galleryMediaData = data
        showGallery = true // Show the UI immediately
        
        // Start isolation process in background
        viewModelScope.launch {
            isVaulting = true
            vaultProgress = 0f
            val context = getApplication<Application>()
            val vaultedData = com.jusdots.jusbrowse.utils.AirlockVaultManager.processAndVaultMedia(context, data) { current, total ->
                vaultProgress = current.toFloat() / total.toFloat()
            }
            galleryMediaData = vaultedData
            isVaulting = false
        }
    }
    
    fun closeAirlock() {
        showAirlock = false
        showGallery = false
    }



    private fun saveSession() {
        debouncedSaveSession()
    }

    private fun debouncedSaveSession() {
        sessionSaveJob?.cancel()
        sessionSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            val snapshot = allTabs
            val idx = _tabDescriptors.indexOfFirst { it.id == _activeTabId.value }.coerceAtLeast(0)
            strait.saveSession(snapshot, idx)
        }
    }

    private suspend fun loadSession() {
        val savedResult = withContext(Dispatchers.IO) { strait.loadSession() }

        if (savedResult != null) {
            val (loadedTabs, savedActiveIndex) = savedResult
            try {
                _tabDescriptors.clear()
                _activeTabState.value = null
                _activeTabId.value = null
                tabWindowStates.clear()

                val sanitizedTabs = loadedTabs.map { tab ->
                    val cid = try { tab.containerId } catch (e: Exception) { null }
                    if (cid == null) tab.copy(containerId = "default") else tab
                }

                if (sanitizedTabs.isNotEmpty()) {
                    val activeIdx = if (savedActiveIndex in sanitizedTabs.indices) savedActiveIndex else 0
                    val activeTab = sanitizedTabs[activeIdx]
                    _activeTabState.value = activeTab
                    _activeTabId.value = activeTab.id
                    _currentUrl.value = activeTab.url

                    sanitizedTabs.forEachIndexed { index, tab ->
                        if (index != activeIdx) {
                            _passiveTabIds[tab.id] = true
                        }
                    }
                    _tabDescriptors.addAll(sanitizedTabs.map { it.toDescriptor() })
                }

                isSessionLoaded = true
                pendingIntentUrl?.let { url ->
                    createNewTab(url)
                    pendingIntentUrl = null
                }
            } catch (e: Exception) {
                createNewTab()
            }
        }

        if (_tabDescriptors.isEmpty()) {
            createNewTab()
        }

        val workspace = withContext(Dispatchers.IO) { strait.loadWorkspace() }
        if (workspace != null) {
            pinnedShortcuts.clear()
            pinnedShortcuts.addAll(workspace.shortcuts)
            stickers.clear()
            stickers.addAll(workspace.stickers)
        }
    }

    private fun saveShortcuts() {
        viewModelScope.launch {
            strait.saveWorkspace(pinnedShortcuts.toList(), stickers.toList())
        }
    }

    override fun onCleared() {
        super.onCleared()
        sessionSaveJob?.cancel()
        strait.lifecycleManager.closeAllSessions()
        geckoSessionPool.values.forEach { it.close() }
        geckoSessionPool.clear()
        viewModelScope.launch { strait.forceFlush() }
    }


    // GeckoSession Management
    fun getGeckoSession(tabId: String): org.mozilla.geckoview.GeckoSession? {
        return geckoSessionPool[tabId]
    }

    suspend fun getOrCreateGeckoSession(tabId: String, isPrivate: Boolean = false, containerId: String? = null): org.mozilla.geckoview.GeckoSession {
        val existing = geckoSessionPool[tabId]
        if (existing != null) return existing

        // Preemptively reap BEFORE creating a new one to prevent concurrent process OOM
        reapSessionsIfNeeded(preemptive = 1)

        val jsEnabled = preferencesRepository.javascriptEnabled.first()
        val newSession = com.jusdots.jusbrowse.security.GeckoSessionFactory.createSession(isPrivate, containerId, jsEnabled)
        registerGeckoSession(tabId, newSession)
        return newSession
    }

    fun registerGeckoSession(tabId: String, session: org.mozilla.geckoview.GeckoSession) {
        geckoSessionPool[tabId] = session
        _passiveTabIds[tabId] = false
        val descriptor = _tabDescriptors.find { it.id == tabId }
        if (descriptor != null) {
            applyDesktopModeToSession(tabId)
            val activeState = _activeTabState.value
            val tab = if (activeState?.id == tabId) activeState else descriptor.toBrowserTab()
            val entry = strait.lifecycleManager.getEntry(tabId)
            if (entry?.state == TabLifecycleState.SERIALIZED) {
                strait.lifecycleManager.hydrateTab(tabId, session)
            } else {
                viewModelScope.launch { strait.registerTab(tab, session) }
            }
        }
        viewModelScope.launch { reapSessionsIfNeeded() }
    }

    private suspend fun reapSessionsIfNeeded(preemptive: Int = 0) {
        val now = System.currentTimeMillis()
        if (now - lastMemoryCheckTime < 3000 && preemptive == 0) return
        lastMemoryCheckTime = now

        strait.enforceMemoryBudget()

        val activeTabId = _activeTabId.value
        val activeSessionIds = strait.lifecycleManager.getActiveTabIds()

        val toReap = geckoSessionPool.keys.filter { id ->
            id != activeTabId && id !in activeSessionIds
        }
        if (toReap.isEmpty()) return

        val keepAtLeast = 1
        val reaperCount = toReap.size - (geckoSessionPool.size - toReap.size).coerceAtMost(keepAtLeast)
        val reapThese = if (reaperCount <= 0) emptyList() else toReap.take(reaperCount)

        reapThese.forEach { id ->
            strait.lifecycleManager.serializeTab(id)
            geckoSessionPool[id]?.close()
            geckoSessionPool.remove(id)
            _passiveTabIds[id] = true
        }
    }

    fun onTrimMemory(level: Int) {
        viewModelScope.launch {
            strait.onTrimMemory(level)
            if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
                val activeTabId = _activeTabId.value
                geckoSessionPool.keys.filter { it != activeTabId }.forEach { id ->
                    strait.lifecycleManager.serializeTab(id)
                    geckoSessionPool[id]?.close()
                    geckoSessionPool.remove(id)
                    _passiveTabIds[id] = true
                }
            }
        }
    }

    // Window Management
    fun updateWindowPosition(tabId: String, x: Float, y: Float) {
        tabWindowStates[tabId]?.let {
            it.x = x
            it.y = y
            debouncedSaveSession()
        }
    }

    fun updateWindowScale(tabId: String, scale: Float) {
        tabWindowStates[tabId]?.let {
            // Clamp scale
            it.scale = scale.coerceIn(0.5f, 3.0f)
            debouncedSaveSession()
        }
    }

    fun bringToFront(tabId: String) {
        // Find max Z
        val maxZ = tabWindowStates.values.maxOfOrNull { it.zIndex } ?: 0f
        tabWindowStates[tabId]?.zIndex = maxZ + 1f
    }

    fun navigateToScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    fun createNewTab(url: String = "about:blank", isPrivate: Boolean = false, containerId: String = "default", select: Boolean = true) {
        val finalUrl = if (url == "about:blank" && homePage.value != "about:blank") homePage.value else url
        val newTabId = UUID.randomUUID().toString()
        val isDesktop = _cachedDesktopMode
        val newTab = BrowserTab(
            id = newTabId,
            url = finalUrl,
            isPrivate = isPrivate,
            containerId = containerId,
            isDesktopMode = isDesktop
        )
        val descriptor = newTab.toDescriptor()
        val insertIndex = if (_cachedNewTabPosition == "after_current") {
            val currentIdx = _tabDescriptors.indexOfFirst { it.id == _activeTabId.value }
            if (currentIdx >= 0) currentIdx + 1 else _tabDescriptors.size
        } else {
            _tabDescriptors.size
        }
        if (insertIndex in 0.._tabDescriptors.size) {
            _tabDescriptors.add(insertIndex, descriptor)
        } else {
            _tabDescriptors.add(descriptor)
        }

        val offset = (_tabDescriptors.size * 20).toFloat()
        tabWindowStates[newTabId] = TabWindowState(
            x = offset,
            y = offset,
            zIndex = (_tabDescriptors.size).toFloat()
        )

        if (select) {
            _activeTabId.value = newTabId
            _activeTabState.value = newTab
            _currentUrl.value = url
        }
        saveSession()

        applyDesktopModeToSession(newTabId)
    }

    // Tab Group Operations
    fun openTabGroup(groupId: String?) {
        _activeGroupId.value = groupId
    }

    fun groupTabs(draggedTabId: String, targetTabId: String) {
        val currentTabs = allTabs
        val draggedIndex = currentTabs.indexOfFirst { it.id == draggedTabId }
        val targetIndex = currentTabs.indexOfFirst { it.id == targetTabId }
        
        if (draggedIndex != -1 && targetIndex != -1 && draggedTabId != targetTabId) {
            val targetTab = currentTabs[targetIndex]
            val draggedTab = currentTabs[draggedIndex]
            
            val groupIdToUse = if (targetTab.isGroupMaster) {
                targetTab.id
            } else {
                val updatedTarget = targetTab.copy(isGroupMaster = true)
                val targetDescIdx = _tabDescriptors.indexOfFirst { it.id == targetTabId }
                if (targetDescIdx >= 0) {
                    _tabDescriptors[targetDescIdx] = updatedTarget.toDescriptor()
                }
                if (_activeTabId.value == targetTabId) {
                    _activeTabState.value = updatedTarget
                }
                targetTab.id
            }
            
            val updatedDragged = draggedTab.copy(parentGroupId = groupIdToUse)
            val draggedDescIdx = _tabDescriptors.indexOfFirst { it.id == draggedTabId }
            if (draggedDescIdx >= 0) {
                _tabDescriptors[draggedDescIdx] = updatedDragged.toDescriptor()
            }
            if (_activeTabId.value == draggedTabId) {
                _activeTabState.value = updatedDragged
            }
            
            saveSession()
        }
    }

    fun ungroupTab(tabId: String) {
        val index = _tabDescriptors.indexOfFirst { it.id == tabId }
        if (index != -1) {
            val descriptor = _tabDescriptors[index]
            val parentId = descriptor.parentGroupId
            val updatedDesc = descriptor.copy(parentGroupId = null)
            _tabDescriptors[index] = updatedDesc
            if (_activeTabId.value == tabId && _activeTabState.value != null) {
                _activeTabState.value = _activeTabState.value?.copy(parentGroupId = null)
            }

            if (parentId != null) {
                val remainingChildren = _tabDescriptors.count { it.parentGroupId == parentId }
                if (remainingChildren == 0) {
                    val parentIndex = _tabDescriptors.indexOfFirst { it.id == parentId }
                    if (parentIndex != -1) {
                        val parentDesc = _tabDescriptors[parentIndex]
                        _tabDescriptors[parentIndex] = parentDesc.copy(isGroupMaster = false)
                        if (_activeTabId.value == parentId && _activeTabState.value != null) {
                            _activeTabState.value = _activeTabState.value?.copy(isGroupMaster = false)
                        }
                    }
                    if (_activeGroupId.value == parentId) {
                        _activeGroupId.value = null
                    }
                }
            }

            saveSession()
        }
    }

    fun switchTab(index: Int) {
        if (index in _tabDescriptors.indices) {
            val descriptor = _tabDescriptors[index]
            val tabId = descriptor.id
            _activeTabId.value = tabId
            _activeTabState.value = descriptor.toBrowserTab()
            _currentUrl.value = descriptor.url

            if (_passiveTabIds[tabId] == true) {
                _passiveTabIds[tabId] = false
            }

            bringToFront(tabId)

            viewModelScope.launch {
                strait.switchTab(tabId)
                reapSessionsIfNeeded()
                saveSession()
            }
        }
    }

    fun closeTab(index: Int) {
        if (_tabDescriptors.size > 1 && index in _tabDescriptors.indices) {
            val tabToRemove = _tabDescriptors[index]
            val tabId = tabToRemove.id

            if (tabToRemove.isGroupMaster) {
                val children = _tabDescriptors.filter { it.parentGroupId == tabToRemove.id }
                children.forEach { child ->
                    val childIndex = _tabDescriptors.indexOfFirst { it.id == child.id }
                    if (childIndex != -1) {
                        _tabDescriptors[childIndex] = child.copy(parentGroupId = null)
                        if (_activeTabId.value == child.id && _activeTabState.value != null) {
                            _activeTabState.value = _activeTabState.value?.copy(parentGroupId = null)
                        }
                    }
                }

                if (_activeGroupId.value == tabToRemove.id) {
                    _activeGroupId.value = null
                }
            }

            val session = geckoSessionPool[tabId]
            val isFollianActive = preferencesRepository.follianModeCached
            if (isFollianActive) {
                com.jusdots.jusbrowse.BrowserApplication.runtime?.storageController?.clearData(org.mozilla.geckoview.StorageController.ClearFlags.ALL)
            }
            session?.close()
            geckoSessionPool.remove(tabId)

            tabWindowStates.remove(tabId)
            blockedTrackers.remove(tabId)
            blockedTrackersCount.remove(tabId)
            anomalies.remove(tabId)
            _passiveTabIds.remove(tabId)
            _tabDescriptors.removeAt(index)

            val activeRemoved = _activeTabId.value == tabId
            if (activeRemoved) {
                val newActiveIndex = index.coerceAtMost(_tabDescriptors.lastIndex)
                val newActiveDesc = _tabDescriptors.getOrNull(newActiveIndex)
                if (newActiveDesc != null) {
                    _activeTabId.value = newActiveDesc.id
                    _activeTabState.value = newActiveDesc.toBrowserTab()
                    _currentUrl.value = newActiveDesc.url
                }
            }

            viewModelScope.launch {
                strait.closeTab(tabId)
                saveSession()
            }
        } else if (_tabDescriptors.size == 1) {
            val oldTabId = _tabDescriptors[0].id
            geckoSessionPool[oldTabId]?.close()
            geckoSessionPool.remove(oldTabId)

            tabWindowStates.remove(oldTabId)
            blockedTrackers.remove(oldTabId)
            blockedTrackersCount.remove(oldTabId)
            anomalies.remove(oldTabId)
            _passiveTabIds.remove(oldTabId)

            val newId = UUID.randomUUID().toString()
            val newDesc = TabDescriptor(
                id = newId,
                url = "about:blank",
                title = "",
                favicon = null,
                isPrivate = false,
                containerId = "default",
                isDesktopMode = false,
                parentGroupId = null,
                isGroupMaster = false
            )
            _tabDescriptors[0] = newDesc
            _activeTabId.value = newId
            _activeTabState.value = newDesc.toBrowserTab()
            tabWindowStates[newId] = TabWindowState()
            _currentUrl.value = "about:blank"

            viewModelScope.launch {
                strait.closeTab(oldTabId)
                saveSession()
            }
        }
    }

    fun closeAllTabs() {
        strait.closeAllTabs()
        geckoSessionPool.values.forEach { it.close() }
        geckoSessionPool.clear()

        tabWindowStates.clear()
        blockedTrackers.clear()
        blockedTrackersCount.clear()
        anomalies.clear()
        _passiveTabIds.clear()
        _tabDescriptors.clear()
        _activeTabState.value = null
        _activeTabId.value = null

        createNewTab()
        saveSession()
    }

    fun updateTab(index: Int, updatedTab: BrowserTab) {
        if (index in _tabDescriptors.indices) {
            val oldDescriptor = _tabDescriptors[index]
            _tabDescriptors[index] = updatedTab.toDescriptor()
            if (_activeTabId.value == updatedTab.id) {
                _activeTabState.value = updatedTab
                _currentUrl.value = updatedTab.url
            }
            if (oldDescriptor.url != updatedTab.url || oldDescriptor.title != updatedTab.title) {
                saveSession()
            }
        }
    }

    // Navigation
    // DEPRECATED: Uses active tab. Use navigateToUrl(tabId, url) instead for multi-window correctness.
    fun navigateToUrl(url: String) {
        val activeId = _activeTabId.value
        if (activeId != null) {
            val index = _tabDescriptors.indexOfFirst { it.id == activeId }
            if (index >= 0) navigateToUrlForIndex(index, url)
        }
    }

    fun navigateToUrlForIndex(index: Int, url: String) {
        val normalizedUrl = normalizeUrl(url)

        if (index in _tabDescriptors.indices) {
            val descriptor = _tabDescriptors[index]
            blockedTrackers.remove(descriptor.id)

            val updatedDesktop = if (descriptor.isDesktopMode) {
                applyDesktopModeToSession(descriptor.id)
                descriptor
            } else descriptor

            val updatedDesc = updatedDesktop.copy(url = normalizedUrl)
            _tabDescriptors[index] = updatedDesc
            if (_activeTabId.value == descriptor.id) {
                _activeTabState.value = _activeTabState.value?.copy(url = normalizedUrl)
                _currentUrl.value = normalizedUrl
            }

            if (!descriptor.isPrivate) {
                viewModelScope.launch {
                    historyRepository.addToHistory(
                        title = normalizedUrl,
                        url = normalizedUrl
                    )
                }
            }

            viewModelScope.launch {
                applySiteSettingsToSession(descriptor.id, normalizedUrl)
            }
        }
    }

    private suspend fun applySiteSettingsToSession(tabId: String, url: String) {
        if (url == "about:blank" || !url.startsWith("http")) return
        val origin = try {
            val uri = android.net.Uri.parse(url)
            "${uri.scheme}://${uri.host}"
        } catch (_: Exception) { return }

        val settings = siteSettingsRepository.getSettingsForOrigin(origin).first() ?: return
        val session = geckoSessionPool[tabId] ?: return

        session.settings.allowJavascript = settings.javascriptEnabled
    }
    
    fun navigateToUrlByTabId(tabId: String, url: String) {
        val index = _tabDescriptors.indexOfFirst { it.id == tabId }
        if (index != -1) {
            navigateToUrlForIndex(index, url)
        }
    }

    fun updateTabTitle(index: Int, title: String) {
        if (index in _tabDescriptors.indices) {
            val descriptor = _tabDescriptors[index]
            val updatedDesc = descriptor.copy(title = title)
            _tabDescriptors[index] = updatedDesc
            if (_activeTabId.value == descriptor.id) {
                _activeTabState.value = _activeTabState.value?.copy(title = title)
            }

            if (!descriptor.isPrivate) {
                viewModelScope.launch {
                    historyRepository.updateHistoryTitle(descriptor.url, title)
                }
            }
        }
    }

    fun updateTabFavicon(index: Int, faviconUrl: String) {
        if (index in _tabDescriptors.indices) {
            val descriptor = _tabDescriptors[index]
            val updatedDesc = descriptor.copy(favicon = faviconUrl)
            _tabDescriptors[index] = updatedDesc
            if (_activeTabId.value == descriptor.id) {
                _activeTabState.value = _activeTabState.value?.copy(favicon = faviconUrl)
            }
        }
    }

    fun updateTabLoadingState(index: Int, isLoading: Boolean, progress: Float = 0f) {
        if (index in _tabDescriptors.indices) {
            val descriptor = _tabDescriptors[index]
            if (_activeTabId.value == descriptor.id) {
                _activeTabState.value = _activeTabState.value?.copy(
                    isLoading = isLoading,
                    progress = progress
                )
            }
        }
    }

    fun updateTabNavigationState(index: Int, canGoBack: Boolean, canGoForward: Boolean) {
        if (index in _tabDescriptors.indices) {
            val descriptor = _tabDescriptors[index]
            if (_activeTabId.value == descriptor.id) {
                _activeTabState.value = _activeTabState.value?.copy(
                    canGoBack = canGoBack,
                    canGoForward = canGoForward
                )
            }
        }
    }

    private fun applyDesktopModeToSession(tabId: String) {
        val descriptor = _tabDescriptors.find { it.id == tabId }
        val session = geckoSessionPool[tabId]
        if (descriptor != null && session != null) {
            session.settings.userAgentOverride = if (descriptor.isDesktopMode) DESKTOP_USER_AGENT else null
        }
    }

    fun toggleDesktopMode(tabId: String) {
        val index = _tabDescriptors.indexOfFirst { it.id == tabId }
        if (index != -1) {
            val descriptor = _tabDescriptors[index]
            val newMode = !descriptor.isDesktopMode
            _tabDescriptors[index] = descriptor.copy(isDesktopMode = newMode)
            if (_activeTabId.value == tabId) {
                _activeTabState.value = _activeTabState.value?.copy(isDesktopMode = newMode)
            }

            applyDesktopModeToSession(tabId)
            val session = geckoSessionPool[tabId]
            if (session != null) {
                session.reload()
            }
            debouncedSaveSession()
        }
    }

    private fun normalizeUrl(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://") && url != "about:blank") {
            "https://$url"
        } else {
            url
        }
    }

    fun isUrlQuery(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return true

        // 1. Explicit protocol
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || 
            trimmed.startsWith("file://") || trimmed.startsWith("about:")) {
            return false
        }

        // 2. Contains spaces -> definitely search
        if (trimmed.contains(" ")) return true

        // 3. Common TLDs check or localhost
        val commonTlds = listOf(".com", ".net", ".org", ".io", ".gov", ".edu", ".dev", ".me", ".info", ".biz", ".top")
        if (commonTlds.any { trimmed.lowercase().endsWith(it) } || 
            trimmed.lowercase() == "localhost" || 
            trimmed.contains("localhost:")) {
            return false
        }

        // 4. IP Address check (simple)
        val ipRegex = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d+)?.*$".toRegex()
        if (ipRegex.matches(trimmed)) return false

        // Default: Search Query
        return true
    }

    fun getSearchUrl(query: String, engine: String = "DuckDuckGo", customUrl: String = ""): String {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        return when (engine.lowercase()) {
            "google" -> "https://www.google.com/search?q=$encodedQuery"
            "bing" -> "https://www.bing.com/search?q=$encodedQuery"
            "brave" -> "https://search.brave.com/search?q=$encodedQuery"
            "custom" -> {
                if (customUrl.isBlank()) {
                    "https://duckduckgo.com/?q=$encodedQuery"
                } else if (customUrl.contains("%s")) {
                    customUrl.replace("%s", encodedQuery)
                } else {
                    // Append query if %s is not present, ensuring proper URL joining
                    val separator = if (customUrl.contains("?")) {
                        if (customUrl.endsWith("?") || customUrl.endsWith("&")) "" else "&"
                    } else "?"
                    "$customUrl${separator}q=$encodedQuery"
                }
            }
            else -> "https://duckduckgo.com/?q=$encodedQuery"
        }
    }

    // Bookmarks
    fun addBookmark(title: String, url: String) {
        viewModelScope.launch {
            bookmarkRepository.addBookmark(title, url)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmark)
        }
    }

    suspend fun isBookmarked(url: String): Boolean {
        return bookmarkRepository.isBookmarked(url)
    }

    // History
    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
        }
    }

    fun deleteHistoryItem(historyItem: HistoryItem) {
        viewModelScope.launch {
            historyRepository.deleteHistory(historyItem)
        }
    }

    // Preferences
    fun setSearchEngine(engine: String) {
        viewModelScope.launch {
            preferencesRepository.setSearchEngine(engine)
        }
    }

    fun setCustomSearchEngineUrl(url: String) {
        viewModelScope.launch {
            preferencesRepository.setCustomSearchEngineUrl(url)
        }
    }

    fun setHomePage(homePage: String) {
        viewModelScope.launch {
            preferencesRepository.setHomePage(homePage)
        }
    }

    fun setStartPageWallpaperUri(uri: String?) {
        viewModelScope.launch {
            preferencesRepository.setStartPageWallpaperUri(uri)
            if (uri != null) {
                preferencesRepository.setBackgroundPreset("NONE")
            }
        }
    }

    private fun extractColorFromUri(uriString: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val uri = android.net.Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inSampleSize = 8 // Downsample for speed
                }
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                if (bitmap != null) {
                    // Extract color from center area or average
                    // Using center 10x10 area average
                    val centerX = bitmap.width / 2
                    val centerY = bitmap.height / 2
                    
                    var r = 0L
                    var g = 0L
                    var b = 0L
                    var count = 0
                    
                    for (x in (centerX - 5).coerceAtLeast(0) until (centerX + 5).coerceAtMost(bitmap.width)) {
                        for (y in (centerY - 5).coerceAtLeast(0) until (centerY + 5).coerceAtMost(bitmap.height)) {
                            val pixel = bitmap.getPixel(x, y)
                            r += android.graphics.Color.red(pixel)
                            g += android.graphics.Color.green(pixel)
                            b += android.graphics.Color.blue(pixel)
                            count++
                        }
                    }
                    
                    if (count > 0) {
                        val finalColor = android.graphics.Color.rgb((r/count).toInt(), (g/count).toInt(), (b/count).toInt())
                        _extractedWallColor.value = androidx.compose.ui.graphics.Color(finalColor)
                    }
                    bitmap.recycle()
                }
            } catch (e: Exception) {
                Log.e("BrowserViewModel", "Failed to extract wallpaper color", e)
            }
        }
    }

    fun setStartPageBlurAmount(amount: Float) {
        viewModelScope.launch {
            preferencesRepository.setStartPageBlurAmount(amount)
        }
    }

    fun setBackgroundPreset(preset: String) {
        viewModelScope.launch {
            preferencesRepository.setBackgroundPreset(preset)
            if (preset != "NONE") {
                preferencesRepository.setStartPageWallpaperUri(null)
            }
        }
    }

    fun setJavascriptEnabled(enabled: Boolean) {
        geckoSessionPool.values.forEach { session ->
            session.settings.allowJavascript = enabled
        }
        viewModelScope.launch {
            preferencesRepository.setJavascriptEnabled(enabled)
        }
    }

    fun setHttpsOnly(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setHttpsOnly(enabled)
        }
    }

    fun setCookieBlockerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setCookieBlockerEnabled(enabled)
        }
    }

    fun setPopupBlockerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPopupBlockerEnabled(enabled)
        }
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAdBlockEnabled(enabled)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkMode(enabled)
        }
    }

    fun setFlagSecureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFlagSecureEnabled(enabled)
        }
    }

    fun setMultiMediaPlaybackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setMultiMediaPlaybackEnabled(enabled)
        }
    }

    fun setAppFont(font: String) {
        viewModelScope.launch {
            preferencesRepository.setAppFont(font)
        }
    }

    fun setShowTabIcons(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowTabIcons(enabled)
        }
    }

    fun setThemePreset(preset: String) {
        viewModelScope.launch {
            preferencesRepository.setThemePreset(preset)
        }
    }

    fun setCustomThemeColor(colorHex: String) {
        viewModelScope.launch {
            preferencesRepository.setCustomThemeColor(colorHex)
        }
    }

    fun setVirusTotalApiKey(key: String) {
        preferencesRepository.setVirusTotalApiKey(key)
    }

    fun setCustomDohUrl(url: String) {
        viewModelScope.launch {
            preferencesRepository.setCustomDohUrl(url)
        }
    }

    fun setKoodousApiKey(key: String) {
        preferencesRepository.setKoodousApiKey(key)
    }

    val follianMode = preferencesRepository.follianMode
    val toolbarPosition = preferencesRepository.toolbarPosition
    val compactMode = preferencesRepository.compactMode
    val addressBarStyle = preferencesRepository.addressBarStyle

    fun setFollianMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFollianMode(enabled)
        }
    }

    fun setToolbarPosition(position: String) {
        viewModelScope.launch {
            preferencesRepository.setToolbarPosition(position)
        }
    }

    fun setCompactMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setCompactMode(enabled)
        }
    }

    fun setAddressBarStyle(style: String) {
        viewModelScope.launch {
            preferencesRepository.setAddressBarStyle(style)
        }
    }

    fun setAmoledBlackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAmoledBlackEnabled(enabled)
        }
    }

    fun setStickersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setStickersEnabled(enabled)
        }
    }

    // Site Settings
    fun updateSiteSettings(settings: com.jusdots.jusbrowse.data.models.SiteSettings) {
        viewModelScope.launch {
            siteSettingsRepository.updateSettings(settings)
        }
    }

    fun getSiteSettings(url: String): kotlinx.coroutines.flow.Flow<com.jusdots.jusbrowse.data.models.SiteSettings?> {
        val origin = try {
            val uri = android.net.Uri.parse(url)
            "${uri.scheme}://${uri.host}"
        } catch (e: Exception) {
            url
        }
        return siteSettingsRepository.getSettingsForOrigin(origin)
    }

    // Multi-View Mode
    fun toggleMultiViewMode() {
        if (_tabDescriptors.size >= 1) {
            _isMultiViewMode.value = !_isMultiViewMode.value
            viewModelScope.launch {
                preferencesRepository.setMultiViewMode(_isMultiViewMode.value)
            }
        }
    }

    // Sticker Mode
    fun toggleStickerMode() {
        _isStickerMode.value = !_isStickerMode.value
    }


    val boomerModeEnabled = preferencesRepository.boomerModeEnabled
    
    fun setBoomerModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setBoomerModeEnabled(enabled)
        }
    }

    val alwaysShowUrl = preferencesRepository.alwaysShowUrl
    val reducedAnimations = preferencesRepository.reducedAnimations
    val pillBottomMargin = preferencesRepository.pillBottomMargin
    val pillCollapsedWidth = preferencesRepository.pillCollapsedWidth
    val globalDesktopMode = preferencesRepository.globalDesktopMode
    val newTabPosition = preferencesRepository.newTabPosition
    val tabChipHeight = preferencesRepository.tabChipHeight
    val activeTabStyle = preferencesRepository.activeTabStyle
    val scrimDarkness = preferencesRepository.scrimDarkness
    val showProgressBar = preferencesRepository.showProgressBar
    val startPageBranding = preferencesRepository.startPageBranding
    val pillBlurOpacity = preferencesRepository.pillBlurOpacity

    fun setAlwaysShowUrl(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setAlwaysShowUrl(enabled) }
    }

    fun setReducedAnimations(enabled: Boolean) {
        com.jusdots.jusbrowse.ui.runtime.StraitUIRuntime.getInstance(getApplication()).setUserReducedAnimations(enabled)
        viewModelScope.launch { preferencesRepository.setReducedAnimations(enabled) }
    }

    fun setPillBottomMargin(margin: Int) {
        viewModelScope.launch { preferencesRepository.setPillBottomMargin(margin) }
    }

    fun setPillCollapsedWidth(width: Int) {
        viewModelScope.launch { preferencesRepository.setPillCollapsedWidth(width) }
    }

    fun setGlobalDesktopMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setGlobalDesktopMode(enabled)
            syncDesktopModeToAllTabs(enabled)
        }
    }

    fun setNewTabPosition(position: String) {
        viewModelScope.launch { preferencesRepository.setNewTabPosition(position) }
    }

    fun setTabChipHeight(height: String) {
        viewModelScope.launch { preferencesRepository.setTabChipHeight(height) }
    }

    fun setActiveTabStyle(style: String) {
        viewModelScope.launch { preferencesRepository.setActiveTabStyle(style) }
    }

    fun setScrimDarkness(darkness: String) {
        viewModelScope.launch { preferencesRepository.setScrimDarkness(darkness) }
    }

    fun setShowProgressBar(show: Boolean) {
        viewModelScope.launch { preferencesRepository.setShowProgressBar(show) }
    }

    fun setPillBlurOpacity(opacity: Float) {
        viewModelScope.launch { preferencesRepository.setPillBlurOpacity(opacity) }
    }

    fun setStartPageBranding(branding: String) {
        viewModelScope.launch { preferencesRepository.setStartPageBranding(branding) }
    }

    fun setBrowserMode(mode: String) {
        viewModelScope.launch { preferencesRepository.setBrowserMode(mode) }
    }

    fun setUiVariant(variant: String) {
        viewModelScope.launch { preferencesRepository.setUiVariant(variant) }
    }

    private fun syncDesktopModeToAllTabs(enabled: Boolean) {
        for (index in _tabDescriptors.indices) {
            val desc = _tabDescriptors[index]
            _tabDescriptors[index] = desc.copy(isDesktopMode = enabled)
            if (_activeTabId.value == desc.id && _activeTabState.value != null) {
                _activeTabState.value = _activeTabState.value?.copy(isDesktopMode = enabled)
            }
            applyDesktopModeToSession(desc.id)
        }
    }

    fun installExtensionFromUrl(url: String) {
        viewModelScope.launch {
            val extMan = com.jusdots.jusbrowse.BrowserApplication.extensionManager
            if (extMan != null) {
                extMan.installExtension(url) { webExt ->
                    Log.d("BrowserViewModel", "Extension installed: ${webExt.metaData?.name}")
                }
            }
        }
    }

    fun setExtensionEnabled(extensionId: String, enabled: Boolean) {
        viewModelScope.launch {
            val extMan = com.jusdots.jusbrowse.BrowserApplication.extensionManager
            extMan?.setEnabled(extensionId, enabled)
        }
    }

    fun startDownload(context: android.content.Context, url: String, fileName: String) {
        val downloadManager = context.getSystemService(android.app.DownloadManager::class.java)
        val uri = android.net.Uri.parse(url)
        val request = android.app.DownloadManager.Request(uri)
            .setTitle(fileName)
            .setDescription("Downloading...")
            .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
        val downloadId = downloadManager?.enqueue(request) ?: return
        val path = "${android.os.Environment.DIRECTORY_DOWNLOADS}/$fileName"
        viewModelScope.launch {
            downloadRepository.addDownload(
                com.jusdots.jusbrowse.data.models.DownloadItem(
                    fileName = fileName,
                    url = url,
                    filePath = path,
                    fileSize = 0L,
                    timestamp = System.currentTimeMillis(),
                    status = "Downloading",
                    systemDownloadId = downloadId
                )
            )
        }
    }

    fun addDownload(fileName: String, url: String, path: String, size: Long) {
        viewModelScope.launch {
            downloadRepository.addDownload(
                com.jusdots.jusbrowse.data.models.DownloadItem(
                    fileName = fileName,
                    url = url,
                    filePath = path,
                    fileSize = size,
                    timestamp = System.currentTimeMillis(),
                    status = "Downloading"
                )
            )
        }
    }

    fun deleteDownload(item: DownloadItem) {
        viewModelScope.launch {
            downloadRepository.deleteDownload(item)
        }
    }

    fun clearDownloads() {
        viewModelScope.launch {
            downloadRepository.clearAll()
        }
    }

    fun updateStickerTransform(id: String, x: Float, y: Float, w: Float, h: Float, r: Float) {
        val index = stickers.indexOfFirst { it.id == id }
        if (index >= 0) {
            stickers[index] = stickers[index].copy(
                x = x, y = y, widthDp = w, heightDp = h, rotation = r
            )
            saveStickers()
        }
    }

    fun removeSticker(id: String) {
        stickers.removeAll { it.id == id }
        saveStickers()
    }

    fun addSticker(uri: String) {
        val sticker = com.jusdots.jusbrowse.data.models.Sticker(
            id = java.util.UUID.randomUUID().toString(),
            imageUri = uri,
            x = 0.5f,
            y = 0.5f
        )
        stickers.add(sticker)
        viewModelScope.launch {
            val json = com.google.gson.Gson().toJson(stickers.toList())
            preferencesRepository.saveStickers(json)
        }
    }

    fun saveStickers() {
        viewModelScope.launch {
            val json = com.google.gson.Gson().toJson(stickers.toList())
            preferencesRepository.saveStickers(json)
        }
    }

    fun updateStickerLink(id: String, link: String?) {
        val index = stickers.indexOfFirst { it.id == id }
        if (index >= 0) {
            stickers[index] = stickers[index].copy(link = link)
            saveStickers()
        }
    }

    companion object {
        const val DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"

    }
}
