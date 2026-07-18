package com.jusdots.jusbrowse.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Top-level chrome style for the browser.
 *
 * - [DEFAULT] preserves the existing JusBrowse chrome (floating pill, animated
 *   backgrounds, transformable stickers, freeform workspace, snap-pill menu).
 * - [SAFE] renders a Chrome / Samsung Internet–style chrome: Material 3
 *   [TopAppBar] at the top, [NavigationBar] at the bottom, conventional tab
 *   strip, no floating shapes, no animated chrome. Tab content, security
 *   features, and persistence are unchanged.
 */
enum class BrowserUiVariant(val displayName: String) {
    DEFAULT("Default JusBrowse"),
    SAFE("Safe (Chrome-style)"),
}

fun BrowserUiVariant.previewColor(): Color = when (this) {
    BrowserUiVariant.DEFAULT -> Color(0xFF8B5CF6)
    BrowserUiVariant.SAFE -> Color(0xFF1A73E8)
}
