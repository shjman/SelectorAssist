package com.yahorshymanchyk.selectorassist.settings.presentation

import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SetBiometryEnabledUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getAppSettings: GetAppSettingsUseCase,
    private val setBiometryEnabled: SetBiometryEnabledUseCase,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        scope.launch {
            getAppSettings().collect { settings ->
                _state.update { it.copy(isBiometryEnabled = settings.isBiometryEnabled) }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleBiometry -> scope.launch {
                setBiometryEnabled(intent.enabled)
            }
        }
    }
}
