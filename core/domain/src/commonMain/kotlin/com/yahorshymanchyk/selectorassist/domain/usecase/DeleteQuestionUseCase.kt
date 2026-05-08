package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository

class DeleteQuestionUseCase(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(questionId: Long) {
        repository.delete(questionId)
    }
}
