package com.yahorshymanchyk.selectorassist.domain

import kotlin.time.Clock

object SystemClock {
    private const val DAY_MS = 86_400_000L

    fun now(): Long = Clock.System.now().toEpochMilliseconds()

    fun todayAtMidnightMs(): Long {
        val now = now()
        return now - (now % DAY_MS)
    }
}
