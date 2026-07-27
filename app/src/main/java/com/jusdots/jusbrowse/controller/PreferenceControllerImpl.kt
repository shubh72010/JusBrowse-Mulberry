package com.jusdots.jusbrowse.controller

import android.content.Context
import com.jusdots.jusbrowse.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class PreferenceControllerImpl(
    context: Context
) : PreferenceController {

    private val repository = PreferencesRepository(context)

    override val searchEngine: Flow<String> = repository.searchEngine
    override val homePage: Flow<String> = repository.homePage
    override val javascriptEnabled: Flow<Boolean> = repository.javascriptEnabled
    override val darkMode: Flow<Boolean> = repository.darkMode
    override val adBlockEnabled: Flow<Boolean> = repository.adBlockEnabled
    override val httpsOnly: Flow<Boolean> = repository.httpsOnly
    override val flagSecureEnabled: Flow<Boolean> = repository.flagSecureEnabled
    override val cookieBlockerEnabled: Flow<Boolean> = repository.cookieBlockerEnabled
    override val popupBlockerEnabled: Flow<Boolean> = repository.popupBlockerEnabled
    override val showTabIcons: Flow<Boolean> = repository.showTabIcons
    override val themePreset: Flow<String> = repository.themePreset
    override val customThemeColor: Flow<String> = repository.customThemeColor
    override val virusTotalApiKey: Flow<String> = repository.virusTotalApiKey
    override val koodousApiKey: Flow<String> = repository.koodousApiKey
    override val amoledBlackEnabled: Flow<Boolean> = repository.amoledBlackEnabled
    override val startPageWallpaperUri: Flow<String?> = repository.startPageWallpaperUri
    override val startPageBlurAmount: Flow<Float> = repository.startPageBlurAmount
    override val backgroundPreset: Flow<String> = repository.backgroundPreset
    override val customDohUrl: Flow<String> = repository.customDohUrl
    override val customSearchEngineUrl: Flow<String> = repository.customSearchEngineUrl
    override val protectionWhitelist: Flow<String> = repository.protectionWhitelist
    override val maxCacheSizeMB: Flow<Int> = repository.maxCacheSizeMB
    override val multiMediaPlaybackEnabled: Flow<Boolean> = repository.multiMediaPlaybackEnabled
    override val appFont: Flow<String> = repository.appFont
    override val browserMode: Flow<String> = repository.browserMode
    override val uiVariant: Flow<String> = repository.uiVariant
    override val boomerModeEnabled: Flow<Boolean> = repository.boomerModeEnabled
    override val reducedAnimations: Flow<Boolean> = repository.reducedAnimations
    override val compactMode: Flow<Boolean> = repository.compactMode
    override val alwaysShowUrl: Flow<Boolean> = repository.alwaysShowUrl
    override val showProgressBar: Flow<Boolean> = repository.showProgressBar
    override val follianMode: Flow<Boolean> = repository.follianMode
    override val follianModeCached: Boolean get() = repository.follianModeCached
    override val stickersEnabled: Flow<Boolean> = repository.stickersEnabled
    override val pillBlurOpacity: Flow<Float> = repository.pillBlurOpacity
    override val pillBottomMargin: Flow<Int> = repository.pillBottomMargin
    override val pillCollapsedWidth: Flow<Int> = repository.pillCollapsedWidth
    override val tabChipHeight: Flow<String> = repository.tabChipHeight
    override val activeTabStyle: Flow<String> = repository.activeTabStyle
    override val startPageBranding: Flow<String> = repository.startPageBranding
    override val scrimDarkness: Flow<String> = repository.scrimDarkness
    override val toolbarPosition: Flow<String> = repository.toolbarPosition
    override val globalDesktopMode: Flow<Boolean> = repository.globalDesktopMode
    override val newTabPosition: Flow<String> = repository.newTabPosition
    override val addressBarStyle: Flow<String> = repository.addressBarStyle

    override suspend fun initFollianModeCache() {
        repository.initFollianModeCache()
    }

    override fun updateFollianModeCache(value: Boolean) {
        repository.updateFollianModeCache(value)
    }

    override suspend fun setProtectionWhitelist(whitelist: String) {
        repository.setProtectionWhitelist(whitelist)
    }

    override suspend fun setMaxCacheSizeMB(size: Int) {
        repository.setMaxCacheSizeMB(size)
    }

    override suspend fun setSearchEngine(engine: String) {
        repository.setSearchEngine(engine)
    }

    override suspend fun setCustomSearchEngineUrl(url: String) {
        repository.setCustomSearchEngineUrl(url)
    }

    override suspend fun setHomePage(homePage: String) {
        repository.setHomePage(homePage)
    }

    override suspend fun setStartPageWallpaperUri(uri: String?) {
        repository.setStartPageWallpaperUri(uri)
    }

    override suspend fun setStartPageBlurAmount(amount: Float) {
        repository.setStartPageBlurAmount(amount)
    }

    override suspend fun setBackgroundPreset(preset: String) {
        repository.setBackgroundPreset(preset)
    }

    override suspend fun setJavascriptEnabled(enabled: Boolean) {
        repository.setJavascriptEnabled(enabled)
    }

    override suspend fun setHttpsOnly(enabled: Boolean) {
        repository.setHttpsOnly(enabled)
    }

    override suspend fun setCookieBlockerEnabled(enabled: Boolean) {
        repository.setCookieBlockerEnabled(enabled)
    }

    override suspend fun setPopupBlockerEnabled(enabled: Boolean) {
        repository.setPopupBlockerEnabled(enabled)
    }

    override suspend fun setAdBlockEnabled(enabled: Boolean) {
        repository.setAdBlockEnabled(enabled)
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        repository.setDarkMode(enabled)
    }

    override suspend fun setFlagSecureEnabled(enabled: Boolean) {
        repository.setFlagSecureEnabled(enabled)
    }

    override suspend fun setMultiMediaPlaybackEnabled(enabled: Boolean) {
        repository.setMultiMediaPlaybackEnabled(enabled)
    }

    override suspend fun setAppFont(font: String) {
        repository.setAppFont(font)
    }

    override suspend fun setShowTabIcons(enabled: Boolean) {
        repository.setShowTabIcons(enabled)
    }

    override suspend fun setThemePreset(preset: String) {
        repository.setThemePreset(preset)
    }

    override suspend fun setCustomThemeColor(colorHex: String) {
        repository.setCustomThemeColor(colorHex)
    }

    override suspend fun setVirusTotalApiKey(key: String) {
        repository.setVirusTotalApiKey(key)
    }

    override suspend fun setCustomDohUrl(url: String) {
        repository.setCustomDohUrl(url)
    }

    override suspend fun setKoodousApiKey(key: String) {
        repository.setKoodousApiKey(key)
    }

    override suspend fun setFollianMode(enabled: Boolean) {
        repository.setFollianMode(enabled)
    }

    override suspend fun setCompactMode(enabled: Boolean) {
        repository.setCompactMode(enabled)
    }

    override suspend fun setAmoledBlackEnabled(enabled: Boolean) {
        repository.setAmoledBlackEnabled(enabled)
    }

    override suspend fun setStickersEnabled(enabled: Boolean) {
        repository.setStickersEnabled(enabled)
    }

    override suspend fun setAlwaysShowUrl(enabled: Boolean) {
        repository.setAlwaysShowUrl(enabled)
    }

    override suspend fun setReducedAnimations(enabled: Boolean) {
        repository.setReducedAnimations(enabled)
    }

    override suspend fun setPillBottomMargin(margin: Int) {
        repository.setPillBottomMargin(margin)
    }

    override suspend fun setPillCollapsedWidth(width: Int) {
        repository.setPillCollapsedWidth(width)
    }

    override suspend fun setTabChipHeight(height: String) {
        repository.setTabChipHeight(height)
    }

    override suspend fun setActiveTabStyle(style: String) {
        repository.setActiveTabStyle(style)
    }

    override suspend fun setPillBlurOpacity(opacity: Float) {
        repository.setPillBlurOpacity(opacity)
    }

    override suspend fun setStartPageBranding(branding: String) {
        repository.setStartPageBranding(branding)
    }

    override suspend fun setScrimDarkness(darkness: String) {
        repository.setScrimDarkness(darkness)
    }

    override suspend fun setShowProgressBar(show: Boolean) {
        repository.setShowProgressBar(show)
    }

    override suspend fun setBrowserMode(mode: String) {
        repository.setBrowserMode(mode)
    }

    override suspend fun setUiVariant(variant: String) {
        repository.setUiVariant(variant)
    }

    override suspend fun setToolbarPosition(position: String) {
        repository.setToolbarPosition(position)
    }

    override suspend fun setGlobalDesktopMode(enabled: Boolean) {
        repository.setGlobalDesktopMode(enabled)
    }

    override suspend fun setNewTabPosition(position: String) {
        repository.setNewTabPosition(position)
    }

    override fun toggleDomainWhitelist(domain: String) {
        // Implemented in BrowserViewModel (involves tab descriptor state)
    }
}