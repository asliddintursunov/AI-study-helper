package com.aistudyhelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LightColors = lightColorScheme(
    primary = Color(0xFF2457D6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE6FF),
    onPrimaryContainer = Color(0xFF0A2463),
    secondary = Color(0xFF008C7A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC9F3EC),
    onSecondaryContainer = Color(0xFF003D35),
    tertiary = Color(0xFFB2572D),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBCB),
    onTertiaryContainer = Color(0xFF4A1B05),
    background = Color(0xFFF5F7FB),
    onBackground = Color(0xFF182033),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF182033),
    surfaceVariant = Color(0xFFE8ECF5),
    onSurfaceVariant = Color(0xFF596276),
    outline = Color(0xFFC9D0DE),
    error = Color(0xFFB3261E)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAFC6FF),
    onPrimary = Color(0xFF002D75),
    primaryContainer = Color(0xFF153E9D),
    onPrimaryContainer = Color(0xFFDDE6FF),
    secondary = Color(0xFF80D8CB),
    onSecondary = Color(0xFF003D35),
    secondaryContainer = Color(0xFF00574C),
    onSecondaryContainer = Color(0xFFC9F3EC),
    tertiary = Color(0xFFFFB692),
    onTertiary = Color(0xFF5F2608),
    tertiaryContainer = Color(0xFF813B18),
    onTertiaryContainer = Color(0xFFFFDBCB),
    background = Color(0xFF101521),
    onBackground = Color(0xFFE8ECF5),
    surface = Color(0xFF171D2A),
    onSurface = Color(0xFFE8ECF5),
    surfaceVariant = Color(0xFF2B3344),
    onSurfaceVariant = Color(0xFFC5CAD8),
    outline = Color(0xFF454F63)
)

private val AppTypography = Typography().run {
    copy(
        displayMedium = displayMedium.copy(fontWeight = FontWeight.ExtraBold),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.Bold)
    )
}

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

@Composable
fun AIStudyHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

fun appBackgroundBrush(darkTheme: Boolean): Brush {
    return if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF101521),
                Color(0xFF151B29),
                Color(0xFF111827)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF9FBFF),
                Color(0xFFF1F5FF),
                Color(0xFFF7F8FB)
            )
        )
    }
}
