package com.jusdots.jusbrowse.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jusdots.jusbrowse.security.ContentBlocker
import kotlinx.coroutines.*
import java.util.UUID
import android.content.Context
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.jusdots.jusbrowse.security.FakeModeManager

sealed class UiEvent {
    data class RequireRestart(val message: String) : UiEvent()
}

data class TabWindowState(
    var x: Float = 0f,
    var y: Float = 0f,
    var scale: Float = 1f,
    var zIndex: Float = 0f
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

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
            com.jusdots.jusbrowse.security.AirlockDiscoveryBus.requestExtraction(tabId)
        }
    }

        // Tab Window management
    val tabWindowStates = mutableStateMapOf<String, TabWindowState>()
    
    // Blocked trackers per tab (unique list for UI)
    val blockedTrackers = mutableStateMapOf<String, SnapshotStateList<TrackerInfo>>()
    
    // Total blocked hits per tab (cumulative count)
    val blockedTrackersCount = mutableStateMapOf<String, Int>()
    
    // Snapshot state lists for Compose
    private val _tabs = mutableStateListOf<BrowserTab>()
    val tabs: SnapshotStateList<BrowserTab> = _tabs
    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex: StateFlow<Int> = _activeTabIndex.asStateFlow()

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
    val geckoSessionPool = mutableMapOf<String, org.mozilla.geckoview.GeckoSession>()

    // Session save debounce
    private var sessionSaveJob: Job? = null

    // Bookmarks, History & Downloads
    val bookmarks = bookmarkRepository.getAllBookmarks()
    val history = historyRepository.getAllHistory()
    val recentHistory = historyRepository.getRecentHistory(10)
    val downloads = downloadRepository.allDownloads

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
    val advancedAdBlockEnabled = preferencesRepository.advancedAdBlockEnabled
    val httpsOnly = preferencesRepository.httpsOnly
    val flagSecureEnabled = preferencesRepository.flagSecureEnabled
    val doNotTrackEnabled = preferencesRepository.doNotTrackEnabled
    val analyticsEnabled = preferencesRepository.analyticsEnabled
    val cookieBlockerEnabled = preferencesRepository.cookieBlockerEnabled
    val popupBlockerEnabled = preferencesRepository.popupBlockerEnabled
    val showTabIcons = preferencesRepository.showTabIcons
    val themePreset = preferencesRepository.themePreset
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
    val cachePolicyWipeOnFull = preferencesRepository.cachePolicyWipeOnFull
    val cachePolicyLRU = preferencesRepository.cachePolicyLRU
    
    // Engines
    val defaultEngineEnabled = preferencesRepository.defaultEngineEnabled
    val jusFakeEngineEnabled = preferencesRepository.jusFakeEngineEnabled
    val boringEngineEnabled = preferencesRepository.boringEngineEnabled
    val multiMediaPlaybackEnabled = preferencesRepository.multiMediaPlaybackEnabled
    val sessionSeed = FakeModeManager.sessionSeed
    val appFont = preferencesRepository.appFont

    // WallTheme Color Extraction
    private val _extractedWallColor = MutableStateFlow<androidx.compose.ui.graphics.Color?>(null)
    val extractedWallColor: StateFlow<androidx.compose.ui.graphics.Color?> = _extractedWallColor.asStateFlow()

    private val _isStickerMode = MutableStateFlow(false)
    val isStickerMode: StateFlow<Boolean> = _isStickerMode.asStateFlow()

    private val _isBoomerMode = MutableStateFlow(false)
    val isBoomerMode: StateFlow<Boolean> = _isBoomerMode.asStateFlow()

    init {
        viewModelScope.launch {
            contentBlocker.initialize()
            loadSession()
            // Sync timezone with network for airtight spoofing
            com.jusdots.jusbrowse.security.FakeModeManager.syncTimezoneWithNetwork(this)
        }
        startCacheMonitor()
        
        // Listen for Native Tracker Blocks from WebExtension
        viewModelScope.launch {
            com.jusdots.jusbrowse.security.TrackerShieldBus.blockedTrackers.collect { (url, domain) ->
                // Map the document URL reported by the extension back to our Tab UUID
                val normalizedUrl = normalizeUrlForMapping(url)
                val tab = tabs.find { normalizeUrlForMapping(it.url) == normalizedUrl }
                if (tab != null) {
                    recordBlockedTracker(tab.id, domain)
                } else if (tabs.isNotEmpty()) {
                    // Fallback to active tab if mapping fails but we are on a page
                    recordBlockedTracker(tabs[activeTabIndex.value].id, domain)
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
            }
        }
        
        // Initial memory check
        reapSessionsIfNeeded()
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

    fun setCachePolicyWipeOnFull(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setCachePolicyWipeOnFull(enabled)
        }
    }

    fun setCachePolicyLRU(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setCachePolicyLRU(enabled)
        }
    }

    private fun startCacheMonitor() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val wipeOnFull = preferencesRepository.cachePolicyWipeOnFull.first()
                    if (wipeOnFull) {
                        val currentLimitMB = preferencesRepository.maxCacheSizeMB.first()
                        val cacheDir = getApplication<Application>().cacheDir
                        val webCache = java.io.File(cacheDir, "gecko_cache") // Generic name for Gecko storage
                        
                        if (webCache.exists()) {
                            val currentSize = getFolderSize(webCache)
                            val limitBytes = currentLimitMB.toLong() * 1024 * 1024
                            
                            if (currentSize >= limitBytes) {
                            if (currentSize >= limitBytes) {
                                clearAllCache()
                            }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(60_000) // Check every minute
            }
        }
    }

    private fun getFolderSize(file: java.io.File): Long {
        var size: Long = 0
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (child in files) {
                    size += getFolderSize(child)
                }
            }
        } else {
            size = file.length()
        }
        return size
    }

    fun clearAllCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val cacheDir = getApplication<Application>().cacheDir
            try {
                // Delete common webview cache locations
                // Cleanup gecko cache if initialized
                java.io.File(cacheDir, "gecko_cache").deleteRecursively()
            } catch (e: Exception) {}
        }
    }

    val stickersEnabled = preferencesRepository.stickersEnabled



    // Multi-View Mode
    private val _isMultiViewMode = MutableStateFlow(false)
    val isMultiViewMode: StateFlow<Boolean> = _isMultiViewMode.asStateFlow()

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(Screen.BROWSER)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

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

    private suspend fun downloadToTempFile(url: String, context: Context): java.io.File? = withContext(Dispatchers.IO) {
        try {
            val file = java.io.File(context.cacheDir, "temp_scan_" + java.util.UUID.randomUUID().toString())
            
            val okHttpClient = com.jusdots.jusbrowse.security.NetworkSurgeon.getSharedClient()
            val requestBuilder = okhttp3.Request.Builder().url(url)
            val headers = FakeModeManager.getHeaders()
            for ((key, value) in headers) {
                requestBuilder.header(key, value)
            }
            
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
            e.printStackTrace()
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



    private val gson = Gson()

    private fun saveSession() {
        sessionSaveJob?.cancel() // Cancel any pending debounced save
        viewModelScope.launch(Dispatchers.Default) {
            val tabsJson = gson.toJson(tabs.toList())
            val windowStatesJson = gson.toJson(tabWindowStates.toMap())
            withContext(Dispatchers.IO) {
                preferencesRepository.saveSession(tabsJson, windowStatesJson, _activeTabIndex.value)
            }
        }
    }

    private fun debouncedSaveSession() {
        sessionSaveJob?.cancel()
        sessionSaveJob = viewModelScope.launch(Dispatchers.Default) {
            delay(100) // 100ms debounce
            val tabsJson = gson.toJson(tabs.toList())
            val windowStatesJson = gson.toJson(tabWindowStates.toMap())
            withContext(Dispatchers.IO) {
                preferencesRepository.saveSession(tabsJson, windowStatesJson, _activeTabIndex.value)
            }
        }
    }

    private suspend fun loadSession() {
        val savedTabsJson = preferencesRepository.savedTabs.first()
        val savedTabWindowStatesJson = preferencesRepository.savedWindowStates.first()
        val savedActiveIndex = preferencesRepository.activeTabIndex.first()

        if (!savedTabsJson.isNullOrBlank()) {
            try {
                val tabsType = object : TypeToken<List<BrowserTab>>() {}.type
                val loadedTabs: List<BrowserTab> = withContext(Dispatchers.IO) { gson.fromJson(savedTabsJson, tabsType) }
                
                val statesType = object : TypeToken<Map<String, TabWindowState>>() {}.type
                val loadedStates: Map<String, TabWindowState> = if (!savedTabWindowStatesJson.isNullOrBlank()) {
                    withContext(Dispatchers.IO) { gson.fromJson(savedTabWindowStatesJson!!, statesType) }
                } else emptyMap()

                tabs.clear()
                tabWindowStates.clear()
                tabWindowStates.putAll(loadedStates)

                // Sanitize and LOAD STAGGERED to avoid ANR from 10+ webviews at once
                val sanitizedTabs = loadedTabs.map { tab ->
                    val cid = try { tab.containerId } catch (e: Exception) { null }
                    if (cid == null) tab.copy(containerId = "default") else tab
                }
                
                // Add active tab first if any
                val activeIdx = if (savedActiveIndex in sanitizedTabs.indices) savedActiveIndex else 0
                if (sanitizedTabs.isNotEmpty()) {
                    tabs.add(sanitizedTabs[activeIdx])
                    _activeTabIndex.value = 0
                    _currentUrl.value = sanitizedTabs[activeIdx].url
                }
                
                // Add others with delay
                viewModelScope.launch {
                    sanitizedTabs.forEachIndexed { index, tab ->
                        if (index != activeIdx) {
                            kotlinx.coroutines.delay(300) // 300ms gap
                            _passiveTabIds[tab.id] = true // Mark passive before UI sees it
                            tabs.add(tab)
                        }
                    }
                }
                
                isSessionLoaded = true
                pendingIntentUrl?.let { url ->
                    createNewTab(url)
                    pendingIntentUrl = null
                }
            } catch (e: Exception) {
                // Layer 12: No debug logging in release - silently handle
                createNewTab()
            }
        }

        if (tabs.isEmpty()) {
            createNewTab()
        }

        val savedShortcutsJson = preferencesRepository.savedShortcuts.first()
        if (!savedShortcutsJson.isNullOrBlank()) {
             try {
                 val shortcutsType = object : TypeToken<List<Shortcut>>() {}.type
                 val loadedShortcuts: List<Shortcut> = gson.fromJson(savedShortcutsJson, shortcutsType)
                 pinnedShortcuts.clear()
                 pinnedShortcuts.addAll(loadedShortcuts)
             } catch (e: Exception) {
                 // Ignore
             }
        }

        val savedStickersJson = preferencesRepository.stickers.first()
        if (!savedStickersJson.isNullOrBlank()) {
             try {
                 val stickerType = object : TypeToken<List<Sticker>>() {}.type
                 val loadedStickers: List<Sticker> = gson.fromJson(savedStickersJson, stickerType)
                 stickers.clear()
                 stickers.addAll(loadedStickers)
             } catch (e: Exception) {
                 // Ignore
             }
        }
    }

    private fun saveShortcuts() {
        viewModelScope.launch {
            val json = gson.toJson(pinnedShortcuts.toList())
            preferencesRepository.saveShortcuts(json)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cleanup GeckoSessions
        geckoSessionPool.values.forEach { it.close() }
        geckoSessionPool.clear()
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
        
        // Minor delay only if we are under memory pressure (existing sessions)
        if (geckoSessionPool.size > 0) {
            kotlinx.coroutines.delay(100)
        }

        val newSession = com.jusdots.jusbrowse.security.GeckoSessionFactory.createSession(isPrivate, containerId)
        registerGeckoSession(tabId, newSession)
        return newSession
    }

    fun registerGeckoSession(tabId: String, session: org.mozilla.geckoview.GeckoSession) {
        geckoSessionPool[tabId] = session
        _passiveTabIds[tabId] = false
        reapSessionsIfNeeded()
    }

    /**
     * Layer 12 Adaptive Memory Reaper
     * Closes non-essential background sessions to stay within RAM budget.
     */
    fun reapSessionsIfNeeded(preemptive: Int = 0) {
        val now = System.currentTimeMillis()
        if (now - lastMemoryCheckTime < 2000 && preemptive == 0) return // Throttled check unless preemptive
        lastMemoryCheckTime = now

        val context = getApplication<Application>()
        val budget = com.jusdots.jusbrowse.security.MemorySurgeon.calculateActiveSessionBudget(context)
        
        if (geckoSessionPool.size + preemptive > budget) {
            val activeTabId = tabs.getOrNull(_activeTabIndex.value)?.id
            val visibleTabIds = if (isMultiViewMode.value) getVisibleTabs().map { it.id }.toSet() else emptySet()
            
            // Priority: Focused Tab > Visible Multi-View Tabs > Others (LRU)
            val candidates = geckoSessionPool.keys.filter { id ->
                id != activeTabId && !visibleTabIds.contains(id)
            }
            
            // Close oldest sessions first until we hit budget
            val numToReap = (geckoSessionPool.size + preemptive) - budget
            candidates.take(numToReap).forEach { id ->
                Log.d("BrowserViewModel", "Memory Pressure: Suspending tab $id to Passive Mode")
                geckoSessionPool[id]?.close()
                geckoSessionPool.remove(id)
                _passiveTabIds[id] = true
            }
        }
    }

    fun onTrimMemory(level: Int) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            Log.w("BrowserViewModel", "Severe Memory Pressure (level $level). Flushing caches.")
            // 1. Force reap everything except active tab
            val activeTabId = tabs.getOrNull(_activeTabIndex.value)?.id
            geckoSessionPool.keys.filter { it != activeTabId }.forEach { id ->
                geckoSessionPool[id]?.close()
                geckoSessionPool.remove(id)
                _passiveTabIds[id] = true
            }
            // 2. Clear content blocker cache
            // contentBlocker.clearCache() // TODO: implement in ContentBlocker if needed
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
        val newTab = BrowserTab(
            id = newTabId,
            url = finalUrl,
            isPrivate = isPrivate,
            containerId = containerId
        )
        tabs.add(newTab)
        
        // Initialize window state with a cascade effect
        val offset = (tabs.size * 20).toFloat()
        tabWindowStates[newTabId] = TabWindowState(
            x = offset,
            y = offset,
            zIndex = (tabs.size).toFloat()
        )

        if (select) {
            _activeTabIndex.value = tabs.lastIndex
            _currentUrl.value = url
        }
        saveSession()
    }

    // Tab Group Operations
    fun openTabGroup(groupId: String?) {
        _activeGroupId.value = groupId
    }

    fun groupTabs(draggedTabId: String, targetTabId: String) {
        val draggedIndex = tabs.indexOfFirst { it.id == draggedTabId }
        val targetIndex = tabs.indexOfFirst { it.id == targetTabId }
        
        if (draggedIndex != -1 && targetIndex != -1 && draggedTabId != targetTabId) {
            val targetTab = tabs[targetIndex]
            val draggedTab = tabs[draggedIndex]
            
            // If target is already a Master, just add the dragged tab to it
            val groupIdToUse = if (targetTab.isGroupMaster) {
                targetTab.id
            } else {
                // Target becomes a master
                val updatedTarget = targetTab.copy(isGroupMaster = true)
                tabs[targetIndex] = updatedTarget
                targetTab.id
            }
            
            // Assign dragged tab to new parent
            val updatedDragged = draggedTab.copy(parentGroupId = groupIdToUse)
            tabs[draggedIndex] = updatedDragged
            
            saveSession()
        }
    }

    fun ungroupTab(tabId: String) {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index != -1) {
            val tab = tabs[index]
            val parentId = tab.parentGroupId
            val updatedTab = tab.copy(parentGroupId = null)
            tabs[index] = updatedTab
            
            // If the parent group is now empty, demote the parent master back to a normal tab
            if (parentId != null) {
                val remainingChildren = tabs.count { it.parentGroupId == parentId }
                if (remainingChildren == 0) {
                    val parentIndex = tabs.indexOfFirst { it.id == parentId }
                    if (parentIndex != -1) {
                        tabs[parentIndex] = tabs[parentIndex].copy(isGroupMaster = false)
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
        if (index in tabs.indices) {
            _activeTabIndex.value = index
            val tab = tabs[index]
            _currentUrl.value = tab.url
            
            // Re-activate if it was passive
            if (_passiveTabIds[tab.id] == true) {
                _passiveTabIds[tab.id] = false
            }
            
            reapSessionsIfNeeded()
            saveSession()
            
            // Bring window to front if in multi-view
            val tabId = tabs[index].id
            bringToFront(tabId)
        }
    }

    fun closeTab(index: Int) {
        if (tabs.size > 1 && index in tabs.indices) {
            val tabToRemove = tabs[index]
            
            // ── Group Safeguard ──
            if (tabToRemove.isGroupMaster) {
                // Remove parentGroupId from all children
                val children = tabs.filter { it.parentGroupId == tabToRemove.id }
                children.forEach { child ->
                    val childIndex = tabs.indexOfFirst { it.id == child.id }
                    if (childIndex != -1) {
                        tabs[childIndex] = child.copy(parentGroupId = null)
                    }
                }
                
                // Clear active group if it was this one
                if (_activeGroupId.value == tabToRemove.id) {
                    _activeGroupId.value = null
                }
            }
            
            val session = geckoSessionPool[tabToRemove.id]
            val isFollianActive = kotlinx.coroutines.runBlocking { preferencesRepository.follianMode.first() }
            if (isFollianActive) {
                com.jusdots.jusbrowse.BrowserApplication.runtime?.storageController?.clearData(org.mozilla.geckoview.StorageController.ClearFlags.ALL)
            }
            session?.close()
            geckoSessionPool.remove(tabToRemove.id)

            tabWindowStates.remove(tabToRemove.id)
            tabs.removeAt(index)
            
            // Adjust active tab index
            when {
                index < _activeTabIndex.value -> _activeTabIndex.value--
                index == _activeTabIndex.value && index == tabs.size -> {
                    _activeTabIndex.value = tabs.lastIndex
                }
            }
            
            // Update current URL
            if (_activeTabIndex.value in tabs.indices) {
                _currentUrl.value = tabs[_activeTabIndex.value].url
            }
            saveSession()
        } else if (tabs.size == 1) {
            // If closing last tab, create a new one
            val oldTabId = tabs[0].id
            geckoSessionPool[oldTabId]?.close()
            geckoSessionPool.remove(oldTabId)

            tabWindowStates.remove(oldTabId)

            val newId = UUID.randomUUID().toString()
            tabs[0] = BrowserTab(
                id = newId,
                url = "about:blank"
            )
            tabWindowStates[newId] = TabWindowState()
            _currentUrl.value = "about:blank"
            saveSession()
        }
    }

    fun closeAllTabs() {
        geckoSessionPool.values.forEach { it.close() }
        geckoSessionPool.clear()
        
        // Clear states
        tabWindowStates.clear()
        tabs.clear()
        
        // Re-initialize with one fresh tab
        createNewTab()
        saveSession()
    }

    fun updateTab(index: Int, updatedTab: BrowserTab) {
        if (index in tabs.indices) {
            tabs[index] = updatedTab
            if (index == _activeTabIndex.value) {
                _currentUrl.value = updatedTab.url
            }
            saveSession()
        }
    }

    // Navigation
    // DEPRECATED: Uses active tab. Use navigateToUrl(tabId, url) instead for multi-window correctness.
    fun navigateToUrl(url: String) {
        navigateToUrlForIndex(_activeTabIndex.value, url)
    }

    fun navigateToUrlForIndex(index: Int, url: String) {
        val normalizedUrl = normalizeUrl(url)
        
        if (index in tabs.indices) {
            val tab = tabs[index]
            // Clear trackers for the new navigation
            blockedTrackers.remove(tab.id)
            
            val updatedTab = tab.copy(url = normalizedUrl)
            updateTab(index, updatedTab)
            
            // Add to history only if NOT private
            if (!tab.isPrivate) {
                viewModelScope.launch {
                    historyRepository.addToHistory(
                        title = normalizedUrl,
                        url = normalizedUrl
                    )
                }
            }
        }
    }
    
    fun navigateToUrlByTabId(tabId: String, url: String) {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index != -1) {
            navigateToUrlForIndex(index, url)
        }
    }

    fun updateTabTitle(index: Int, title: String) {
        if (index in tabs.indices) {
            val tab = tabs[index]
            val updatedTab = tab.copy(title = title)
            updateTab(index, updatedTab)
            
            // Sync with history if not private
            if (!tab.isPrivate) {
                viewModelScope.launch {
                    historyRepository.updateHistoryTitle(tab.url, title)
                }
            }
        }
    }

    fun updateTabLoadingState(index: Int, isLoading: Boolean, progress: Float = 0f) {
        if (index in tabs.indices) {
            val updatedTab = tabs[index].copy(
                isLoading = isLoading,
                progress = progress
            )
            updateTab(index, updatedTab)
        }
    }

    fun updateTabNavigationState(index: Int, canGoBack: Boolean, canGoForward: Boolean) {
        if (index in tabs.indices) {
            val updatedTab = tabs[index].copy(
                canGoBack = canGoBack,
                canGoForward = canGoForward
            )
            updateTab(index, updatedTab)
        }
    }

    fun toggleDesktopMode(tabId: String) {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index != -1) {
            val tab = tabs[index]
            val newMode = !tab.isDesktopMode
            tabs[index] = tab.copy(isDesktopMode = newMode)
            
            // Apply to session immediately if it exists
            val session = geckoSessionPool[tabId]
            if (session != null) {
                // In GeckoView, we change the User Agent override
                val settings = session.settings
                if (newMode) {
                    settings.userAgentOverride = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
                    settings.useTrackingProtection = true
                } else {
                    settings.userAgentOverride = "" // Reset to default mobile
                }
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
                // Ignore errors
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
        viewModelScope.launch {
            preferencesRepository.setJavascriptEnabled(enabled)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDarkMode(enabled)
        }
    }

    fun setAdBlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAdBlockEnabled(enabled)
        }
    }

    fun setAdvancedAdBlockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAdvancedAdBlockEnabled(enabled)
        }
    }

    fun setHttpsOnly(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setHttpsOnly(enabled)
        }
    }

    fun setFlagSecureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFlagSecureEnabled(enabled)
        }
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAnalyticsEnabled(enabled)
        }
    }

    fun setDoNotTrackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDoNotTrackEnabled(enabled)
        }
    }

    fun setCookieBlockerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setCookieBlockerEnabled(enabled)
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

    fun setPopupBlockerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPopupBlockerEnabled(enabled)
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

    fun setVirusTotalApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepository.setVirusTotalApiKey(key)
        }
    }

    fun setCustomDohUrl(url: String) {
        viewModelScope.launch {
            preferencesRepository.setCustomDohUrl(url)
        }
    }

    fun setKoodousApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepository.setKoodousApiKey(key)
        }
    }

    // ============ NEW UI CUSTOMIZATION PREFERENCES ============
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

    // Engines
    fun setDefaultEngineEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                preferencesRepository.setDefaultEngineEnabled(true)
                preferencesRepository.setBoringEngineEnabled(false)
                preferencesRepository.setJusFakeEngineEnabled(false)
            } else {
                preferencesRepository.setDefaultEngineEnabled(false)
            }
        }
    }

    fun setJusFakeEngineEnabled(context: android.content.Context, enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                // If enabling, we don't do it here anymore, we use activateJusFakeEngine from UI
                // to handle the restart/context correctly. 
                // However, for consistency, we update preferences.
                preferencesRepository.setJusFakeEngineEnabled(true)
                preferencesRepository.setDefaultEngineEnabled(false)
            } else {
                preferencesRepository.setJusFakeEngineEnabled(false)
                // Trigger the restart to return to default storage
                com.jusdots.jusbrowse.security.FakeModeManager.disableFakeMode(context)
            }
        }
    }

    fun activateJusFakeEngine(context: android.content.Context, persona: com.jusdots.jusbrowse.security.FakePersona) {
        viewModelScope.launch {
            // 1. Save preferences FIRST and Wait
            preferencesRepository.setJusFakeEngineEnabled(true)
            preferencesRepository.setDefaultEngineEnabled(false)
            
            // 2. Important: Sync other states if needed
            
            // 3. Trigger the restart through FakeModeManager
            com.jusdots.jusbrowse.security.FakeModeManager.enableFakeMode(context, persona)
        }
    }

    fun setBoringEngineEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                preferencesRepository.setBoringEngineEnabled(true)
                preferencesRepository.setDefaultEngineEnabled(false)
            } else {
                preferencesRepository.setBoringEngineEnabled(false)
            }
        }
    }

    // Mutable SharedFlow for UI events like App Restart Prompts
    private val _uiEvents = kotlinx.coroutines.flow.MutableSharedFlow<UiEvent>()
    val uiEvents: kotlinx.coroutines.flow.SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    val follianModeState: kotlinx.coroutines.flow.StateFlow<Boolean> = preferencesRepository.follianMode.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setFollianModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setFollianMode(enabled)
            // Note: Follian requires an app restart to rebuild GeckoRuntime.
            _uiEvents.emit(UiEvent.RequireRestart("Follian Protocol requires an app restart to engage native engine locks."))
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
        // Only allow multi-view if we have 2+ tabs, OR if user just wants to see windows
        if (tabs.size >= 1) { 
            _isMultiViewMode.value = !_isMultiViewMode.value
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

    fun getVisibleTabs(): List<BrowserTab> {
        if (_isMultiViewMode.value) return tabs.toList()
        
        // Return first 4 tabs for grid view (legacy/fallback)
        return tabs.take(4)
    }

    // Downloads
    fun clearDownloads() {
        viewModelScope.launch {
            downloadRepository.clearAll()
        }
    }

    fun deleteDownload(item: DownloadItem) {
        viewModelScope.launch {
            downloadRepository.deleteDownload(item)
        }
    }

    fun addDownload(fileName: String, url: String, filePath: String, fileSize: Long, systemDownloadId: Long = -1) {
        viewModelScope.launch {
            downloadRepository.addDownload(
                DownloadItem(
                    fileName = fileName,
                    url = url,
                    filePath = filePath,
                    fileSize = fileSize,
                    securityStatus = "Pending Scan",
                    systemDownloadId = systemDownloadId
                )
            )
        }
    }

    fun startDownload(context: android.content.Context, url: String, fileName: String) {
        if (url.startsWith("/")) {
            // Local file (Vaulted)
            viewModelScope.launch {
                try {
                    val source = java.io.File(url)
                    val destDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    if (!destDir.exists()) destDir.mkdirs()
                    val destFile = java.io.File(destDir, fileName)
                    source.copyTo(destFile, overwrite = true)
                    
                    addDownload(fileName, "internal://vaulted", destFile.absolutePath, source.length())
                    android.widget.Toast.makeText(context, "Saved to Downloads", android.widget.Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    android.widget.Toast.makeText(context, "Export failed: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        try {
            val uri = android.net.Uri.parse(url)
            val request = android.app.DownloadManager.Request(uri)
                .setTitle(fileName)
                .setDescription("Downloading via JusBrowse...")
                .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)

            val downloadManager = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
            val id = downloadManager.enqueue(request)

            // Add to database
            val fullPath = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + fileName
            addDownload(fileName, url, fullPath, 0L, id)
            
            android.widget.Toast.makeText(context, "Download started", android.widget.Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Download failed: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun updateDownloadSecurity(downloadId: Long, status: String, result: String) {
        viewModelScope.launch {
            // Find by system ID (more robust than filename)
            val allDownloads = downloadRepository.allDownloads.first()
            val item = allDownloads.find { it.systemDownloadId == downloadId }
            
            if (item != null) {
                val updatedItem = item.copy(
                    securityStatus = status,
                    scanResult = result
                )
                downloadRepository.addDownload(updatedItem)
            }
        }
    }

    // Shortcuts Management
    fun pinShortcut(title: String, url: String) {
        val shortcut = Shortcut(title = title, url = url)
        pinnedShortcuts.add(shortcut)
        saveShortcuts()
    }

    fun unpinShortcut(shortcut: Shortcut) {
        pinnedShortcuts.remove(shortcut)
        saveShortcuts()
    }

    fun pinCurrentTabToDesktop() {
        val activeTab = tabs.getOrNull(activeTabIndex.value)
        if (activeTab != null && activeTab.url != "about:blank") {
            pinShortcut(activeTab.title, activeTab.url)
        }
    }

    fun addSticker(imageUri: String, link: String? = null) {
        val newSticker = Sticker(
            id = UUID.randomUUID().toString(),
            imageUri = imageUri,
            link = link,
            x = 0.5f,
            y = 0.5f,
            widthDp = 512f,
            heightDp = 512f,
            rotation = 0f
        )
        stickers.add(newSticker)
        saveStickers()
    }

    fun updateStickerTransform(id: String, x: Float, y: Float, widthDp: Float, heightDp: Float, rotation: Float) {
        val index = stickers.indexOfFirst { it.id == id }
        if (index != -1) {
            stickers[index] = stickers[index].copy(
                x = x,
                y = y,
                widthDp = widthDp,
                heightDp = heightDp,
                rotation = rotation
            )
            saveStickers()
        }
    }

    fun updateStickerLink(stickerId: String, link: String?) {
        val index = stickers.indexOfFirst { it.id == stickerId }
        if (index != -1) {
            stickers[index] = stickers[index].copy(link = link)
            saveStickers()
        }
    }

    fun removeSticker(stickerId: String) {
        stickers.removeIf { it.id == stickerId }
        saveStickers()
    }

    private var stickerSaveJob: Job? = null

    fun saveStickers() {
        stickerSaveJob?.cancel()
        stickerSaveJob = viewModelScope.launch {
            delay(500) // Debounce for 500ms
            val stickersList = stickers.toList()
            val json = withContext(Dispatchers.Default) {
                gson.toJson(stickersList)
            }
            preferencesRepository.saveStickers(json)
        }
    }

    companion object {
        const val DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"

    }
}
