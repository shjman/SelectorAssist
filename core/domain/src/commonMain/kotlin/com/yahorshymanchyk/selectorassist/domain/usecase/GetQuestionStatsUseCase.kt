package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Pole
import com.yahorshymanchyk.selectorassist.domain.model.QuestionStats
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.model.TagGroup
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SLIDER_MID = 5
private const val PERCENT_MULTIPLIER = 100

class GetQuestionStatsUseCase(private val repository: EntryRepository) {
    operator fun invoke(questionId: Long): Flow<QuestionStats> =
        repository.observeAll(questionId).map { it.toStats() }

    private fun List<Entry>.toStats(): QuestionStats {
        val total = size
        val poleACount = count { it.sliderValue < SLIDER_MID }
        val poleBCount = count { it.sliderValue > SLIDER_MID }
        val poleATendency = if (total > 0) poleACount * PERCENT_MULTIPLIER / total else 0
        return QuestionStats(
            totalEntries = total,
            poleATendencyPercent = poleATendency,
            poleBTendencyPercent = PERCENT_MULTIPLIER - poleATendency,
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
        val poleACount = tagged.count { it.sliderValue < SLIDER_MID }
        val poleBCount = tagged.count { it.sliderValue > SLIDER_MID }
        return when {
            poleACount > poleBCount -> Pole.A
            poleBCount > poleACount -> Pole.B
            else -> null
        }
    }
}
