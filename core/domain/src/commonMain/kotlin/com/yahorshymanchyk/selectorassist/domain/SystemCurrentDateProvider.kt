package com.yahorshymanchyk.selectorassist.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SystemCurrentDateProvider : CurrentDateProvider {
    private val _nowMs = MutableStateFlow(SystemClock.now())
    override val nowMs: StateFlow<Long> = _nowMs
}
