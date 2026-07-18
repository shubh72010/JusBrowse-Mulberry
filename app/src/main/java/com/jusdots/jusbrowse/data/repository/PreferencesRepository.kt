package com.jusdots.jusbrowse.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "browser_preferences")

class PreferencesRepository(private val context: Context) {

    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_api_keys",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private object PreferenceKeys {
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val HOME_PAGE = stringPreferencesKey("home_page")
        val JAVASCRIPT_ENABLED = booleanPreferencesKey("javascript_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SAVED_TABS = stringPreferencesKey("saved_tabs")
        val SAVED_WINDOW_STATES = stringPreferencesKey("saved_window_states")
        val ACTIVE_TAB_INDEX = stringPreferencesKey("active_tab_index")
        val HTTPS_ONLY = booleanPreferencesKey("https_only")
        val FLAG_SECURE_ENABLED = booleanPreferencesKey("flag_secure_enabled")
        val COOKIE_BLOCKER_ENABLED = booleanPreferencesKey("cookie_blocker_enabled")
        val POPUP_BLOCKER_ENABLED = booleanPreferencesKey("popup_blocker_enabled")
        val SAVED_SHORTCUTS = stringPreferencesKey("saved_shortcuts")
        val SHOW_TAB_ICONS = booleanPreferencesKey("show_tab_icons")
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val FOLLIAN_MODE = booleanPreferencesKey("follian_mode")
        val TOOLBAR_POSITION = stringPreferencesKey("toolbar_position")
        val COMPACT_MODE = booleanPreferencesKey("compact_mode")
        val ADDRESS_BAR_STYLE = stringPreferencesKey("address_bar_style")
        val AMOLED_BLACK_ENABLED = booleanPreferencesKey("amoled_black_enabled")
        val START_PAGE_WALLPAPER_URI = stringPreferencesKey("start_page_wallpaper_uri")
        val START_PAGE_BLUR_AMOUNT = stringPreferencesKey("start_page_blur_amount")
        val CUSTOM_DOH_URL = stringPreferencesKey("custom_doh_url")
        val CUSTOM_SEARCH_ENGINE_URL = stringPreferencesKey("custom_search_engine_url")
        val MULTI_MEDIA_PLAYBACK_ENABLED = booleanPreferencesKey("multi_media_playback_enabled")
        val APP_FONT = stringPreferencesKey("app_font")
        val BACKGROUND_PRESET = stringPreferencesKey("background_preset")
        val SAVED_STICKERS = stringPreferencesKey("saved_stickers")
        val STICKERS_ENABLED = booleanPreferencesKey("stickers_enabled")
        val PROTECTION_WHITELIST = stringPreferencesKey("protection_whitelist")
        val MAX_CACHE_SIZE_MB = intPreferencesKey("max_cache_size_mb")
        val BOOMER_MODE_ENABLED = booleanPreferencesKey("boomer_mode_enabled")

        val ALWAYS_SHOW_URL = booleanPreferencesKey("always_show_url")
        val MULTI_VIEW_MODE = booleanPreferencesKey("multi_view_mode")
        val REDUCED_ANIMATIONS = booleanPreferencesKey("reduced_animations")
        val PILL_BOTTOM_MARGIN = intPreferencesKey("pill_bottom_margin")
        val PILL_COLLAPSED_WIDTH = intPreferencesKey("pill_collapsed_width")
        val GLOBAL_DESKTOP_MODE = booleanPreferencesKey("global_desktop_mode")
        val NEW_TAB_POSITION = stringPreferencesKey("new_tab_position")
        val TAB_CHIP_HEIGHT = stringPreferencesKey("tab_chip_height")
        val ACTIVE_TAB_STYLE = stringPreferencesKey("active_tab_style")
        val SCRIM_DARKNESS = stringPreferencesKey("scrim_darkness")
        val SHOW_PROGRESS_BAR = booleanPreferencesKey("show_progress_bar")
        val START_PAGE_BRANDING = stringPreferencesKey("start_page_branding")
        val CUSTOM_THEME_COLOR = stringPreferencesKey("custom_theme_color")
        val BROWSER_MODE = stringPreferencesKey("browser_mode")
        val UI_VARIANT = stringPreferencesKey("ui_variant")
    }

    val searchEngine: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SEARCH_ENGINE] ?: "DuckDuckGo"
    }

    val homePage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.HOME_PAGE] ?: "about:blank"
    }

    val javascriptEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.JAVASCRIPT_ENABLED] ?: true
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DARK_MODE] ?: true
    }



    val savedTabs: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SAVED_TABS]
    }

    val savedWindowStates: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SAVED_WINDOW_STATES]
    }

    val activeTabIndex: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ACTIVE_TAB_INDEX]?.toIntOrNull() ?: 0
    }

    val adBlockEnabled: Flow<Boolean> = context.dataStore.data.map { true }

    val httpsOnly: Flow<Boolean> = context.dataStore.data.map { true }

    val flagSecureEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FLAG_SECURE_ENABLED] ?: true
    }

    val cookieBlockerEnabled: Flow<Boolean> = context.dataStore.data.map { true }

    val popupBlockerEnabled: Flow<Boolean> = context.dataStore.data.map { true }

    val showTabIcons: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SHOW_TAB_ICONS] ?: false
    }

    val themePreset: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.THEME_PRESET] ?: "SYSTEM"
    }

    val customThemeColor: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CUSTOM_THEME_COLOR] ?: ""
    }

    private val _virusTotalApiKey = MutableStateFlow(encryptedPrefs.getString("virustotal_api_key", "") ?: "")
    val virusTotalApiKey: Flow<String> = _virusTotalApiKey.asStateFlow()

    private val _koodousApiKey = MutableStateFlow(encryptedPrefs.getString("koodous_api_key", "") ?: "")
    val koodousApiKey: Flow<String> = _koodousApiKey.asStateFlow()

    val customDohUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CUSTOM_DOH_URL] ?: ""
    }

    val customSearchEngineUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CUSTOM_SEARCH_ENGINE_URL] ?: ""
    }

    suspend fun setSearchEngine(searchEngine: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SEARCH_ENGINE] = searchEngine
        }
    }

    suspend fun setHomePage(homePage: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.HOME_PAGE] = homePage
        }
    }

    suspend fun setJavascriptEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.JAVASCRIPT_ENABLED] = enabled
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_MODE] = enabled
        }
    }

    suspend fun saveSession(tabsJson: String, windowStatesJson: String, activeIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SAVED_TABS] = tabsJson
            preferences[PreferenceKeys.SAVED_WINDOW_STATES] = windowStatesJson
            preferences[PreferenceKeys.ACTIVE_TAB_INDEX] = activeIndex.toString()
        }
    }

    suspend fun setFlagSecureEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FLAG_SECURE_ENABLED] = enabled
        }
    }

    val savedShortcuts: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SAVED_SHORTCUTS]
    }

    suspend fun saveShortcuts(shortcutsJson: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SAVED_SHORTCUTS] = shortcutsJson
        }
    }

    suspend fun setShowTabIcons(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHOW_TAB_ICONS] = enabled
        }
    }

    suspend fun setThemePreset(preset: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_PRESET] = preset
        }
    }

    suspend fun setCustomThemeColor(colorHex: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CUSTOM_THEME_COLOR] = colorHex
        }
    }

    fun setVirusTotalApiKey(key: String) {
        encryptedPrefs.edit().putString("virustotal_api_key", key).apply()
        _virusTotalApiKey.value = key
    }

    fun setKoodousApiKey(key: String) {
        encryptedPrefs.edit().putString("koodous_api_key", key).apply()
        _koodousApiKey.value = key
    }

    suspend fun setCustomDohUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CUSTOM_DOH_URL] = url
        }
    }

    suspend fun setCustomSearchEngineUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CUSTOM_SEARCH_ENGINE_URL] = url
        }
    }

    // ============ UI CUSTOMIZATION PREFERENCES ============

    val follianMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FOLLIAN_MODE] ?: false
    }

    val follianModeCached: Boolean get() = _follianModeCached
    private var _follianModeCached: Boolean = false

    suspend fun initFollianModeCache() {
        _follianModeCached = follianMode.first()
    }

    fun updateFollianModeCache(value: Boolean) {
        _follianModeCached = value
    }

    val toolbarPosition: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.TOOLBAR_POSITION] ?: "TOP"
    }

    val compactMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.COMPACT_MODE] ?: false
    }

    val addressBarStyle: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ADDRESS_BAR_STYLE] ?: "ROUNDED"
    }

    suspend fun setFollianMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FOLLIAN_MODE] = enabled
        }
    }

    suspend fun setToolbarPosition(position: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.TOOLBAR_POSITION] = position
        }
    }

    suspend fun setCompactMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.COMPACT_MODE] = enabled
        }
    }

    suspend fun setAddressBarStyle(style: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ADDRESS_BAR_STYLE] = style
        }
    }

    val amoledBlackEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.AMOLED_BLACK_ENABLED] ?: false
    }


    suspend fun setAmoledBlackEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AMOLED_BLACK_ENABLED] = enabled
        }
    }


    val startPageWallpaperUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.START_PAGE_WALLPAPER_URI]
    }

    val startPageBlurAmount: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.START_PAGE_BLUR_AMOUNT]?.toFloat() ?: 0f
    }

    suspend fun setStartPageWallpaperUri(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri != null) {
                preferences[PreferenceKeys.START_PAGE_WALLPAPER_URI] = uri
            } else {
                preferences.remove(PreferenceKeys.START_PAGE_WALLPAPER_URI)
            }
        }
    }

    suspend fun setStartPageBlurAmount(amount: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.START_PAGE_BLUR_AMOUNT] = amount.toString()
        }
    }

    val multiMediaPlaybackEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.MULTI_MEDIA_PLAYBACK_ENABLED] ?: false
    }

    val appFont: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.APP_FONT] ?: "SYSTEM"
    }

    val backgroundPreset: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.BACKGROUND_PRESET] ?: "NONE"
    }

    suspend fun setMultiMediaPlaybackEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MULTI_MEDIA_PLAYBACK_ENABLED] = enabled
        }
    }

    suspend fun setAppFont(font: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.APP_FONT] = font
        }
    }

    suspend fun setBackgroundPreset(preset: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.BACKGROUND_PRESET] = preset
        }
    }

    val stickers: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SAVED_STICKERS]
    }

    suspend fun saveStickers(stickersJson: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SAVED_STICKERS] = stickersJson
        }
    }

    val stickersEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.STICKERS_ENABLED] ?: true
    }

    suspend fun setStickersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.STICKERS_ENABLED] = enabled
        }
    }

    val protectionWhitelist: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.PROTECTION_WHITELIST] ?: ""
    }

    suspend fun setProtectionWhitelist(whitelist: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PROTECTION_WHITELIST] = whitelist
        }
    }

    val maxCacheSizeMB: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.MAX_CACHE_SIZE_MB] ?: 1024 // 1GB default
    }

    suspend fun setMaxCacheSizeMB(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MAX_CACHE_SIZE_MB] = size
        }
    }

    val boomerModeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.BOOMER_MODE_ENABLED] ?: false
    }

    suspend fun setBoomerModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.BOOMER_MODE_ENABLED] = enabled
        }
    }

    val alwaysShowUrl: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ALWAYS_SHOW_URL] ?: true
    }

    suspend fun setAlwaysShowUrl(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ALWAYS_SHOW_URL] = enabled
        }
    }

    val multiViewMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.MULTI_VIEW_MODE] ?: false
    }

    suspend fun setMultiViewMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MULTI_VIEW_MODE] = enabled
        }
    }

    val reducedAnimations: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REDUCED_ANIMATIONS] ?: false
    }

    val pillBottomMargin: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.PILL_BOTTOM_MARGIN] ?: 90
    }

    val pillCollapsedWidth: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.PILL_COLLAPSED_WIDTH] ?: 260
    }

    suspend fun setReducedAnimations(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.REDUCED_ANIMATIONS] = enabled
        }
    }

    suspend fun setPillBottomMargin(margin: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PILL_BOTTOM_MARGIN] = margin
        }
    }

    suspend fun setPillCollapsedWidth(width: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PILL_COLLAPSED_WIDTH] = width
        }
    }

    val globalDesktopMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.GLOBAL_DESKTOP_MODE] ?: false
    }

    val newTabPosition: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.NEW_TAB_POSITION] ?: "end"
    }

    suspend fun setGlobalDesktopMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.GLOBAL_DESKTOP_MODE] = enabled
        }
    }

    suspend fun setNewTabPosition(position: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NEW_TAB_POSITION] = position
        }
    }

    val tabChipHeight: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.TAB_CHIP_HEIGHT] ?: "normal"
    }

    val activeTabStyle: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ACTIVE_TAB_STYLE] ?: "gradient"
    }

    val scrimDarkness: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SCRIM_DARKNESS] ?: "normal"
    }

    val showProgressBar: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SHOW_PROGRESS_BAR] ?: true
    }

    val startPageBranding: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.START_PAGE_BRANDING] ?: "full"
    }

    val browserMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.BROWSER_MODE] ?: "strait"
    }

    suspend fun setTabChipHeight(height: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.TAB_CHIP_HEIGHT] = height
        }
    }

    suspend fun setActiveTabStyle(style: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ACTIVE_TAB_STYLE] = style
        }
    }

    suspend fun setScrimDarkness(darkness: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SCRIM_DARKNESS] = darkness
        }
    }

    suspend fun setShowProgressBar(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHOW_PROGRESS_BAR] = show
        }
    }

    suspend fun setStartPageBranding(branding: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.START_PAGE_BRANDING] = branding
        }
    }

    suspend fun setBrowserMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.BROWSER_MODE] = mode
        }
    }

    val uiVariant: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.UI_VARIANT] ?: com.jusdots.jusbrowse.ui.theme.BrowserUiVariant.DEFAULT.name
    }

    suspend fun setUiVariant(variant: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.UI_VARIANT] = variant
        }
    }
}
