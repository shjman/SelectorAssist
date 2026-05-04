package com.yahorshymanchyk.selectorassist

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WebCurrentDateProvider : CurrentDateProvider {
    private val _nowMs = MutableStateFlow(SystemClock.now())
    override val nowMs: StateFlow<Long> = _nowMs

    fun advance() { _nowMs.value = _nowMs.value + CurrentDateProvider.DAY_MS }
    fun retreat() { _nowMs.value = _nowMs.value - CurrentDateProvider.DAY_MS }
}
