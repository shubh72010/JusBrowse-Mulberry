package com.jusdots.jusbrowse.ui.theme

import androidx.compose.ui.graphics.Color

// ── Semantic security colors ─────────────────────────────────────────────
// These stay FIXED regardless of theme — they convey SECURITY meaning:
//   green = safe, red = danger, amber = warning
// Changing these with the theme would confuse users about security state.
val SecureGreen   = Color(0xFF4CAF50)
val InsecureRed   = Color(0xFFEF5350)
val WarningAmber  = Color(0xFFFF9800)

val SecureGreenContainer   = Color(0x1A4CAF50)
val InsecureRedContainer   = Color(0x1AEF5350)

// ── Container identity colors (fixed across themes) ──────────────────────
// These let users visually distinguish container types regardless of theme.
val ContainerWork     = Color(0xFF4285F4)
val ContainerPersonal = Color(0xFF34A853)
val ContainerBanking  = Color(0xFFFBBC05)
val ContainerSandbox  = Color(0xFFEA4335)
val ContainerShopping = Color(0xFFE91E63)
