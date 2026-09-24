package com.liceo.account.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = LiceoBluePrimary,
    onPrimary = LiceoBlueOnPrimary,
    primaryContainer = LiceoBlueContainer,
    onPrimaryContainer = LiceoBlueOnContainer,
    secondary = LiceoRedSecondary,
    onSecondary = LiceoRedOnSecondary,
    secondaryContainer = LiceoRedContainer,
    onSecondaryContainer = LiceoRedOnContainer,
    background = LiceoBackground,
    surface = LiceoSurface,
    onSurface = LiceoOnSurface,
    onSurfaceVariant = LiceoOnSurfaceVariant,
    outline = LiceoOutline
)

private val CustomShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun LiceoAccountTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        shapes = CustomShapes,
        content = content
    )
}
