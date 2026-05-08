package com.yahorshymanchyk.selectorassist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import org.jetbrains.compose.resources.stringResource
import selectorassist.composeapp.generated.resources.Res
import selectorassist.composeapp.generated.resources.biometry_error_title
import selectorassist.composeapp.generated.resources.biometry_retry

@Composable
fun BiometryScreen(component: BiometryComponent) {
    val state by component.state.subscribeAsState()
    val authenticator = rememberBiometryAuthenticator()

    LaunchedEffect(state.isReadyToAuthenticate) {
        if (state.isReadyToAuthenticate) {
            val success = authenticator.authenticate()
            component.onIntent(if (success) BiometryIntent.AuthSuccess else BiometryIntent.AuthFailed)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isError) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.biometry_error_title),
                    color = AppColors.TextSecondary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = { component.onIntent(BiometryIntent.Retry) }) {
                    Text(
                        text = stringResource(Res.string.biometry_retry),
                        color = AppColors.PoleA,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
