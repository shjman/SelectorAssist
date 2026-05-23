package com.yahorshymanchyk.selectorassist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.yahorshymanchyk.selectorassist.ui.components.BackButton
import com.yahorshymanchyk.selectorassist.ui.components.SettingsIconButton
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import com.yahorshymanchyk.selectorassist.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class ComponentSnapshotTest {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_5,
            showSystemUi = false,
        )

    @Test
    fun backButton() {
        paparazzi.snapshot {
            AppTheme {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .background(AppColors.Background),
                    contentAlignment = Alignment.Center,
                ) {
                    BackButton(onClick = {})
                }
            }
        }
    }

    @Test
    fun settingsIconButton() {
        paparazzi.snapshot {
            AppTheme {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .background(AppColors.Background),
                    contentAlignment = Alignment.Center,
                ) {
                    SettingsIconButton(onClick = {})
                }
            }
        }
    }
}
