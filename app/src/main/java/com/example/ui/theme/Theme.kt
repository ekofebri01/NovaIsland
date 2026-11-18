package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.model.ThemeMode

private fun buildDarkScheme(accentColor: Color) = darkColorScheme(
    primary = accentColor,
    onPrimary = ObsidianBlack,
    primaryContainer = accentColor.copy(alpha = 0.2f),
    onPrimaryContainer = TextPrimaryDark,
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = ElectricViolet.copy(alpha = 0.22f),
    onSecondaryContainer = TextPrimaryDark,
    tertiary = NeonEmerald,
    background = ObsidianBlack,
    onBackground = TextPrimaryDark,
    surface = DeepSpaceSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ElevatedSpaceSurface,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color.White.copy(alpha = 0.14f),
    error = CoralAlert
)

private fun buildLightScheme(accentColor: Color) = lightColorScheme(
    primary = if (accentColor == AuroraCyan) AuroraCyanDark else accentColor,
    onPrimary = Color.White,
    primaryContainer = accentColor.copy(alpha = 0.16f),
    onPrimaryContainer = TextPrimaryLight,
    secondary = ElectricVioletDark,
    onSecondary = Color.White,
    secondaryContainer = ElectricViolet.copy(alpha = 0.14f),
    onSecondaryContainer = TextPrimaryLight,
    tertiary = NeonEmerald,
    background = FrostedLightSurface,
    onBackground = TextPrimaryLight,
    surface = PureIceSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = SoftIceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color.Black.copy(alpha = 0.12f),
    error = CoralAlert
)

val NovaShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun NovaIslandTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    accentHex: Long = 0xFF22D3EE,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.AUTO -> systemDark
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val accentColor = Color(accentHex)

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> buildDarkScheme(accentColor)
        else -> buildLightScheme(accentColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NovaTypography,
        shapes = NovaShapes,
        content = content
    )
}
