package com.yahorshymanchyk.selectorassist

import androidx.compose.runtime.Composable

interface BiometryAuthenticator {
    suspend fun authenticate(): Boolean
}

@Composable
expect fun rememberBiometryAuthenticator(): BiometryAuthenticator
