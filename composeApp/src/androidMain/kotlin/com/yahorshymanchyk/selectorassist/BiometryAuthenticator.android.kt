package com.yahorshymanchyk.selectorassist

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private class AndroidBiometryAuthenticator(
    private val activity: FragmentActivity,
) : BiometryAuthenticator {
    override suspend fun authenticate(): Boolean {
        val status = BiometricManager.from(activity).canAuthenticate(BIOMETRIC_WEAK)
        if (status != BiometricManager.BIOMETRIC_SUCCESS) return true
        return suspendCancellableCoroutine { cont ->
            val executor = ContextCompat.getMainExecutor(activity)
            val prompt =
                BiometricPrompt(
                    activity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            if (cont.isActive) cont.resume(true)
                        }

                        override fun onAuthenticationFailed() {
                            // Individual attempt failed — wait for terminal error to resume false
                        }

                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence,
                        ) {
                            if (cont.isActive) cont.resume(false)
                        }
                    },
                )
            val promptInfo =
                BiometricPrompt.PromptInfo
                    .Builder()
                    .setTitle("Вход в приложение")
                    .setNegativeButtonText("Отмена")
                    .build()
            prompt.authenticate(promptInfo)
            cont.invokeOnCancellation { prompt.cancelAuthentication() }
        }
    }
}

@Composable
actual fun rememberBiometryAuthenticator(): BiometryAuthenticator {
    val context = LocalContext.current
    val activity =
        context as? FragmentActivity
            ?: error("rememberBiometryAuthenticator requires FragmentActivity context")
    return remember(activity) { AndroidBiometryAuthenticator(activity) }
}
