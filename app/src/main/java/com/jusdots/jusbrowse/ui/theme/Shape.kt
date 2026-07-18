package com.jusdots.jusbrowse.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),    // Menus, tooltips, small chips
    small      = RoundedCornerShape(16.dp),   // Chips, input fields
    medium     = RoundedCornerShape(24.dp),   // Cards, dialogs
    large      = RoundedCornerShape(32.dp),   // Large containers, sheets
    extraLarge = RoundedCornerShape(36.dp)    // Pill-style floating elements
)

// Named shape aliases for expressive surfaces
val PillShape        = RoundedCornerShape(percent = 50) // true pill
val GlassCardShape   = RoundedCornerShape(32.dp)        // menu card
val SquircleShape    = RoundedCornerShape(24.dp)        // icon surfaces
val SheetShape       = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp)

/**
 * Material 3 default-leaning shape scale for the Safe (Chrome-style) chrome.
 * 12dp is the M3 standard for components like Cards and TextFields. 28dp
 * is the M3 standard for large surfaces like Dialogs and NavigationBar
 * internal items. 4dp / 8dp / 16dp are M3 component defaults.
 */
val SafeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
