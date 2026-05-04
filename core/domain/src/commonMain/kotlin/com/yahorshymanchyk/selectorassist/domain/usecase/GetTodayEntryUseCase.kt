package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GetTodayEntryUseCase(
    private val repository: EntryRepository,
    private val clock: CurrentDateProvider,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(questionId: Long): Flow<Entry?> =
        clock.nowMs
            .map { it - (it % CurrentDateProvider.DAY_MS) }
            .distinctUntilChanged()
            .flatMapLatest { dateMs -> repository.observeByDate(questionId, dateMs) }
}
