package com.yahorshymanchyk.selectorassist

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WebCurrentDateProvider : CurrentDateProvider {
    private val initialDayMs = SystemClock.now().let { it - (it % CurrentDateProvider.DAY_MS) }

    private val _offsetDays = MutableStateFlow(0)
    val offsetDays: StateFlow<Int> = _offsetDays

    private val _nowMs = MutableStateFlow(initialDayMs)
    override val nowMs: StateFlow<Long> = _nowMs

    fun advance() {
        _offsetDays.value++
        _nowMs.value = initialDayMs + _offsetDays.value * CurrentDateProvider.DAY_MS
    }

    fun retreat() {
        _offsetDays.value--
        _nowMs.value = initialDayMs + _offsetDays.value * CurrentDateProvider.DAY_MS
    }
}
