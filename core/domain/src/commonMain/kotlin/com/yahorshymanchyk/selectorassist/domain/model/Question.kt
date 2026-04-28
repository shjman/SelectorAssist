package com.yahorshymanchyk.selectorassist.domain.model

// Binary dilemma being observed over a fixed time period
data class Question(
    val id: Long,
    val title: String,
    val poleA: String,
    val poleB: String,
    val createdAt: Long,
    val deadlineAt: Long,
    val isCompleted: Boolean,
)
