package com.yahorshymanchyk.selectorassist.domain

import kotlinx.coroutines.flow.StateFlow

interface CurrentDateProvider {
    val nowMs: StateFlow<Long>

    fun now(): Long = nowMs.value

    fun todayAtMidnightMs(): Long = nowMs.value.let { it - (it % DAY_MS) }

    companion object {
        const val DAY_MS = 86_400_000L
    }
}
