package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow

class GetActiveQuestionsUseCase(private val repository: QuestionRepository) {
    operator fun invoke(): Flow<List<Question>> = repository.observeActive()
}
