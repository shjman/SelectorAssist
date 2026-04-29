package com.yahorshymanchyk.selectorassist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics

private class IosBiometryAuthenticator : BiometryAuthenticator {
    override suspend fun authenticate(): Boolean {
        val ctx = LAContext()
        if (!ctx.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, error = null)) return true
        return suspendCancellableCoroutine { cont ->
            ctx.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = "Войдите в приложение",
            ) { success, _ ->
                if (cont.isActive) cont.resume(success)
            }
        }
    }
}

@Composable
actual fun rememberBiometryAuthenticator(): BiometryAuthenticator {
    return remember { IosBiometryAuthenticator() }
}
