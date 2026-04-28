package com.yahorshymanchyk.selectorassist.domain.model

data class Question(
    val id: Long,
    val title: String,
    val poleA: String,
    val poleB: String,
    val createdAt: Long,
    val deadlineAt: Long,
    val isCompleted: Boolean,
)
