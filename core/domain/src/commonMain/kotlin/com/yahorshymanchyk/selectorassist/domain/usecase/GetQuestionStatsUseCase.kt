package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.QuestionStats
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetQuestionStatsUseCase(private val repository: EntryRepository) {
    operator fun invoke(questionId: Long): Flow<QuestionStats> =
        repository.observeAll(questionId).map { it.toStats() }

    private fun List<Entry>.toStats(): QuestionStats {
        val counts = groupingBy { it.sliderValue }.eachCount()
        val sliderDistribution = (1..5).associateWith { counts.getOrElse(it) { 0 } }

        val tagFrequency = flatMap { it.tags }
            .groupingBy { it }
            .eachCount()

        val poleAArguments = filter { it.sliderValue in 1..2 }
            .mapNotNull { it.comment }

        val poleBArguments = filter { it.sliderValue in 4..5 }
            .mapNotNull { it.comment }

        return QuestionStats(
            sliderDistribution = sliderDistribution,
            tagFrequency = tagFrequency,
            poleAArguments = poleAArguments,
            poleBArguments = poleBArguments,
        )
    }
}
