package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Pole
import com.yahorshymanchyk.selectorassist.domain.model.QuestionStats
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.model.TagGroup
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SLIDER_MAX = 10
private const val SLIDER_MID = 5

class GetQuestionStatsUseCase(private val repository: EntryRepository) {
    operator fun invoke(questionId: Long): Flow<QuestionStats> =
        repository.observeAll(questionId).map { it.toStats() }

    private fun List<Entry>.toStats(): QuestionStats {
        val total = size
        val poleATendencyPercent = if (total > 0) {
            (sumOf { (SLIDER_MAX - it.sliderValue).toDouble() } / (total * SLIDER_MAX) * 100).toInt()
        } else 0
        return QuestionStats(
            totalEntries = total,
            poleATendencyPercent = poleATendencyPercent,
            poleBTendencyPercent = 100 - poleATendencyPercent,
            noiseInfluencePole = computeGroupInfluence(TagGroup.NOISE),
            healthyInfluencePole = computeGroupInfluence(TagGroup.HEALTHY),
            poleAArguments = filter { it.sliderValue < SLIDER_MID }.mapNotNull { it.comment },
            poleBArguments = filter { it.sliderValue > SLIDER_MID }.mapNotNull { it.comment },
        )
    }

    private fun List<Entry>.computeGroupInfluence(group: TagGroup): Pole? {
        val groupTags = Tag.entries.filter { it.group == group }.toSet()
        val tagged = filter { entry -> entry.tags.any { it in groupTags } }
        if (tagged.isEmpty()) return null
        val poleAWeight = tagged.sumOf { (SLIDER_MAX - it.sliderValue).toDouble() }
        val poleBWeight = tagged.sumOf { it.sliderValue.toDouble() }
        return when {
            poleAWeight > poleBWeight -> Pole.A
            poleBWeight > poleAWeight -> Pole.B
            else -> null
        }
    }
}
