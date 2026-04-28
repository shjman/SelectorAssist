package com.yahorshymanchyk.selectorassist.domain.model

data class ActiveQuestionSummary(
    val question: Question,
    val hasTodayEntry: Boolean,
    val currentDay: Int,
    val totalDays: Int,
)
