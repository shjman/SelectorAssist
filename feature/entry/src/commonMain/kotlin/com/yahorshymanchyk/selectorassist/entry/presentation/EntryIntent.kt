package com.yahorshymanchyk.selectorassist.entry.presentation

import com.yahorshymanchyk.selectorassist.domain.model.Tag

sealed interface EntryIntent {
    data class SliderChanged(
        val value: Float,
    ) : EntryIntent

    data class TagToggled(
        val tag: Tag,
    ) : EntryIntent

    data class CommentChanged(
        val text: String,
    ) : EntryIntent

    data object Save : EntryIntent
}
