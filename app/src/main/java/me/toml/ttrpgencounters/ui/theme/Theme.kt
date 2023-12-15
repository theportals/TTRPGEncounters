package me.toml.ttrpgencounters.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = DNDRed,
    primaryVariant = DNDRed,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = DNDRed,
    primaryVariant = DOrange900,
    secondary = DOrange900,
    background = Parchment,
    surface = Color.White,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TTRPGEncountersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val suic = rememberSystemUiController()
    if (darkTheme) suic.setStatusBarColor(color = Color.Transparent)
    else suic.setStatusBarColor(color = DarkerRed)
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}