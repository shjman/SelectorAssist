package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository

class SaveEntryUseCase(
    private val repository: EntryRepository,
    private val clock: CurrentDateProvider,
) {
    suspend operator fun invoke(
        questionId: Long,
        sliderValue: Int,
        tags: List<Tag>,
        comment: String?,
    ) {
        repository.upsert(questionId, clock.todayAtMidnightMs(), sliderValue, tags, comment)
    }
}
