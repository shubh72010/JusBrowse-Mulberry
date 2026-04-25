package com.jusdots.jusbrowse.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "browser_preferences")

class PreferencesRepository(private val context: Context) {
    
    private object PreferenceKeys {
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val HOME_PAGE = stringPreferencesKey("home_page")
        val JAVASCRIPT_ENABLED = booleanPreferencesKey("javascript_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SAVED_TABS = stringPreferencesKey("saved_tabs")
        val SAVED_WINDOW_STATES = stringPreferencesKey("saved_window_states")
        val ACTIVE_TAB_INDEX = stringPreferencesKey("active_tab_index")
        val AD_BLOCK_ENABLED = booleanPreferencesKey("ad_block_enabled")
        val ADVANCED_ADBLOCK_ENABLED = booleanPreferencesKey("advanced_adblock_enabled")
        val HTTPS_ONLY = booleanPreferencesKey("https_only")
        val FLAG_SECURE_ENABLED = booleanPreferencesKey("flag_secure_enabled")
        val DO_NOT_TRACK_ENABLED = booleanPreferencesKey("do_not_track_enabled")
        val COOKIE_BLOCKER_ENABLED = booleanPreferencesKey("cookie_blocker_enabled")
        val POPUP_BLOCKER_ENABLED = booleanPreferencesKey("popup_blocker_enabled")
        val SAVED_SHORTCUTS = stringPreferencesKey("saved_shortcuts")
        val SHOW_TAB_ICONS = booleanPreferencesKey("show_tab_icons")
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val VIRUSTOTAL_API_KEY = stringPreferencesKey("virustotal_api_key")
        val KOODOUS_API_KEY = stringPreferencesKey("koodous_api_key")
        // New UI customization preferences
        val FOLLIAN_MODE = booleanPreferencesKey("follian_mode")
        val TOOLBAR_POSITION = stringPreferencesKey("toolbar_position")
        val COMPACT_MODE = booleanPreferencesKey("compact_mode")
        val ADDRESS_BAR_STYLE = stringPreferencesKey("address_bar_style")
        val AMOLED_BLACK_ENABLED = booleanPreferencesKey("amoled_black_enabled")
        val START_PAGE_WALLPAPER_URI = stringPreferencesKey("start_page_wallpaper_uri")
        val START_PAGE_BLUR_AMOUNT = stringPreferencesKey("start_page_blur_amount")
        val CUSTOM_DOH_URL = stringPreferencesKey("custom_doh_url")
        val CUSTOM_SEARCH_ENGINE_URL = stringPreferencesKey("custom_search_engine_url")
        
        // Engines
        val DEFAULT_ENGINE_ENABLED = booleanPreferencesKey("default_engine_enabled")
        val JUS_FAKE_ENGINE_ENABLED = booleanPreferencesKey("jus_fake_engine_enabled")
        val BORING_ENGINE_ENABLED = booleanPreferencesKey("boring_engine_enabled")
        val MULTI_MEDIA_PLAYBACK_ENABLED = booleanPreferencesKey("multi_media_playback_enabled")
        val APP_FONT = stringPreferencesKey("app_font")
        val BACKGROUND_PRESET = stringPreferencesKey("background_preset")
        val SAVED_STICKERS = stringPreferencesKey("saved_stickers")
        val STICKERS_ENABLED = booleanPreferencesKey("stickers_enabled")
        val PROTECTION_WHITELIST = stringPreferencesKey("protection_whitelist")
        val MAX_CACHE_SIZE_MB = androidx.datastore.preferences.core.intPreferencesKey("max_cache_size_mb")
        val CACHE_POLICY_WIPE_ON_FULL = booleanPreferencesKey("cache_policy_wipe_on_full")
        val CACHE_POLICY_LRU = booleanPreferencesKey("cache_policy_lru")
        val BOOMER_MODE_ENABLED = booleanPreferencesKey("boomer_mode_enabled")
        
        // Analytics
        val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
        val ANALYTICS_USER_ID = stringPreferencesKey("analytics_user_id")
        val ANALYTICS_LAST_SYNC_DATE = stringPreferencesKey("analytics_last_sync_date")
        val ANALYTICS_LAST_SYNC_SUCCESS = booleanPreferencesKey("analytics_last_sync_success")
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

    val adBlockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.AD_BLOCK_ENABLED] ?: true
    }

    val advancedAdBlockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ADVANCED_ADBLOCK_ENABLED] ?: false
    }

    val httpsOnly: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.HTTPS_ONLY] ?: true
    }

    val flagSecureEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FLAG_SECURE_ENABLED] ?: true
    }

    val doNotTrackEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DO_NOT_TRACK_ENABLED] ?: true
    }

    val cookieBlockerEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.COOKIE_BLOCKER_ENABLED] ?: true
    }

    val popupBlockerEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.POPUP_BLOCKER_ENABLED] ?: true
    }

    val showTabIcons: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SHOW_TAB_ICONS] ?: false
    }

    val themePreset: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.THEME_PRESET] ?: "SYSTEM"
    }

    val virusTotalApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.VIRUSTOTAL_API_KEY] ?: ""
    }

    val koodousApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.KOODOUS_API_KEY] ?: ""
    }

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

    suspend fun setAdBlockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AD_BLOCK_ENABLED] = enabled
        }
    }

    suspend fun setAdvancedAdBlockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ADVANCED_ADBLOCK_ENABLED] = enabled
        }
    }

    suspend fun setHttpsOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.HTTPS_ONLY] = enabled
        }
    }

    suspend fun setFlagSecureEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FLAG_SECURE_ENABLED] = enabled
        }
    }

    suspend fun setDoNotTrackEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DO_NOT_TRACK_ENABLED] = enabled
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

    suspend fun setCookieBlockerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.COOKIE_BLOCKER_ENABLED] = enabled
        }
    }

    suspend fun setPopupBlockerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.POPUP_BLOCKER_ENABLED] = enabled
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

    suspend fun setVirusTotalApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.VIRUSTOTAL_API_KEY] = key
        }
    }

    suspend fun setKoodousApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.KOODOUS_API_KEY] = key
        }
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

    // ============ NEW UI CUSTOMIZATION PREFERENCES ============

    val follianMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FOLLIAN_MODE] ?: false
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

    // Engines
    val defaultEngineEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DEFAULT_ENGINE_ENABLED] ?: true
    }

    val jusFakeEngineEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.JUS_FAKE_ENGINE_ENABLED] ?: false
    }

    val boringEngineEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.BORING_ENGINE_ENABLED] ?: false
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

    suspend fun setDefaultEngineEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DEFAULT_ENGINE_ENABLED] = enabled
        }
    }

    suspend fun setJusFakeEngineEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.JUS_FAKE_ENGINE_ENABLED] = enabled
        }
    }

    suspend fun setBoringEngineEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.BORING_ENGINE_ENABLED] = enabled
        }
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

    val cachePolicyWipeOnFull: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CACHE_POLICY_WIPE_ON_FULL] ?: false
    }

    suspend fun setCachePolicyWipeOnFull(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CACHE_POLICY_WIPE_ON_FULL] = enabled
            if (enabled) preferences[PreferenceKeys.CACHE_POLICY_LRU] = false
        }
    }

    val cachePolicyLRU: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CACHE_POLICY_LRU] ?: true
    }

    suspend fun setCachePolicyLRU(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CACHE_POLICY_LRU] = enabled
            if (enabled) preferences[PreferenceKeys.CACHE_POLICY_WIPE_ON_FULL] = false
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

    val analyticsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ANALYTICS_ENABLED] ?: true
    }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ANALYTICS_ENABLED] = enabled
        }
    }

    val analyticsUserId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ANALYTICS_USER_ID]
    }

    suspend fun setAnalyticsUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ANALYTICS_USER_ID] = userId
        }
    }

    val analyticsLastSyncDate: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ANALYTICS_LAST_SYNC_DATE]
    }

    suspend fun setAnalyticsLastSyncDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ANALYTICS_LAST_SYNC_DATE] = date
        }
    }

    val analyticsLastSyncSuccess: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ANALYTICS_LAST_SYNC_SUCCESS] ?: false
    }

    suspend fun setAnalyticsLastSyncSuccess(success: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ANALYTICS_LAST_SYNC_SUCCESS] = success
        }
    }
}
