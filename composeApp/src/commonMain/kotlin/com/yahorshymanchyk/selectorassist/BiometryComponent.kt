package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.value.Value

interface BiometryComponent {
    val state: Value<BiometryState>
    fun onIntent(intent: BiometryIntent)
}
