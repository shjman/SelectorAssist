package com.yahorshymanchyk.selectorassist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberBiometryAuthenticator(): BiometryAuthenticator =
    remember {
        object : BiometryAuthenticator {
            override suspend fun authenticate() = true
        }
    }
