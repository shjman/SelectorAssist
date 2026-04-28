package com.yahorshymanchyk.selectorassist.entry.component

import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryIntent
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryState

interface EntryComponent {
    val state: Value<EntryState>
    fun onIntent(intent: EntryIntent)
    fun onBack()
}
