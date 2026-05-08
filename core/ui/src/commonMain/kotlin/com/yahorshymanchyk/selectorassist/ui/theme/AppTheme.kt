package com.yahorshymanchyk.selectorassist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        background = AppColors.Background,
        surface = AppColors.Surface,
        onBackground = AppColors.TextPrimary,
        onSurface = AppColors.TextPrimary,
        onSurfaceVariant = AppColors.TextSecondary,
        outline = AppColors.Divider,
        primary = AppColors.PoleA,
        secondary = AppColors.PoleB,
        tertiary = AppColors.PendingIndicator,
    )

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content,
    )
}
