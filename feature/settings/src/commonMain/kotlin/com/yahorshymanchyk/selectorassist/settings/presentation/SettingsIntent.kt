package com.yahorshymanchyk.selectorassist.settings.presentation

sealed interface SettingsIntent {
    data class ToggleBiometry(
        val enabled: Boolean,
    ) : SettingsIntent
}
