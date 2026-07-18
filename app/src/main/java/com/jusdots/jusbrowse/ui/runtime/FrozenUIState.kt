package com.jusdots.jusbrowse.ui.runtime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Immutable
data class FrozenToolbarState(
    val widthPx: Int = 0,
    val heightPx: Int = 0,
    val pillWidthDp: Dp = Dp.Unspecified,
    val pillHeightDp: Dp = Dp.Unspecified,
    val cornerRadiusDp: Dp = Dp.Unspecified,
    val iconSizeDp: Dp = Dp.Unspecified,
    val iconBounds: List<Rect> = emptyList(),
    val addressBarOffsetX: Float = 0f,
    val addressBarWidth: Float = 0f,
    val securityStateKey: Int = 2
)

@Immutable
data class FrozenTabStripState(
    val chipWidths: Map<String, Dp> = emptyMap(),
    val chipPositions: Map<String, DpOffset> = emptyMap(),
    val totalWidth: Dp = Dp.Unspecified,
    val visibleChipCount: Int = 0,
    val scrollOffset: Dp = 0.dp
)

@Immutable
data class FrozenLayoutState(
    val toolbar: FrozenToolbarState = FrozenToolbarState(),
    val tabStrip: FrozenTabStripState = FrozenTabStripState(),
    val contentPadding: DpSize = DpSize(0.dp, 0.dp),
    val statusBarHeight: Dp = 0.dp,
    val navigationBarHeight: Dp = 0.dp,
    val keyboardHeight: Dp = 0.dp
)

@Immutable
data class FrozenSecurityState(
    val isSecure: Boolean = false,
    val stateKey: Int = 0,
    val animationPreset: String = "securityMorph"
)

@Immutable
data class FrozenPillState(
    val isExpanded: Boolean = false,
    val showMenu: Boolean = false,
    val widthDp: Dp = Dp.Unspecified,
    val heightDp: Dp = Dp.Unspecified,
    val cornerRadiusDp: Dp = Dp.Unspecified
)

@Stable
interface FrozenStateProvider {
    val layoutState: FrozenLayoutState
    val securityState: FrozenSecurityState
    val pillState: FrozenPillState
    fun invalidateLayout()
    fun invalidateSecurity()
    fun invalidatePill()
}

object FrozenDefaults {
    val LAYOUT = FrozenLayoutState()
    val SECURITY = FrozenSecurityState()
    val PILL = FrozenPillState()
}

class FrozenUIStateSnapshot(
    override val layoutState: FrozenLayoutState = FrozenDefaults.LAYOUT,
    override val securityState: FrozenSecurityState = FrozenDefaults.SECURITY,
    override val pillState: FrozenPillState = FrozenDefaults.PILL
) : FrozenStateProvider {
    private var layoutInvalid = false
    private var securityInvalid = false
    private var pillInvalid = false

    override fun invalidateLayout() { layoutInvalid = true }
    override fun invalidateSecurity() { securityInvalid = true }
    override fun invalidatePill() { pillInvalid = true }

    fun isLayoutValid(): Boolean = !layoutInvalid
    fun isSecurityValid(): Boolean = !securityInvalid
    fun isPillValid(): Boolean = !pillInvalid

    fun markLayoutClean() { layoutInvalid = false }
    fun markSecurityClean() { securityInvalid = false }
    fun markPillClean() { pillInvalid = false }
}


