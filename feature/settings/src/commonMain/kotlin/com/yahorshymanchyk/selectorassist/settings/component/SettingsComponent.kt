package com.yahorshymanchyk.selectorassist.settings.component

import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.settings.presentation.SettingsIntent
import com.yahorshymanchyk.selectorassist.settings.presentation.SettingsState

interface SettingsComponent {
    val state: Value<SettingsState>

    fun onIntent(intent: SettingsIntent)

    fun onBack()
}
