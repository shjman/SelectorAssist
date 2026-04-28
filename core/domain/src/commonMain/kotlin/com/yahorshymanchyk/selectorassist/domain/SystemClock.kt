package com.yahorshymanchyk.selectorassist.domain

expect fun currentTimeMs(): Long

object SystemClock {
    private const val DAY_MS = 86_400_000L

    fun now(): Long = currentTimeMs()

    fun todayAtMidnightMs(): Long {
        val now = now()
        return now - (now % DAY_MS)
    }
}
