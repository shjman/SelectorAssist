package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.SystemClock
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

private const val MILLIS_PER_DAY = 86_400_000L

class GetActiveQuestionSummariesUseCase(
    private val questionRepository: QuestionRepository,
    private val entryRepository: EntryRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<ActiveQuestionSummary>> =
        questionRepository.observeActive().flatMapLatest { questions ->
            if (questions.isEmpty()) {
                flowOf(emptyList())
            } else {
                val today = SystemClock.todayAtMidnightMs()
                combine(
                    questions.map { question ->
                        entryRepository.observeByDate(question.id, today).map { entry ->
                            question.toSummary(hasTodayEntry = entry != null)
                        }
                    }
                ) { it.toList() }
            }
        }

    private fun Question.toSummary(hasTodayEntry: Boolean): ActiveQuestionSummary {
        val totalDays = ((deadlineAt - createdAt) / MILLIS_PER_DAY).toInt().coerceAtLeast(1)
        val currentDay = ((SystemClock.now() - createdAt) / MILLIS_PER_DAY + 1L)
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
