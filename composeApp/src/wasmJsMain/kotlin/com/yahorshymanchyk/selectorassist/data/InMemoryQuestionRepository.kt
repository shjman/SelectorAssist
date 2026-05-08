package com.yahorshymanchyk.selectorassist.data

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryQuestionRepository(
    private val clock: CurrentDateProvider,
) : QuestionRepository {
    private var nextId = 1L
    private val questions = MutableStateFlow<List<Question>>(emptyList())

    override fun observeActive(): Flow<List<Question>> =
        combine(questions, clock.nowMs) { list, nowMs ->
            list.map { it.withCompletion(nowMs) }.filter { !it.isCompleted }
        }

    override fun observeCompleted(): Flow<List<Question>> =
        combine(questions, clock.nowMs) { list, nowMs ->
            list.map { it.withCompletion(nowMs) }.filter { it.isCompleted }
        }

    override fun observeById(questionId: Long): Flow<Question?> =
        combine(questions, clock.nowMs) { list, nowMs ->
            list.find { it.id == questionId }?.withCompletion(nowMs)
        }

    override suspend fun create(
        title: String,
        poleA: String,
        poleB: String,
        deadlineAt: Long,
    ) {
        questions.update { list ->
            list +
                Question(
                    id = nextId++,
                    title = title,
                    poleA = poleA,
                    poleB = poleB,
                    createdAt = clock.now(),
                    deadlineAt = deadlineAt,
                    isCompleted = false,
                )
        }
    }

    override suspend fun delete(questionId: Long) {
        questions.update { list -> list.filter { it.id != questionId } }
    }

    private fun Question.withCompletion(nowMs: Long) = copy(isCompleted = deadlineAt <= nowMs)
}
