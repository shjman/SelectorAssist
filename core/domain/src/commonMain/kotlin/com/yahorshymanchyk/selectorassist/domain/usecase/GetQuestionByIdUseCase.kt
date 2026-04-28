package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetQuestionByIdUseCase(private val repository: QuestionRepository) {
    operator fun invoke(questionId: Long): Flow<Question?> = repository.observeById(questionId)
}
