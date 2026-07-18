package com.jusdots.jusbrowse.ui.runtime

import android.content.ComponentCallbacks2
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.jusdots.jusbrowse.StraitArchitecture

class StraitUIRuntime(private val context: Context) {

    val animationCache: PrecomputedAnimationCache = PrecomputedAnimationCache()
    val layoutCache: LayoutMetricsCache = LayoutMetricsCache()
    val visualAssets: CachedBackgroundAssets = CachedBackgroundAssets()
    val frozenState: FrozenUIStateSnapshot = FrozenUIStateSnapshot()
    val snapshotStorage: UISnapshotStorage = UISnapshotStorage()

    private var isLowRamDevice = false
    private var displayWidthPx = 0
    private var displayHeightPx = 0
    private var density = 2f
    private var userReducedAnimations = false

    data class DeviceMetrics(
        val widthPx: Int,
        val heightPx: Int,
        val density: Float,
        val isLowRam: Boolean
    )

    fun initialize(metrics: DeviceMetrics) {
        isLowRamDevice = metrics.isLowRam
        displayWidthPx = metrics.widthPx
        displayHeightPx = metrics.heightPx
        density = metrics.density
    }

    fun isLowEnd(): Boolean = isLowRamDevice

    fun shouldUseStaticAnimations(): Boolean = isLowRamDevice || userReducedAnimations

    fun setUserReducedAnimations(enabled: Boolean) {
        userReducedAnimations = enabled
    }

    fun shouldCacheBackground(): Boolean = true

    fun collectSnapshot(): UISnapshot {
        val layout = frozenState.layoutState
        return UISnapshot(
            toolbarWidthPx = layout.toolbar.widthPx,
            toolbarHeightPx = layout.toolbar.heightPx,
            pillWidthPx = layout.toolbar.pillWidthDp.value * density,
            pillHeightPx = layout.toolbar.pillHeightDp.value * density,
            tabChipCount = layout.tabStrip.visibleChipCount,
            activeTabChipIndex = 0,
            tabChipWidthsPx = layout.tabStrip.chipWidths.values.map { it.value * density }.toFloatArray(),
            securityStateKey = layout.toolbar.securityStateKey,
            animationPresets = mapOf(
                "pillExpand" to 200,
                "pillCollapse" to 180,
                "tabSelect" to 200
            ),
            bottomBarOffsetPx = 0f,
            displayDensity = density,
            displayWidthPx = displayWidthPx,
            displayHeightPx = displayHeightPx
        )
    }

    fun pushSnapshot() {
        snapshotStorage.push(collectSnapshot())
    }

    fun onTrimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            layoutCache.clear()
            animationCache.clear()
        }
        if (level >= ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            visualAssets.clear()
            frozenState.invalidateLayout()
        }
    }

    fun onConfigurationChanged() {
        frozenState.invalidateLayout()
    }

    companion object {
        @Volatile
        private var instance: StraitUIRuntime? = null

        fun getInstance(context: Context): StraitUIRuntime {
            return instance ?: synchronized(this) {
                instance ?: StraitUIRuntime(context.applicationContext).also { instance = it }
            }
        }

        fun resetInstance() {
            instance = null
        }
    }
}

class PrecomputedAnimationCache {
    private val cache = mutableMapOf<String, PrecomputedCurve>()

    fun get(name: String): PrecomputedCurve {
        return cache.getOrPut(name) {
            PrecomputedAnimations.DEFAULT_PRESETS[name] ?: PrecomputedAnimations.easeOutCubic()
        }
    }

    fun getAllPresets(): Map<String, PrecomputedCurve> = PrecomputedAnimations.DEFAULT_PRESETS

    fun clear() { cache.clear() }
}

@Composable
fun rememberStraitUIRuntime(): StraitUIRuntime {
    val context = LocalContext.current
    return remember(context) { StraitUIRuntime.getInstance(context) }
}

@Composable
fun StraitUIRuntimeInitializer(
    strait: StraitArchitecture,
    runtime: StraitUIRuntime = rememberStraitUIRuntime()
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current

    val metrics = remember(config) {
        StraitUIRuntime.DeviceMetrics(
            widthPx = config.screenWidthDp * (config.densityDpi / 160f).toInt(),
            heightPx = config.screenHeightDp * (config.densityDpi / 160f).toInt(),
            density = config.densityDpi / 160f,
            isLowRam = context.getSystemService(Context.ACTIVITY_SERVICE)
                ?.let { service ->
                    try {
                        val am = service as android.app.ActivityManager
                        am.isLowRamDevice
                    } catch (e: Exception) { false }
                } ?: false
        )
    }

    DisposableEffect(Unit) {
        runtime.initialize(metrics)
        runtime.pushSnapshot()

        onDispose {
            if (runtime === StraitUIRuntime.getInstance(context)) {
                StraitUIRuntime.resetInstance()
            }
        }
    }
}

object StraitUIAnimations {
    fun pillExpand(runtime: StraitUIRuntime): PrecomputedCurve {
        return if (runtime.shouldUseStaticAnimations()) {
            PrecomputedAnimations.scalePulse()
        } else {
            runtime.animationCache.get("pillExpand")
        }
    }

    fun pillCollapse(runtime: StraitUIRuntime): PrecomputedCurve {
        return if (runtime.shouldUseStaticAnimations()) {
            PrecomputedAnimations.fadeOut(120, 6)
        } else {
            runtime.animationCache.get("pillCollapse")
        }
    }

    fun tabSelect(runtime: StraitUIRuntime): PrecomputedCurve {
        return if (runtime.shouldUseStaticAnimations()) {
            PrecomputedAnimations.tabSwitchSlide(100, 6)
        } else {
            runtime.animationCache.get("tabSelect")
        }
    }

    fun securityMorph(runtime: StraitUIRuntime): PrecomputedCurve {
        return if (runtime.shouldUseStaticAnimations()) {
            PrecomputedAnimations.fadeIn(100, 6)
        } else {
            runtime.animationCache.get("securityMorph")
        }
    }

    fun bottomBar(runtime: StraitUIRuntime): PrecomputedCurve {
        return if (runtime.shouldUseStaticAnimations()) {
            PrecomputedAnimations.fadeIn(120, 8)
        } else {
            runtime.animationCache.get("bottomBarReveal")
        }
    }
}
