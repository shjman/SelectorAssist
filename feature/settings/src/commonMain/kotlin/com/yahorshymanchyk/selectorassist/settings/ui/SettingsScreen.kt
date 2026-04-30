@file:Suppress("MagicNumber")

package com.yahorshymanchyk.selectorassist.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.settings.component.SettingsComponent
import selectorassist.feature.settings.generated.resources.Res
import selectorassist.feature.settings.generated.resources.settings_biometry_subtitle
import selectorassist.feature.settings.generated.resources.settings_biometry_title
import selectorassist.feature.settings.generated.resources.settings_section_security
import selectorassist.feature.settings.generated.resources.settings_title
import com.yahorshymanchyk.selectorassist.settings.presentation.SettingsIntent
import com.yahorshymanchyk.selectorassist.ui.components.BackButton
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import com.yahorshymanchyk.selectorassist.ui.theme.isAndroid
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(component: SettingsComponent) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        SettingsHeader(onBack = component::onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.settings_section_security),
                color = if (isAndroid) AppColors.PoleA else AppColors.TextSecondary,
                fontSize = 12.sp,
                letterSpacing = 0.8.sp,
                fontWeight = if (isAndroid) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            Spacer(Modifier.height(8.dp))

            BiometryToggleRow(
                isEnabled = state.isBiometryEnabled,
                onToggle = { component.onIntent(SettingsIntent.ToggleBiometry(it)) },
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isAndroid) 4.dp else 8.dp, end = 20.dp, top = 56.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackButton(onClick = onBack)
        Text(
            text = stringResource(Res.string.settings_title),
            color = AppColors.TextPrimary,
            fontSize = if (isAndroid) 22.sp else 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun BiometryToggleRow(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.settings_biometry_title),
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
            )
            Text(
                text = stringResource(Res.string.settings_biometry_subtitle),
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppColors.TextPrimary,
                checkedTrackColor = AppColors.PoleA,
                uncheckedThumbColor = AppColors.TextSecondary,
                uncheckedTrackColor = AppColors.InputField,
                uncheckedBorderColor = AppColors.Chevron,
            ),
        )
    }
}
