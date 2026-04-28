package com.yahorshymanchyk.selectorassist.domain.model

// Daily user input for a specific question: slider position, tags, and optional comment
data class Entry(
    val id: Long,
    val questionId: Long,
    val date: Long,
    val sliderValue: Int,
    val tags: List<Tag>,
    val comment: String?,
)
