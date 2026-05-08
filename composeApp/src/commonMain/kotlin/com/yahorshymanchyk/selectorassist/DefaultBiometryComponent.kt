package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DefaultBiometryComponent(
    componentContext: ComponentContext,
    getAppSettings: GetAppSettingsUseCase,
    onAuthenticated: () -> Unit,
) : BiometryComponent,
    ComponentContext by componentContext {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel =
        BiometryViewModel(
            scope = scope,
            getAppSettings = getAppSettings,
            onAuthenticated = onAuthenticated,
        )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<BiometryState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    override fun onIntent(intent: BiometryIntent) = viewModel.onIntent(intent)
}
