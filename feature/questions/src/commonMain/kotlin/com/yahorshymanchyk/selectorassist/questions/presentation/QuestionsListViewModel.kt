package com.yahorshymanchyk.selectorassist.questions.presentation

import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class QuestionsListViewModel(
    getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase,
    getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase,
    scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(QuestionsListState())
    val state: StateFlow<QuestionsListState> = _state.asStateFlow()

    init {
        scope.launch {
            combine(
                getActiveQuestionSummaries(),
                getCompletedQuestionSummaries(),
            ) { active, completed ->
                QuestionsListState(
                    isLoading = false,
                    activeQuestions = active,
                    completedQuestions = completed,
                )
            }.collect { _state.value = it }
        }
    }
}
