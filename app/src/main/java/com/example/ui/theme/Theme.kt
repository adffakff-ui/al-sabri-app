package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = QuranDarkPrimary,
  onPrimary = Color(0xFF04281E),
  primaryContainer = QuranEmeraldDark,
  onPrimaryContainer = Color(0xFFA6E7D5),
  secondary = QuranDarkSecondary,
  onSecondary = Color(0xFF382900),
  secondaryContainer = QuranGoldDark,
  onSecondaryContainer = Color(0xFFFFE088),
  tertiary = Color(0xFF7CD4D8),
  background = QuranDarkBackground,
  onBackground = QuranDarkTextPrimary,
  surface = QuranDarkSurface,
  onSurface = QuranDarkTextPrimary,
  surfaceVariant = QuranDarkSurfaceVariant,
  onSurfaceVariant = QuranDarkTextSecondary,
  error = QuranError,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = QuranEmeraldPrimary,
  onPrimary = Color.White,
  primaryContainer = QuranEmeraldLight,
  onPrimaryContainer = QuranEmeraldDark,
  secondary = QuranGoldSecondary,
  onSecondary = Color.White,
  secondaryContainer = QuranGoldLight,
  onSecondaryContainer = QuranGoldDark,
  tertiary = QuranTealTertiary,
  onTertiary = Color.White,
  tertiaryContainer = QuranTealLight,
  onTertiaryContainer = Color(0xFF0B3337),
  background = QuranBackground,
  onBackground = QuranTextPrimary,
  surface = QuranSurface,
  onSurface = QuranTextPrimary,
  surfaceVariant = QuranSurfaceVariant,
  onSurfaceVariant = QuranTextSecondary,
  error = QuranError,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
