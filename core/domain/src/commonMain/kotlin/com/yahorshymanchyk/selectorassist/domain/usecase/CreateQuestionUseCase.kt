package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository

class CreateQuestionUseCase(
    private val repository: QuestionRepository,
    private val clock: CurrentDateProvider,
) {
    suspend operator fun invoke(
        title: String,
        poleA: String,
        poleB: String,
        durationDays: Int,
    ) {
        val deadlineAt = clock.now() + durationDays * CurrentDateProvider.DAY_MS
        repository.create(title, poleA, poleB, deadlineAt)
    }
}
