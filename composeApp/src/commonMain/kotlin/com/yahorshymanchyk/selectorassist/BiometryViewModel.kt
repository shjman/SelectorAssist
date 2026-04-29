package com.yahorshymanchyk.selectorassist

import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BiometryViewModel(
    scope: CoroutineScope,
    getAppSettings: GetAppSettingsUseCase,
    private val onAuthenticated: () -> Unit,
) {
    private val _state = MutableStateFlow(BiometryState())
    val state: StateFlow<BiometryState> = _state.asStateFlow()

    init {
        scope.launch {
            val settings = getAppSettings().first()
            if (!settings.isBiometryEnabled) {
                onAuthenticated()
            } else {
                _state.update { it.copy(isReadyToAuthenticate = true) }
            }
        }
    }

    fun onIntent(intent: BiometryIntent) {
        when (intent) {
            BiometryIntent.AuthSuccess -> onAuthenticated()
            BiometryIntent.AuthFailed -> _state.update { it.copy(isReadyToAuthenticate = false, isError = true) }
            BiometryIntent.Retry -> _state.update { it.copy(isReadyToAuthenticate = true, isError = false) }
        }
    }
}
