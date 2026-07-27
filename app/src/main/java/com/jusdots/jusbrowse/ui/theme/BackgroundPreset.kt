package com.jusdots.jusbrowse.ui.theme

import androidx.compose.ui.graphics.Color

enum class BackgroundPreset(
    val displayName: String,
    val colors: List<Color> = emptyList(),
    val imageFile: String? = null
) {
    NONE(
        displayName = "None",
        colors = emptyList()
    ),
    WALLPAPER_BLUE1(
        displayName = "Blue 1",
        imageFile = "blue1.webp"
    ),
    WALLPAPER_BLUE2(
        displayName = "Blue 2",
        imageFile = "blue2.webp"
    ),
    WALLPAPER_COLOURFUL1(
        displayName = "Colourful 1",
        imageFile = "colourful1.webp"
    ),
    WALLPAPER_DARK1(
        displayName = "Dark 1",
        imageFile = "dark1.webp"
    ),
    WALLPAPER_GOLDEN1(
        displayName = "Golden 1",
        imageFile = "golden1.webp"
    ),
    WALLPAPER_GREEN1(
        displayName = "Green 1",
        imageFile = "green1.webp"
    ),
    WALLPAPER_GREEN2(
        displayName = "Green 2",
        imageFile = "green2.webp"
    ),
    WALLPAPER_RED1(
        displayName = "Red 1",
        imageFile = "red1.webp"
    );

    companion object {
        fun fromName(name: String): BackgroundPreset {
            return values().find { it.name == name } ?: NONE
        }
        val wallpapers get() = values().filter { it.imageFile != null }
    }
}
