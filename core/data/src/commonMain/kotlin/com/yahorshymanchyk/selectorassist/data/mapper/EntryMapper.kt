package com.yahorshymanchyk.selectorassist.data.mapper

import com.yahorshymanchyk.selectorassist.data.db.Entries
import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Tag

internal fun Entries.toDomain(tagNames: List<String>): Entry = Entry(
    id = id,
    questionId = question_id,
    date = date,
    sliderValue = slider_value.toInt(),
    tags = tagNames.mapNotNull { name -> Tag.entries.firstOrNull { it.name == name } },
    comment = comment,
)
