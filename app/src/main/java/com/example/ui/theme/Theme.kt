package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = VibePrimaryDark,
  onPrimary = VibeOnPrimaryDark,
  primaryContainer = VibePrimaryContainerDark,
  onPrimaryContainer = VibeOnPrimaryContainerDark,
  secondary = VibeSecondaryDark,
  onSecondary = VibeOnSecondaryDark,
  secondaryContainer = VibeSecondaryContainerDark,
  onSecondaryContainer = VibeOnSecondaryContainerDark,
  tertiary = VibeTertiaryDark,
  onTertiary = VibeOnTertiaryDark,
  tertiaryContainer = VibeTertiaryContainerDark,
  onTertiaryContainer = VibeOnTertiaryContainerDark,
  background = VibeBackgroundDark,
  onBackground = VibeOnBackgroundDark,
  surface = VibeSurfaceDark,
  onSurface = VibeOnSurfaceDark,
  surfaceVariant = VibeSurfaceVariantDark,
  onSurfaceVariant = VibeOnSurfaceVariantDark,
)

private val LightColorScheme = lightColorScheme(
  primary = VibePrimaryLight,
  onPrimary = VibeOnPrimaryLight,
  primaryContainer = VibePrimaryContainerLight,
  onPrimaryContainer = VibeOnPrimaryContainerLight,
  secondary = VibeSecondaryLight,
  onSecondary = VibeOnSecondaryLight,
  secondaryContainer = VibeSecondaryContainerLight,
  onSecondaryContainer = VibeOnSecondaryContainerLight,
  tertiary = VibeTertiaryLight,
  onTertiary = VibeOnTertiaryLight,
  tertiaryContainer = VibeTertiaryContainerLight,
  onTertiaryContainer = VibeOnTertiaryContainerLight,
  background = VibeBackgroundLight,
  onBackground = VibeOnBackgroundLight,
  surface = VibeSurfaceLight,
  onSurface = VibeOnSurfaceLight,
  surfaceVariant = VibeSurfaceVariantLight,
  onSurfaceVariant = VibeOnSurfaceVariantLight,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
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
