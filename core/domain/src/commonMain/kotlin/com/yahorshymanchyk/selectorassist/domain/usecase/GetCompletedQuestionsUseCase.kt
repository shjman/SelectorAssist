package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetCompletedQuestionsUseCase(private val repository: QuestionRepository) {
    operator fun invoke(): Flow<List<Question>> = repository.observeCompleted()
}
