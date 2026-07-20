package com.jusdots.jusbrowse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB3A0FF),
    onPrimary = Color(0xFF1F0044),
    primaryContainer = Color(0xFF4F2D8F),
    onPrimaryContainer = Color(0xFFE8D5FF),
    secondary = Color(0xFFECB3FF),
    onSecondary = Color(0xFF3F005A),
    secondaryContainer = Color(0xFF5C1F7F),
    onSecondaryContainer = Color(0xFFF0D5FF),
    tertiary = Color(0xFFFF9ED0),
    onTertiary = Color(0xFF5F003F),
    tertiaryContainer = Color(0xFF7F1F5F),
    onTertiaryContainer = Color(0xFFFFD8EF),
    background = Color(0xFF0A0B0E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF13151A),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF1A1D26),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF6750A4)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE0FF),
    onPrimaryContainer = Color(0xFF3F008F),
    secondary = Color(0xFF9C6CB8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0D5FF),
    onSecondaryContainer = Color(0xFF3F005A),
    tertiary = Color(0xFFD96C9E),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8EF),
    onTertiaryContainer = Color(0xFF5F003F),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0EDF0),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFFCAC4D0),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFD0BCFF)
)

@Composable
fun JusBrowse2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themePreset: String = "SYSTEM",
    amoledBlackEnabled: Boolean = false,
    customColor: Color? = null,
    appFont: String = "SYSTEM",
    backgroundPreset: String = "NONE",
    uiVariant: String = BrowserUiVariant.DEFAULT.name,
    content: @Composable () -> Unit
) {
    val preset = try {
        BrowserTheme.valueOf(themePreset)
    } catch (e: Exception) {
        BrowserTheme.SYSTEM
    }

    val colorScheme = when (preset) {
        BrowserTheme.CUSTOM_COLOR -> {
            val seed = customColor ?: Color(0xFF607D8B)
            if (darkTheme) {
                val bg = Color(0xFF1A1A1A)
                darkColorScheme(
                    primary = seed,
                    onPrimary = Color.White,
                    primaryContainer = blend(seed, bg, 0.35f),
                    onPrimaryContainer = seed.copy(alpha = 0.9f),
                    secondary = blend(seed, bg, 0.65f),
                    onSecondary = Color.White,
                    secondaryContainer = blend(seed, bg, 0.25f),
                    onSecondaryContainer = seed.copy(alpha = 0.85f),
                    tertiary = blend(seed, Color.White, 0.4f),
                    onTertiary = bg,
                    tertiaryContainer = blend(seed, bg, 0.15f),
                    onTertiaryContainer = seed.copy(alpha = 0.7f),
                    background = bg,
                    onBackground = Color(0xFFE6E1E5),
                    surface = Color(0xFF242424),
                    onSurface = Color(0xFFE6E1E5),
                    surfaceVariant = Color(0xFF2F2F2F),
                    onSurfaceVariant = Color(0xFFCAC4D0),
                    outline = blend(seed, bg, 0.5f),
                    outlineVariant = blend(seed, bg, 0.2f),
                    error = Color(0xFFF2B8B5),
                    onError = Color(0xFF601410),
                    errorContainer = Color(0xFF8C1D18),
                    onErrorContainer = Color(0xFFF9DEDC),
                    inverseSurface = Color(0xFFE6E1E5),
                    inverseOnSurface = Color(0xFF313033),
                    inversePrimary = blend(seed, Color.White, 0.3f)
                )
            } else {
                val bg = Color.White
                lightColorScheme(
                    primary = seed,
                    onPrimary = Color.White,
                    primaryContainer = blend(seed, bg, 0.12f),
                    onPrimaryContainer = seed,
                    secondary = blend(seed, bg, 0.55f),
                    onSecondary = Color.White,
                    secondaryContainer = blend(seed, bg, 0.08f),
                    onSecondaryContainer = seed,
                    tertiary = blend(seed, bg, 0.4f),
                    onTertiary = Color.White,
                    tertiaryContainer = blend(seed, bg, 0.05f),
                    onTertiaryContainer = seed,
                    background = Color(0xFFF8F8F8),
                    onBackground = Color(0xFF1C1B1F),
                    surface = bg,
                    onSurface = Color(0xFF1C1B1F),
                    surfaceVariant = Color(0xFFF0EDF0),
                    onSurfaceVariant = Color(0xFF49454F),
                    outline = blend(seed, bg, 0.6f),
                    outlineVariant = blend(seed, bg, 0.3f),
                    error = Color(0xFFB3261E),
                    onError = Color.White,
                    errorContainer = Color(0xFFF9DEDC),
                    onErrorContainer = Color(0xFF410E0B),
                    inverseSurface = Color(0xFF313033),
                    inverseOnSurface = Color(0xFFF4EFF4),
                    inversePrimary = blend(seed, Color.White, 0.85f)
                )
            }
        }
        BrowserTheme.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
        BrowserTheme.VIVALDI_RED -> if (darkTheme) VivaldiRedDark else VivaldiRedLight
        BrowserTheme.OCEAN_BLUE -> if (darkTheme) OceanBlueDark else OceanBlueLight
        BrowserTheme.FOREST_GREEN -> if (darkTheme) ForestGreenDark else ForestGreenLight
        BrowserTheme.MIDNIGHT_PURPLE -> if (darkTheme) MidnightPurpleDark else MidnightPurpleLight
        BrowserTheme.SUNSET_ORANGE -> if (darkTheme) SunsetOrangeDark else SunsetOrangeLight
        BrowserTheme.ABYSS_BLACK -> if (darkTheme) AbyssBlackDark else AbyssBlackLight
        BrowserTheme.NORD_ICE -> if (darkTheme) NordIceDark else NordIceLight
        BrowserTheme.DRACULA -> if (darkTheme) DraculaDark else DraculaLight
        BrowserTheme.SOLARIZED -> if (darkTheme) SolarizedDark else SolarizedLight
        BrowserTheme.CYBERPUNK -> if (darkTheme) CyberpunkDark else CyberpunkLight
        BrowserTheme.MINT_FRESH -> if (darkTheme) MintFreshDark else MintFreshLight
        BrowserTheme.ROSE_GOLD -> if (darkTheme) RoseGoldDark else RoseGoldLight
        BrowserTheme.STRAIT_SAFE -> if (darkTheme) StraitSafeDark else StraitSafeLight
        BrowserTheme.SYSTEM -> {
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        }
    }

    val finalColorScheme = if (amoledBlackEnabled && darkTheme) {
        colorScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceVariant = Color.Black,
            surfaceContainer = Color.Black,
            surfaceContainerLow = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerHigh = Color.Black,
            surfaceContainerHighest = Color.Black
        )
    } else {
        colorScheme
    }

    val selectedAppFont = try {
        AppFont.valueOf(appFont)
    } catch (e: Exception) {
        AppFont.SYSTEM
    }

    val activeShapes = Shapes

    MaterialTheme(
        colorScheme = finalColorScheme,
        typography = getTypography(selectedAppFont.fontFamily),
        shapes = activeShapes
    ) {
        content()
    }
}

private fun blend(foreground: Color, background: Color, alpha: Float): Color {
    val a = alpha.coerceIn(0f, 1f)
    return Color(
        red = foreground.red * a + background.red * (1f - a),
        green = foreground.green * a + background.green * (1f - a),
        blue = foreground.blue * a + background.blue * (1f - a),
        alpha = 1f
    )
}
