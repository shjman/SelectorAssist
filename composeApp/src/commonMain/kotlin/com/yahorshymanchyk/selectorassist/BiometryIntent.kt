package com.yahorshymanchyk.selectorassist

sealed interface BiometryIntent {
    data object AuthSuccess : BiometryIntent

    data object AuthFailed : BiometryIntent

    data object Retry : BiometryIntent
}
