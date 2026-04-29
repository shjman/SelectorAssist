package com.yahorshymanchyk.selectorassist.settings.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SetBiometryEnabledUseCase
import com.yahorshymanchyk.selectorassist.settings.presentation.SettingsIntent
import com.yahorshymanchyk.selectorassist.settings.presentation.SettingsState
import com.yahorshymanchyk.selectorassist.settings.presentation.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val onNavigateBack: () -> Unit,
    getAppSettings: GetAppSettingsUseCase,
    setBiometryEnabled: SetBiometryEnabledUseCase,
) : SettingsComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel = SettingsViewModel(
        getAppSettings = getAppSettings,
        setBiometryEnabled = setBiometryEnabled,
        scope = scope,
    )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<SettingsState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    override fun onIntent(intent: SettingsIntent) = viewModel.onIntent(intent)
    override fun onBack() = onNavigateBack()
}
