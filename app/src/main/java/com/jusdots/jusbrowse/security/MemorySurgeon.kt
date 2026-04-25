package com.jusdots.jusbrowse.security

import android.app.ActivityManager
import android.content.Context
import android.util.Log

/**
 * Layer 12 Memory Stabilization Component
 * Calculates adaptive RAM budget to prevent OOM process kills.
 */
object MemorySurgeon {
    private const val TAG = "MemorySurgeon"
    
    // Target usage of available RAM (25% as suggested by the user)
    private const val TARGET_RATIO = 0.25f
    
    // Average size of a GeckoSession process on Android (observed ~150MB-200MB)
    private const val AVG_SESSION_SIZE_MB = 180L

    enum class DeviceMode {
        HIGH_SPEC,
        LOW_SPEC
    }

    private var currentMode: DeviceMode? = null

    fun getDeviceMode(context: Context): DeviceMode {
        if (currentMode != null) return currentMode!!

        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        val cores = Runtime.getRuntime().availableProcessors()

        // 3.5GB accounts for system reserved memory on 4GB devices
        currentMode = if (totalMemGb >= 3.5 && cores >= 4) {
            DeviceMode.HIGH_SPEC
        } else {
            DeviceMode.LOW_SPEC
        }

        Log.d(TAG, "Hardware Detection: Total RAM = String.format(\"%.1f\", totalMemGb)GB, Cores = $cores. Mode = $currentMode")
        return currentMode!!
    }

    /**
     * Calculates the maximum number of active (rendering) GeckoSessions 
     * the system can handle right now.
     */
    fun calculateActiveSessionBudget(context: Context): Int {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        
        val availMemMb = memoryInfo.availMem / (1024 * 1024)
        val totalMemMb = memoryInfo.totalMem / (1024 * 1024)
        
        val mode = getDeviceMode(context)
        
        // High Spec devices can use more of the available RAM before aggressively killing tabs
        val targetRatio = if (mode == DeviceMode.HIGH_SPEC) 0.40f else 0.25f
        
        // Calculate budget based on available RAM
        val budgetMb = (availMemMb * targetRatio).toLong()
        
        // Minimum 1 session (active tab)
        // High spec allowed up to 12 active background tabs, Low spec maxes at 6.
        val maxSessions = if (mode == DeviceMode.HIGH_SPEC) 12 else 6
        val minSessions = if (mode == DeviceMode.HIGH_SPEC) 3 else 1
        
        val sessionCount = (budgetMb / AVG_SESSION_SIZE_MB).toInt().coerceIn(minSessions, maxSessions)
        
        Log.d(TAG, "Memory: Mode=$mode, Avail=${availMemMb}MB, Budget=${budgetMb}MB -> SessionLimit=$sessionCount")
        
        // If system is already in low-memory state, restrict to 1 active session
        if (memoryInfo.lowMemory) {
            return 1
        }
        
        return sessionCount
    }

    /**
     * Checks if we are currently under severe memory pressure.
     */
    fun isLowMemory(context: Context): Boolean {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.lowMemory
    }
}
