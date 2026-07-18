package com.jusdots.jusbrowse.lifecycle

import android.app.ActivityManager
import android.content.Context
import android.util.Log

class MemoryBudgetController(private val context: Context) {

    companion object {
        const val MAX_ACTIVE_TABS = 1
        const val MAX_SUSPENDED_TABS = 2
        const val MAX_TOTAL_SESSIONS = 3
        const val UI_MEMORY_CEILING_MB = 50
        const val GECKOVIEW_BUDGET_MB = 150
        const val IMAGE_CACHE_LIMIT_MB = 50
        private const val TAG = "MemoryBudgetController"
    }

    data class MemoryBudget(
        val maxActiveGeckoSessions: Int,
        val maxSuspendedTabs: Int,
        val uiMemoryCeilingMb: Int,
        val geckoViewBudgetMb: Int,
        val imageCacheLimitMb: Int,
        val isLowMemory: Boolean,
        val availableRamMb: Long,
        val totalRamGb: Double,
        val isLowRamDevice: Boolean,
        val enforcePhase: EnforcePhase
    )

    data class EvictionPriority(
        val priority: Int,
        val target: String
    )

    enum class EnforcePhase {
        NONE,
        MODERATE,
        CRITICAL
    }

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val cachedMemoryInfo = ActivityManager.MemoryInfo()
    private var lastRefreshTimeMs = 0L
    private val refreshIntervalMs = 5000L

    private var _isLowRamDevice: Boolean = false
    private var _totalRamGb: Double = 0.0
    private var _coreCount: Int = 0
    private var classified: Boolean = false

    private fun classifyDevice() {
        if (classified) return
        refreshMemoryInfo()
        _totalRamGb = cachedMemoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        _coreCount = Runtime.getRuntime().availableProcessors()
        _isLowRamDevice = activityManager.isLowRamDevice
        classified = true
        Log.d(TAG, "Device: ${_totalRamGb}GB RAM, $_coreCount cores, isLowRamDevice=$_isLowRamDevice")
    }

    fun isLowRamDevice(): Boolean {
        classifyDevice()
        return _isLowRamDevice
    }

    fun getTotalRamGb(): Double {
        classifyDevice()
        return _totalRamGb
    }

    fun getCoreCount(): Int {
        classifyDevice()
        return _coreCount
    }

    private fun refreshMemoryInfo() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTimeMs < refreshIntervalMs) return
        lastRefreshTimeMs = now
        activityManager.getMemoryInfo(cachedMemoryInfo)
    }

    private val availMemMb: Long get() = cachedMemoryInfo.availMem / (1024 * 1024)

    fun getCurrentBudget(): MemoryBudget {
        classifyDevice()
        refreshMemoryInfo()

        val isLowMemory = cachedMemoryInfo.lowMemory
        val availMb = availMemMb
        val lowRamDevice = _isLowRamDevice

        val phase = when {
            isLowMemory || availMb < 256 -> EnforcePhase.CRITICAL
            availMb < 512 || lowRamDevice -> EnforcePhase.MODERATE
            else -> EnforcePhase.NONE
        }

        val adjustedActiveTabs = if (isLowMemory || lowRamDevice || availMb < 512) {
            1
        } else {
            MAX_ACTIVE_TABS
        }

        val adjustedSuspendedTabs = when {
            isLowMemory -> 0
            lowRamDevice -> 0
            availMb < 512 -> 1
            else -> MAX_SUSPENDED_TABS
        }

        val adjustedGeckoBudget = when {
            isLowMemory -> (GECKOVIEW_BUDGET_MB * 0.5).toInt()
            lowRamDevice -> (GECKOVIEW_BUDGET_MB * 0.7).toInt()
            else -> GECKOVIEW_BUDGET_MB
        }

        val adjustedImageCache = when {
            isLowMemory -> (IMAGE_CACHE_LIMIT_MB * 0.3).toInt()
            lowRamDevice -> (IMAGE_CACHE_LIMIT_MB * 0.5).toInt()
            else -> IMAGE_CACHE_LIMIT_MB
        }

        val totalSessions = adjustedActiveTabs + adjustedSuspendedTabs

        return MemoryBudget(
            maxActiveGeckoSessions = totalSessions.coerceAtMost(MAX_TOTAL_SESSIONS),
            maxSuspendedTabs = adjustedSuspendedTabs,
            uiMemoryCeilingMb = UI_MEMORY_CEILING_MB,
            geckoViewBudgetMb = adjustedGeckoBudget,
            imageCacheLimitMb = adjustedImageCache,
            isLowMemory = isLowMemory,
            availableRamMb = availMb,
            totalRamGb = _totalRamGb,
            isLowRamDevice = lowRamDevice,
            enforcePhase = phase
        )
    }

    fun getEvictionPriorityOrder(): List<String> = listOf(
        "background_service_caches",
        "suspended_tab_ui_state",
        "history_thumbnails_cache",
        "image_cache",
        "non_essential_ui_prefetched"
    )

    fun assessMemoryPressure(): EnforcePhase {
        refreshMemoryInfo()
        return when {
            cachedMemoryInfo.lowMemory || availMemMb < 256 -> EnforcePhase.CRITICAL
            availMemMb < 512 || _isLowRamDevice -> EnforcePhase.MODERATE
            else -> EnforcePhase.NONE
        }
    }

    fun isUnderMemoryPressure(): Boolean {
        return assessMemoryPressure() != EnforcePhase.NONE
    }

    fun getAvailableRamMb(): Long {
        refreshMemoryInfo()
        return availMemMb
    }

}
