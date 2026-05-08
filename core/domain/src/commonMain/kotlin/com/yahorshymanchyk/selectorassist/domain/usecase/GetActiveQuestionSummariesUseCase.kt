package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.model.ActiveQuestionSummary
import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private const val MILLIS_PER_DAY = CurrentDateProvider.DAY_MS

class GetActiveQuestionSummariesUseCase(
    private val questionRepository: QuestionRepository,
    private val entryRepository: EntryRepository,
    private val clock: CurrentDateProvider,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<ActiveQuestionSummary>> =
        combine(questionRepository.observeActive(), clock.nowMs) { questions, nowMs ->
            Pair(questions, nowMs)
        }.flatMapLatest { (questions, nowMs) ->
            val today = nowMs - (nowMs % MILLIS_PER_DAY)
            if (questions.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    questions.map { question ->
                        entryRepository.observeByDate(question.id, today).map { entry ->
                            question.toSummary(hasTodayEntry = entry != null, nowMs = nowMs)
                        }
                    },
                ) { it.toList() }
            }
        }

    private fun Question.toSummary(
        hasTodayEntry: Boolean,
        nowMs: Long,
    ): ActiveQuestionSummary {
        val totalDays = ((deadlineAt - createdAt) / MILLIS_PER_DAY).toInt().coerceAtLeast(1)
        val currentDay =
            ((nowMs - createdAt) / MILLIS_PER_DAY + 1L)
                .toInt()
                .coerceIn(1, totalDays)
        return ActiveQuestionSummary(
            question = this,
            hasTodayEntry = hasTodayEntry,
            currentDay = currentDay,
            totalDays = totalDays,
        )
    }
}
