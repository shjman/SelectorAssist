package com.yahorshymanchyk.selectorassist

data class BiometryState(
    val isReadyToAuthenticate: Boolean = false,
    val isError: Boolean = false,
)
