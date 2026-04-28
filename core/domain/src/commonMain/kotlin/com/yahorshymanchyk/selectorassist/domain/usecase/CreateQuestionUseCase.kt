package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository

class CreateQuestionUseCase(private val repository: QuestionRepository) {
    suspend operator fun invoke(title: String, poleA: String, poleB: String, deadlineAt: Long) {
        repository.create(title, poleA, poleB, deadlineAt)
    }
}
