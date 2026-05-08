package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.CompletedQuestionSummary
import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private const val SLIDER_MID = 5
private const val PERCENT_MULTIPLIER = 100
private const val PERCENT_NEUTRAL = 50

class GetCompletedQuestionSummariesUseCase(
    private val questionRepository: QuestionRepository,
    private val entryRepository: EntryRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<CompletedQuestionSummary>> =
        questionRepository.observeCompleted().flatMapLatest { questions ->
            if (questions.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    questions.map { question ->
                        entryRepository.observeAll(question.id).map { entries ->
                            question.toSummary(entries)
                        }
                    },
                ) { it.toList() }
            }
        }

    private fun Question.toSummary(entries: List<Entry>): CompletedQuestionSummary {
        val poleACount = entries.count { it.sliderValue < SLIDER_MID }
        val poleBCount = entries.count { it.sliderValue > SLIDER_MID }
        val total = entries.size
        val (dominantPole, percentage) =
            when {
                total == 0 -> poleA to PERCENT_NEUTRAL
                poleACount >= poleBCount -> poleA to (poleACount * PERCENT_MULTIPLIER / total)
                else -> poleB to (poleBCount * PERCENT_MULTIPLIER / total)
            }
        return CompletedQuestionSummary(
            question = this,
            dominantPole = dominantPole,
            dominantPercentage = percentage,
        )
    }
}
