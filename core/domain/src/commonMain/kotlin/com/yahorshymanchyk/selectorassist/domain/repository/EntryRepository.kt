package com.yahorshymanchyk.selectorassist.domain.repository

import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    fun observeByDate(questionId: Long, dateMs: Long): Flow<Entry?>
    fun observeAll(questionId: Long): Flow<List<Entry>>
    suspend fun upsert(questionId: Long, date: Long, sliderValue: Int, tags: List<Tag>, comment: String?)
}
