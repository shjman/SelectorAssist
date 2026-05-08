package com.yahorshymanchyk.selectorassist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors

private const val LABEL_FONT_SIZE = 14

@Composable
fun DateSwitcherBar(
    offsetDays: Int,
    onRetreat: () -> Unit,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label =
        when {
            offsetDays == 0 -> "Today"
            offsetDays > 0 -> "+$offsetDays day${if (offsetDays > 1) "s" else ""}"
            else -> "$offsetDays day${if (offsetDays < -1) "s" else ""}"
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onRetreat,
            modifier =
                Modifier
                    .size(36.dp)
                    .background(AppColors.InputField, CircleShape),
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Previous day",
                tint = AppColors.TextPrimary,
            )
        }
        Text(
            text = label,
            color = AppColors.TextPrimary,
            fontSize = LABEL_FONT_SIZE.sp,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        IconButton(
            onClick = onAdvance,
            modifier =
                Modifier
                    .size(36.dp)
                    .background(AppColors.InputField, CircleShape),
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Next day",
                tint = AppColors.TextPrimary,
            )
        }
    }
}
