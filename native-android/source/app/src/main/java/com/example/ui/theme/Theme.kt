package com.example.ui.theme

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
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF003258),
    primaryContainer = EkabNavyLight,
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF4DD0E1),
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFF97F0FF),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = Color(0xFF6B3C00),
    onTertiaryContainer = Color(0xFFFFDCC2),
    background = EkabBackgroundDark,
    onBackground = Color(0xFFE2E2E6),
    surface = EkabSurfaceDark,
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = EkabSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC3C7CF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = EkabNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = EkabBlue,
    onSecondary = Color.White,
    secondaryContainer = EkabBlueContainer,
    onSecondaryContainer = EkabOnBlueContainer,
    tertiary = EkabAmber,
    onTertiary = Color.White,
    tertiaryContainer = EkabAmberContainer,
    onTertiaryContainer = EkabOnAmberContainer,
    background = EkabBackgroundLight,
    onBackground = Color(0xFF191C1E),
    surface = EkabSurfaceLight,
    onSurface = Color(0xFF191C1E),
    surfaceVariant = EkabSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF42474E),
    error = EkabRed,
    onError = Color.White,
    errorContainer = EkabRedContainer,
    onErrorContainer = EkabOnRedContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional EKAB theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
