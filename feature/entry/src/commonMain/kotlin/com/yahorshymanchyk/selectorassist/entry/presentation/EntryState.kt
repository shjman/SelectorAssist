package com.yahorshymanchyk.selectorassist.entry.presentation

import com.yahorshymanchyk.selectorassist.domain.model.Tag

data class EntryState(
    val questionTitle: String = "",
    val poleA: String = "",
    val poleB: String = "",
    val currentDay: Int = 0,
    val totalDays: Int = 0,
    val sliderValue: Float = 0.5f,
    val selectedTags: Set<Tag> = emptySet(),
    val comment: String = "",
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
)
