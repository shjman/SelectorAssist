package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.SystemClock
import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow

class GetTodayEntryUseCase(private val repository: EntryRepository) {
    operator fun invoke(questionId: Long): Flow<Entry?> =
        repository.observeByDate(questionId, SystemClock.todayAtMidnightMs())
}
