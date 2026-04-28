package com.yahorshymanchyk.selectorassist.domain.model

data class Entry(
    val id: Long,
    val questionId: Long,
    val date: Long,
    val sliderValue: Int,
    val tags: List<Tag>,
    val comment: String?,
)
