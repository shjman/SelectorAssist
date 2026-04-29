package com.yahorshymanchyk.selectorassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import com.yahorshymanchyk.selectorassist.ui.theme.isAndroid

@Composable
fun SettingsIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (isAndroid) {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = AppColors.TextSecondary,
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(AppColors.Surface)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = AppColors.TextPrimary,
            )
        }
    }
}
