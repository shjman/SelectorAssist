package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.SystemClock
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository

class SaveEntryUseCase(private val repository: EntryRepository) {
    suspend operator fun invoke(questionId: Long, sliderValue: Int, tags: List<Tag>, comment: String?) {
        repository.upsert(questionId, SystemClock.todayAtMidnightMs(), sliderValue, tags, comment)
    }
}
