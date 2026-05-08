package com.yahorshymanchyk.selectorassist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import com.yahorshymanchyk.selectorassist.ui.theme.isAndroid

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppColors.TextPrimary,
) {
    if (isAndroid) {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = tint,
            )
        }
    } else {
        Box(
            modifier =
                modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "‹", color = tint, fontSize = 24.sp)
        }
    }
}
