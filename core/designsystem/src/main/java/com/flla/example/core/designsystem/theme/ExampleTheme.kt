package com.flla.example.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.flla.example.core.model.ThemeMode

private val LightColors =
    lightColorScheme(
        primary = ExampleBlue,
        secondary = ExampleGreen,
        background = ExampleSurface,
        surface = androidx.compose.ui.graphics.Color.White,
        onPrimary = androidx.compose.ui.graphics.Color.White,
        onBackground = ExampleInk,
        onSurface = ExampleInk,
        error = ExampleError,
    )

private val DarkColors =
    darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF9AB4FF),
        secondary = androidx.compose.ui.graphics.Color(0xFF7FD8B4),
        background = androidx.compose.ui.graphics.Color(0xFF111318),
        surface = androidx.compose.ui.graphics.Color(0xFF1A1D24),
        onPrimary = androidx.compose.ui.graphics.Color(0xFF0B1B3F),
        onBackground = androidx.compose.ui.graphics.Color(0xFFE6E8EF),
        onSurface = androidx.compose.ui.graphics.Color(0xFFE6E8EF),
        error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    )

@Composable
fun ExampleTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val darkTheme =
        when (themeMode) {
            ThemeMode.System -> isSystemInDarkTheme()
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        }
    CompositionLocalProvider(LocalExampleSpacing provides ExampleSpacing()) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = ExampleTypography,
            content = content,
        )
    }
}
