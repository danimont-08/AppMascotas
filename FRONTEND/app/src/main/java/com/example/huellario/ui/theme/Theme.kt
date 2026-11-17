package com.example.huellario.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Esquema de colores de Huellario con AzulPrincipal (#83CBE8) como color primario/protagonista
 * y VioletaPrincipal (#49358C) como color secundario para acciones y acentos.
 */
private val HuellarioLightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = TextoCharcoal,
    primaryContainer = AzulSuave,
    onPrimaryContainer = TextoCharcoal,
    secondary = VioletaPrincipal,
    onSecondary = BlancoPuro,
    secondaryContainer = Lavanda,
    onSecondaryContainer = VioletaPrincipal,
    tertiary = AzulCielo,
    onTertiary = TextoCharcoal,
    background = FondoClaro,
    onBackground = TextoCharcoal,
    surface = BlancoPuro,
    onSurface = TextoCharcoal,
    surfaceVariant = AzulSuave,
    onSurfaceVariant = TextoMuted,
    outline = AzulCielo,
    outlineVariant = AzulSuave,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainerRed
)

@Composable
fun HuellarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = HuellarioLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

