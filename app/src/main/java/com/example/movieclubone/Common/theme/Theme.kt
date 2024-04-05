package com.example.movieclubone.Common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun MovieClubOneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = DarkPrimary,
            secondary = DarkSecondary,
            background = DarkBackground,
            surface = DarkSurface,
            onPrimary = OnPrimary,
            onSecondary = OnSecondary,
            onBackground = OnBackground,
            onSurface = OnSurface
        )
    } else {
        lightColorScheme(
            primary = LightPrimary,
            secondary = LightSecondary,
            background = LightBackground,
            surface = LightSurface,
            onPrimary = OnPrimary,
            onSecondary = OnSecondary,
            onBackground = OnBackground,
            onSurface = OnSurface
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
