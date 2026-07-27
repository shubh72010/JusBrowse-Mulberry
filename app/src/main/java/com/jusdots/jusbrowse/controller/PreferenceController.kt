package com.jusdots.jusbrowse.controller

import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

interface PreferenceController {
    val searchEngine: Flow<String>
    val homePage: Flow<String>
    val javascriptEnabled: Flow<Boolean>
    val darkMode: Flow<Boolean>
    val adBlockEnabled: Flow<Boolean>
    val httpsOnly: Flow<Boolean>
    val flagSecureEnabled: Flow<Boolean>
    val cookieBlockerEnabled: Flow<Boolean>
    val popupBlockerEnabled: Flow<Boolean>
    val showTabIcons: Flow<Boolean>
    val themePreset: Flow<String>
    val customThemeColor: Flow<String>
    val virusTotalApiKey: Flow<String>
    val koodousApiKey: Flow<String>
    val amoledBlackEnabled: Flow<Boolean>
    val startPageWallpaperUri: Flow<String?>
    val startPageBlurAmount: Flow<Float>
    val backgroundPreset: Flow<String>
    val customDohUrl: Flow<String>
    val customSearchEngineUrl: Flow<String>
    val protectionWhitelist: Flow<String>
    val maxCacheSizeMB: Flow<Int>
    val multiMediaPlaybackEnabled: Flow<Boolean>
    val appFont: Flow<String>
    val browserMode: Flow<String>
    val uiVariant: Flow<String>
    val boomerModeEnabled: Flow<Boolean>
    val reducedAnimations: Flow<Boolean>
    val compactMode: Flow<Boolean>
    val alwaysShowUrl: Flow<Boolean>
    val showProgressBar: Flow<Boolean>
    val follianMode: Flow<Boolean>
    val follianModeCached: Boolean
    val stickersEnabled: Flow<Boolean>
    val pillBlurOpacity: Flow<Float>
    val pillBottomMargin: Flow<Int>
    val pillCollapsedWidth: Flow<Int>
    val tabChipHeight: Flow<String>
    val activeTabStyle: Flow<String>
    val startPageBranding: Flow<String>
    val scrimDarkness: Flow<String>
    val toolbarPosition: Flow<String>
    val globalDesktopMode: Flow<Boolean>
    val newTabPosition: Flow<String>
    val addressBarStyle: Flow<String>

    suspend fun initFollianModeCache()
    fun updateFollianModeCache(value: Boolean)
    suspend fun setProtectionWhitelist(whitelist: String)
    suspend fun setMaxCacheSizeMB(size: Int)
    suspend fun setSearchEngine(engine: String)
    suspend fun setCustomSearchEngineUrl(url: String)
    suspend fun setHomePage(homePage: String)
    suspend fun setStartPageWallpaperUri(uri: String?)
    suspend fun setStartPageBlurAmount(amount: Float)
    suspend fun setBackgroundPreset(preset: String)
    suspend fun setJavascriptEnabled(enabled: Boolean)
    suspend fun setHttpsOnly(enabled: Boolean)
    suspend fun setCookieBlockerEnabled(enabled: Boolean)
    suspend fun setPopupBlockerEnabled(enabled: Boolean)
    suspend fun setAdBlockEnabled(enabled: Boolean)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setFlagSecureEnabled(enabled: Boolean)
    suspend fun setMultiMediaPlaybackEnabled(enabled: Boolean)
    suspend fun setAppFont(font: String)
    suspend fun setShowTabIcons(enabled: Boolean)
    suspend fun setThemePreset(preset: String)
    suspend fun setCustomThemeColor(colorHex: String)
    suspend fun setVirusTotalApiKey(key: String)
    suspend fun setCustomDohUrl(url: String)
    suspend fun setKoodousApiKey(key: String)
    suspend fun setFollianMode(enabled: Boolean)
    suspend fun setCompactMode(enabled: Boolean)
    suspend fun setAmoledBlackEnabled(enabled: Boolean)
    suspend fun setStickersEnabled(enabled: Boolean)
    suspend fun setAlwaysShowUrl(enabled: Boolean)
    suspend fun setReducedAnimations(enabled: Boolean)
    suspend fun setPillBottomMargin(margin: Int)
    suspend fun setPillCollapsedWidth(width: Int)
    suspend fun setTabChipHeight(height: String)
    suspend fun setActiveTabStyle(style: String)
    suspend fun setPillBlurOpacity(opacity: Float)
    suspend fun setStartPageBranding(branding: String)
    suspend fun setScrimDarkness(darkness: String)
    suspend fun setShowProgressBar(show: Boolean)
    suspend fun setBrowserMode(mode: String)
    suspend fun setUiVariant(variant: String)
    suspend fun setToolbarPosition(position: String)
    suspend fun setGlobalDesktopMode(enabled: Boolean)
    suspend fun setNewTabPosition(position: String)
    fun toggleDomainWhitelist(domain: String)
}
