package com.mhmtn.a6thsense.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF8F5FF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2D1B69),
    onSurface = Color(0xFF2D1B69),
    onSurfaceVariant = Color(0xFF9E9E9E),
    surfaceVariant = Color(0xFFF3F0FF),
    outline = Color(0xFFE0E0E0),
    error = Color(0xFFFF4E6A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surfaceTint = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF0F0C29),
    surface = Color(0xFF1A1A2E),
    onPrimary = Color(0xFF1A1A2E),
    onSecondary = Color(0xFF1A1A2E),
    onTertiary = Color(0xFF1A1A2E),
    onBackground = Color(0xFFE8DEFF),
    onSurface = Color(0xFFE8DEFF),
    onSurfaceVariant = Color(0xFF9E9E9E),
    surfaceVariant = Color(0xFF2A2545),
    outline = Color(0xFF2E2B4A),
    error = Color(0xFFFF4E6A),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceTint = Color.White
)

@Composable
fun _6thSenseTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = false,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb() // 👈 Şeffaf
            window.navigationBarColor = Color.Transparent.toArgb() // 👈 Şeffaf
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme // 👈 İkona rengi
                isAppearanceLightNavigationBars = !darkTheme // 👈 Navigation bar ikonu rengi
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}