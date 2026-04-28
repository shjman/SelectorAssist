package com.yahorshymanchyk.selectorassist.questions.presentation

import com.yahorshymanchyk.selectorassist.domain.model.ActiveQuestionSummary
import com.yahorshymanchyk.selectorassist.domain.model.CompletedQuestionSummary

data class QuestionsListState(
    val isLoading: Boolean = true,
    val activeQuestions: List<ActiveQuestionSummary> = emptyList(),
    val completedQuestions: List<CompletedQuestionSummary> = emptyList(),
)
