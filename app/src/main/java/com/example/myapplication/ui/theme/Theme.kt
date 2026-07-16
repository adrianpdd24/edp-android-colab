package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    secondary = SecondaryLight,
    surface = SurfaceLight,
    background = BackgroundLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    secondary = SecondaryDark,
    surface = SurfaceDark,
    background = BackgroundDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

@Composable
fun ProfileTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit

) {

    MaterialTheme(

        colorScheme =
            if (darkTheme) DarkColors
            else LightColors,

        typography = Typography,

        content = content

    )

}