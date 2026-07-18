package com.jusdots.jusbrowse.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class BrowserTheme {
    SYSTEM,
    MATERIAL_YOU,
    CUSTOM_COLOR,
    VIVALDI_RED,
    OCEAN_BLUE,
    FOREST_GREEN,
    MIDNIGHT_PURPLE,
    SUNSET_ORANGE,
    ABYSS_BLACK,
    NORD_ICE,
    DRACULA,
    SOLARIZED,
    CYBERPUNK,
    MINT_FRESH,
    ROSE_GOLD,
    STRAIT_SAFE
}

// Helper to get the primary preview color for each theme (used in Settings)
fun BrowserTheme.previewColor(customColor: Color? = null): Color = when (this) {
    BrowserTheme.VIVALDI_RED -> Color(0xFFD32F2F)
    BrowserTheme.OCEAN_BLUE -> Color(0xFF0288D1)
    BrowserTheme.FOREST_GREEN -> Color(0xFF388E3C)
    BrowserTheme.MIDNIGHT_PURPLE -> Color(0xFF7B1FA2)
    BrowserTheme.SUNSET_ORANGE -> Color(0xFFF57C00)
    BrowserTheme.ABYSS_BLACK -> Color(0xFF000000)
    BrowserTheme.NORD_ICE -> Color(0xFF5E81AC)
    BrowserTheme.DRACULA -> Color(0xFFBD93F9)
    BrowserTheme.SOLARIZED -> Color(0xFF268BD2)
    BrowserTheme.CYBERPUNK -> Color(0xFFFF00FF)
    BrowserTheme.MINT_FRESH -> Color(0xFF00BFA5)
    BrowserTheme.ROSE_GOLD -> Color(0xFFB76E79)
    BrowserTheme.STRAIT_SAFE -> Color(0xFF1A73E8)
    BrowserTheme.SYSTEM -> Color(0xFF8B5CF6)
    BrowserTheme.MATERIAL_YOU -> Color(0xFF6750A4)
    BrowserTheme.CUSTOM_COLOR -> customColor ?: Color(0xFF607D8B)
}

// =========================================================================
// VIVALDI RED
// =========================================================================
val VivaldiRedLight = lightColorScheme(
    primary = Color(0xFFD32F2F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF7F0000),
    secondary = Color(0xFFB71C1C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFCDD2),
    onSecondaryContainer = Color(0xFF5F0000),
    tertiary = Color(0xFFD81B60),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF0F0),
    onTertiaryContainer = Color(0xFF880E4F),
    background = Color(0xFFFFEBEE),
    onBackground = Color(0xFF2D0000),
    surface = Color.White,
    onSurface = Color(0xFF2D0000),
    surfaceVariant = Color(0xFFFFF0F0),
    onSurfaceVariant = Color(0xFF5F3E3E),
    outline = Color(0xFFD32F2F).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFD32F2F).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val VivaldiRedDark = darkColorScheme(
    primary = Color(0xFFE57373),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB71C1C),
    onPrimaryContainer = Color(0xFFFFCDD2),
    secondary = Color(0xFFEF5350),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF7F0000),
    onSecondaryContainer = Color(0xFFFFCDD2),
    tertiary = Color(0xFFF06292),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF880E4F),
    onTertiaryContainer = Color(0xFFFFF0F0),
    background = Color(0xFF2C2C2C),
    onBackground = Color(0xFFFFEBEE),
    surface = Color(0xFF3E3E3E),
    onSurface = Color(0xFFFFEBEE),
    surfaceVariant = Color(0xFF5F3E3E),
    onSurfaceVariant = Color(0xFFFFCDD2),
    outline = Color(0xFFE57373).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFE57373).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// OCEAN BLUE
// =========================================================================
val OceanBlueLight = lightColorScheme(
    primary = Color(0xFF0288D1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB3E5FC),
    onPrimaryContainer = Color(0xFF003F5F),
    secondary = Color(0xFF0277BD),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB3E5FC),
    onSecondaryContainer = Color(0xFF002F4F),
    tertiary = Color(0xFF00796B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = Color(0xFF00332F),
    background = Color(0xFFE1F5FE),
    onBackground = Color(0xFF001F2F),
    surface = Color.White,
    onSurface = Color(0xFF001F2F),
    surfaceVariant = Color(0xFFE1F5FE),
    onSurfaceVariant = Color(0xFF3F5F6F),
    outline = Color(0xFF0288D1).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF0288D1).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val OceanBlueDark = darkColorScheme(
    primary = Color(0xFF29B6F6),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0277BD),
    onPrimaryContainer = Color(0xFFB3E5FC),
    secondary = Color(0xFF4FC3F7),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003F5F),
    onSecondaryContainer = Color(0xFFB3E5FC),
    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = Color(0xFFB2DFDB),
    background = Color(0xFF102027),
    onBackground = Color(0xFFE1F5FE),
    surface = Color(0xFF263238),
    onSurface = Color(0xFFE1F5FE),
    surfaceVariant = Color(0xFF3F5F6F),
    onSurfaceVariant = Color(0xFFB3E5FC),
    outline = Color(0xFF29B6F6).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF29B6F6).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// FOREST GREEN
// =========================================================================
val ForestGreenLight = lightColorScheme(
    primary = Color(0xFF388E3C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF0F4F14),
    tertiary = Color(0xFF558B2F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF1F8E9),
    onTertiaryContainer = Color(0xFF2A4F10),
    background = Color(0xFFE8F5E9),
    onBackground = Color(0xFF0F2F10),
    surface = Color.White,
    onSurface = Color(0xFF0F2F10),
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF3F5F40),
    outline = Color(0xFF388E3C).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF388E3C).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val ForestGreenDark = darkColorScheme(
    primary = Color(0xFF66BB6A),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = Color(0xFFC8E6C9),
    tertiary = Color(0xFF8BC34A),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF33691E),
    onTertiaryContainer = Color(0xFFDCEDC8),
    background = Color(0xFF1B5E20),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF2E7D32),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF3F5F40),
    onSurfaceVariant = Color(0xFFC8E6C9),
    outline = Color(0xFF66BB6A).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF66BB6A).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// MIDNIGHT PURPLE
// =========================================================================
val MidnightPurpleLight = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1BEE7),
    onPrimaryContainer = Color(0xFF3F005F),
    secondary = Color(0xFF6A1B9A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1BEE7),
    onSecondaryContainer = Color(0xFF2F004F),
    tertiary = Color(0xFFAD1457),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCE4EC),
    onTertiaryContainer = Color(0xFF5F002F),
    background = Color(0xFFF3E5F5),
    onBackground = Color(0xFF1F002F),
    surface = Color.White,
    onSurface = Color(0xFF1F002F),
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = Color(0xFF5F3F6F),
    outline = Color(0xFF7B1FA2).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF7B1FA2).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val MidnightPurpleDark = darkColorScheme(
    primary = Color(0xFFAB47BC),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6A1B9A),
    onPrimaryContainer = Color(0xFFE1BEE7),
    secondary = Color(0xFFBA68C8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3F005F),
    onSecondaryContainer = Color(0xFFE1BEE7),
    tertiary = Color(0xFFEC407A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5F002F),
    onTertiaryContainer = Color(0xFFFCE4EC),
    background = Color(0xFF120022),
    onBackground = Color(0xFFF3E5F5),
    surface = Color(0xFF240046),
    onSurface = Color(0xFFF3E5F5),
    surfaceVariant = Color(0xFF5F3F6F),
    onSurfaceVariant = Color(0xFFE1BEE7),
    outline = Color(0xFFAB47BC).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFAB47BC).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// SUNSET ORANGE
// =========================================================================
val SunsetOrangeLight = lightColorScheme(
    primary = Color(0xFFF57C00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF6F3F00),
    secondary = Color(0xFFEF6C00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF5F2F00),
    tertiary = Color(0xFFBF360C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFBE9E7),
    onTertiaryContainer = Color(0xFF5F1000),
    background = Color(0xFFFFF3E0),
    onBackground = Color(0xFF2F1F00),
    surface = Color.White,
    onSurface = Color(0xFF2F1F00),
    surfaceVariant = Color(0xFFFFF3E0),
    onSurfaceVariant = Color(0xFF6F4F2F),
    outline = Color(0xFFF57C00).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFF57C00).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val SunsetOrangeDark = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFEF6C00),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFFFF9800),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5F2F00),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFFFF7043),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF5F1000),
    onTertiaryContainer = Color(0xFFFBE9E7),
    background = Color(0xFF3E2723),
    onBackground = Color(0xFFFFF3E0),
    surface = Color(0xFF4E342E),
    onSurface = Color(0xFFFFF3E0),
    surfaceVariant = Color(0xFF6F4F2F),
    onSurfaceVariant = Color(0xFFFFE0B2),
    outline = Color(0xFFFFB74D).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFFFB74D).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// ABYSS BLACK - True AMOLED Black
// =========================================================================
val AbyssBlackLight = lightColorScheme(
    primary = Color(0xFF212121),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBDBDBD),
    onPrimaryContainer = Color(0xFF1F1F1F),
    secondary = Color(0xFF424242),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBDBDBD),
    onSecondaryContainer = Color(0xFF2F2F2F),
    tertiary = Color(0xFF616161),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E0E0),
    onTertiaryContainer = Color(0xFF3F3F3F),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1F1F1F),
    surface = Color.White,
    onSurface = Color(0xFF1F1F1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF5F5F5F),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD).copy(alpha = 0.5f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val AbyssBlackDark = darkColorScheme(
    primary = Color(0xFFBDBDBD),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF616161),
    onPrimaryContainer = Color(0xFFE0E0E0),
    secondary = Color(0xFF757575),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF424242),
    onSecondaryContainer = Color(0xFFBDBDBD),
    tertiary = Color(0xFF9E9E9E),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF616161),
    onTertiaryContainer = Color(0xFFE0E0E0),
    background = Color(0xFF000000), // True black
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2F2F2F),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF424242),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// NORD ICE
// =========================================================================
val NordIceLight = lightColorScheme(
    primary = Color(0xFF5E81AC),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8DEE9),
    onPrimaryContainer = Color(0xFF2E3440),
    secondary = Color(0xFF81A1C1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8DEE9),
    onSecondaryContainer = Color(0xFF3B4252),
    tertiary = Color(0xFF8FBCBB),
    onTertiary = Color(0xFF2E3440),
    tertiaryContainer = Color(0xFFECEFF4),
    onTertiaryContainer = Color(0xFF4C566A),
    background = Color(0xFFECEFF4),
    onBackground = Color(0xFF2E3440),
    surface = Color(0xFFE5E9F0),
    onSurface = Color(0xFF2E3440),
    surfaceVariant = Color(0xFFD8DEE9),
    onSurfaceVariant = Color(0xFF4C566A),
    outline = Color(0xFF81A1C1).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF81A1C1).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val NordIceDark = darkColorScheme(
    primary = Color(0xFF88C0D0),
    onPrimary = Color(0xFF2E3440),
    primaryContainer = Color(0xFF5E81AC),
    onPrimaryContainer = Color(0xFFD8DEE9),
    secondary = Color(0xFF81A1C1),
    onSecondary = Color(0xFF2E3440),
    secondaryContainer = Color(0xFF4C566A),
    onSecondaryContainer = Color(0xFFD8DEE9),
    tertiary = Color(0xFF8FBCBB),
    onTertiary = Color(0xFF2E3440),
    tertiaryContainer = Color(0xFF4C566A),
    onTertiaryContainer = Color(0xFFD8DEE9),
    background = Color(0xFF2E3440),
    onBackground = Color(0xFFD8DEE9),
    surface = Color(0xFF3B4252),
    onSurface = Color(0xFFD8DEE9),
    surfaceVariant = Color(0xFF4C566A),
    onSurfaceVariant = Color(0xFFD8DEE9),
    outline = Color(0xFF81A1C1).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF81A1C1).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// DRACULA
// =========================================================================
val DraculaLight = lightColorScheme(
    primary = Color(0xFFBD93F9),
    onPrimary = Color(0xFF282A36),
    primaryContainer = Color(0xFFE8D5FF),
    onPrimaryContainer = Color(0xFF3F2F5F),
    secondary = Color(0xFFFF79C6),
    onSecondary = Color(0xFF282A36),
    secondaryContainer = Color(0xFFFFE0F0),
    onSecondaryContainer = Color(0xFF5F1F4F),
    tertiary = Color(0xFF50FA7B),
    onTertiary = Color(0xFF003310),
    tertiaryContainer = Color(0xFFD0FFD8),
    onTertiaryContainer = Color(0xFF003F10),
    background = Color(0xFFF8F8F2),
    onBackground = Color(0xFF282A36),
    surface = Color.White,
    onSurface = Color(0xFF282A36),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF5F5F6F),
    outline = Color(0xFFBD93F9).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFBD93F9).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val DraculaDark = darkColorScheme(
    primary = Color(0xFFBD93F9),
    onPrimary = Color(0xFF282A36),
    primaryContainer = Color(0xFF5F3F8F),
    onPrimaryContainer = Color(0xFFE8D5FF),
    secondary = Color(0xFFFF79C6),
    onSecondary = Color(0xFF282A36),
    secondaryContainer = Color(0xFF5F1F4F),
    onSecondaryContainer = Color(0xFFFFE0F0),
    tertiary = Color(0xFF50FA7B),
    onTertiary = Color(0xFF003F10),
    tertiaryContainer = Color(0xFF004F14),
    onTertiaryContainer = Color(0xFFD0FFD8),
    background = Color(0xFF282A36),
    onBackground = Color(0xFFF8F8F2),
    surface = Color(0xFF44475A),
    onSurface = Color(0xFFF8F8F2),
    surfaceVariant = Color(0xFF5F5F6F),
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color(0xFFBD93F9).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFBD93F9).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// SOLARIZED
// =========================================================================
val SolarizedLight = lightColorScheme(
    primary = Color(0xFF268BD2),
    onPrimary = Color(0xFFFDF6E3),
    primaryContainer = Color(0xFFD6EDFF),
    onPrimaryContainer = Color(0xFF0F3F5F),
    secondary = Color(0xFF2AA198),
    onSecondary = Color(0xFFFDF6E3),
    secondaryContainer = Color(0xFFE0F8F5),
    onSecondaryContainer = Color(0xFF003F3F),
    tertiary = Color(0xFFCB4B16),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0CF),
    onTertiaryContainer = Color(0xFF5F1F00),
    background = Color(0xFFFDF6E3),
    onBackground = Color(0xFF073642),
    surface = Color(0xFFEEE8D5),
    onSurface = Color(0xFF073642),
    surfaceVariant = Color(0xFFE0D8C5),
    onSurfaceVariant = Color(0xFF5F5F4F),
    outline = Color(0xFF268BD2).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF268BD2).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val SolarizedDark = darkColorScheme(
    primary = Color(0xFF268BD2),
    onPrimary = Color(0xFF002B36),
    primaryContainer = Color(0xFF0F5F8F),
    onPrimaryContainer = Color(0xFFD6EDFF),
    secondary = Color(0xFF2AA198),
    onSecondary = Color(0xFF002B36),
    secondaryContainer = Color(0xFF004F4F),
    onSecondaryContainer = Color(0xFFE0F8F5),
    tertiary = Color(0xFFCB4B16),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5F2F00),
    onTertiaryContainer = Color(0xFFFFE0CF),
    background = Color(0xFF002B36),
    onBackground = Color(0xFFFDF6E3),
    surface = Color(0xFF073642),
    onSurface = Color(0xFFFDF6E3),
    surfaceVariant = Color(0xFF3F4F4F),
    onSurfaceVariant = Color(0xFFE0D8C5),
    outline = Color(0xFF268BD2).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF268BD2).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// CYBERPUNK
// =========================================================================
val CyberpunkLight = lightColorScheme(
    primary = Color(0xFFFF00FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFD0FF),
    onPrimaryContainer = Color(0xFF5F005F),
    secondary = Color(0xFFFFFF00),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFFFCF),
    onSecondaryContainer = Color(0xFF5F5F00),
    tertiary = Color(0xFF00E5FF),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFD0FBFF),
    onTertiaryContainer = Color(0xFF004F5F),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF0D0D0D),
    surface = Color.White,
    onSurface = Color(0xFF0D0D0D),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF5F5F5F),
    outline = Color(0xFFFF00FF).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFFF00FF).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val CyberpunkDark = darkColorScheme(
    primary = Color(0xFFFF00FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF5F005F),
    onPrimaryContainer = Color(0xFFFFD0FF),
    secondary = Color(0xFFFFFF00),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5F5F00),
    onSecondaryContainer = Color(0xFFFFFFCF),
    tertiary = Color(0xFF00E5FF),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004F5F),
    onTertiaryContainer = Color(0xFFD0FBFF),
    background = Color(0xFF0D0D0D),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1A1A2E),
    onSurface = Color(0xFFE0E0FF),
    surfaceVariant = Color(0xFF2F2F4F),
    onSurfaceVariant = Color(0xFFC0C0E0),
    outline = Color(0xFFFF00FF).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFFF00FF).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// MINT FRESH
// =========================================================================
val MintFreshLight = lightColorScheme(
    primary = Color(0xFF00BFA5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF003F3F),
    secondary = Color(0xFF1DE9B6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F8EC),
    onSecondaryContainer = Color(0xFF004F3F),
    tertiary = Color(0xFF26A69A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = Color(0xFF003F3F),
    background = Color(0xFFE0F2F1),
    onBackground = Color(0xFF00332F),
    surface = Color.White,
    onSurface = Color(0xFF00332F),
    surfaceVariant = Color(0xFFE0F2F1),
    onSurfaceVariant = Color(0xFF3F5F5F),
    outline = Color(0xFF00BFA5).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF00BFA5).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val MintFreshDark = darkColorScheme(
    primary = Color(0xFF64FFDA),
    onPrimary = Color(0xFF003D33),
    primaryContainer = Color(0xFF00BFA5),
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondary = Color(0xFF1DE9B6),
    onSecondary = Color(0xFF003D33),
    secondaryContainer = Color(0xFF004F3F),
    onSecondaryContainer = Color(0xFFD0F8EC),
    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color(0xFF003D33),
    tertiaryContainer = Color(0xFF004F4F),
    onTertiaryContainer = Color(0xFFE0F2F1),
    background = Color(0xFF004D40),
    onBackground = Color(0xFFE0F2F1),
    surface = Color(0xFF00695C),
    onSurface = Color(0xFFE0F2F1),
    surfaceVariant = Color(0xFF3F5F5F),
    onSurfaceVariant = Color(0xFFB2DFDB),
    outline = Color(0xFF64FFDA).copy(alpha = 0.5f),
    outlineVariant = Color(0xFF64FFDA).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// ROSE GOLD
// =========================================================================
val RoseGoldLight = lightColorScheme(
    primary = Color(0xFFB76E79),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF8E0E3),
    onPrimaryContainer = Color(0xFF4F1F2F),
    secondary = Color(0xFFD4A5A5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF0F0),
    onSecondaryContainer = Color(0xFF4F2F2F),
    tertiary = Color(0xFFC2185B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCE4EC),
    onTertiaryContainer = Color(0xFF5F002F),
    background = Color(0xFFFFF0F0),
    onBackground = Color(0xFF2F1F1F),
    surface = Color.White,
    onSurface = Color(0xFF2F1F1F),
    surfaceVariant = Color(0xFFFFF0F0),
    onSurfaceVariant = Color(0xFF5F3F3F),
    outline = Color(0xFFB76E79).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFB76E79).copy(alpha = 0.2f),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF0F0F4),
    inversePrimary = Color(0xFFB3A0FF)
)

val RoseGoldDark = darkColorScheme(
    primary = Color(0xFFE8B4B8),
    onPrimary = Color(0xFF3D2B2B),
    primaryContainer = Color(0xFFB76E79),
    onPrimaryContainer = Color(0xFFF8E0E3),
    secondary = Color(0xFFD4A5A5),
    onSecondary = Color(0xFF3D2B2B),
    secondaryContainer = Color(0xFF5F3F3F),
    onSecondaryContainer = Color(0xFFFEF0F0),
    tertiary = Color(0xFFF06292),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF5F002F),
    onTertiaryContainer = Color(0xFFFCE4EC),
    background = Color(0xFF2D1F1F),
    onBackground = Color(0xFFFFF0F0),
    surface = Color(0xFF3D2B2B),
    onSurface = Color(0xFFFFF0F0),
    surfaceVariant = Color(0xFF5F3F3F),
    onSurfaceVariant = Color(0xFFF8E0E3),
    outline = Color(0xFFE8B4B8).copy(alpha = 0.5f),
    outlineVariant = Color(0xFFE8B4B8).copy(alpha = 0.2f),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF6750A4)
)

// =========================================================================
// STRAIT SAFE — Material 3 defaults calibrated for the Safe (Chrome-style) chrome
// 70% familiar Chrome/Samsung blue, 30% JusBrowse warmth via tertiary + secondary
// =========================================================================
val StraitSafeLight = lightColorScheme(
    primary = Color(0xFF1A73E8),               // Chrome blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E3FD),     // Chrome blue container
    onPrimaryContainer = Color(0xFF001A41),
    secondary = Color(0xFF8AB4F8),            // Chrome light blue
    onSecondary = Color(0xFF002F66),
    secondaryContainer = Color(0xFFD7E3FB),
    onSecondaryContainer = Color(0xFF001A41),
    tertiary = Color(0xFFB85B6B),              // JusBrowse warm accent
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9DF),
    onTertiaryContainer = Color(0xFF3F0017),
    background = Color(0xFFFFFFFF),           // true white (not warm-tinted)
    onBackground = Color(0xFF1F1F1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F1F1F),
    surfaceVariant = Color(0xFFF1F3F4),       // Chrome grey
    onSurfaceVariant = Color(0xFF444746),
    outline = Color(0xFF747775),
    outlineVariant = Color(0xFFE3E5E6),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F3F4),
    inversePrimary = Color(0xFFA8C7FA)
)

val StraitSafeDark = darkColorScheme(
    primary = Color(0xFFA8C7FA),               // Chrome dark blue
    onPrimary = Color(0xFF002F65),
    primaryContainer = Color(0xFF0B57D0),
    onPrimaryContainer = Color(0xFFD3E3FD),
    secondary = Color(0xFF8AB4F8),
    onSecondary = Color(0xFF002F66),
    secondaryContainer = Color(0xFF1F3F73),
    onSecondaryContainer = Color(0xFFD7E3FB),
    tertiary = Color(0xFFE8A4AD),              // JusBrowse warm accent (dark)
    onTertiary = Color(0xFF5F1126),
    tertiaryContainer = Color(0xFF7F2D3D),
    onTertiaryContainer = Color(0xFFFFD9DF),
    background = Color(0xFF1F1F1F),
    onBackground = Color(0xFFE3E3E3),
    surface = Color(0xFF1F1F1F),
    onSurface = Color(0xFFE3E3E3),
    surfaceVariant = Color(0xFF2D2E2F),
    onSurfaceVariant = Color(0xFFC4C7C5),
    outline = Color(0xFF8E918F),
    outlineVariant = Color(0xFF444746),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE3E3E3),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF1A73E8)
)
