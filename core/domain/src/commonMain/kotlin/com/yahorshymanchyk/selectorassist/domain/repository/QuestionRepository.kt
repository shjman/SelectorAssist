package com.yahorshymanchyk.selectorassist.domain.repository

import com.yahorshymanchyk.selectorassist.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun observeActive(): Flow<List<Question>>

    fun observeCompleted(): Flow<List<Question>>

    fun observeById(questionId: Long): Flow<Question?>

    suspend fun create(
        title: String,
        poleA: String,
        poleB: String,
        deadlineAt: Long,
    )

    suspend fun delete(questionId: Long)
}
