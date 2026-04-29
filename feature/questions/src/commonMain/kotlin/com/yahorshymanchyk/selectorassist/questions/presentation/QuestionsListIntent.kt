package com.yahorshymanchyk.selectorassist.questions.presentation

sealed interface QuestionsListIntent {
    data class OpenQuestion(val questionId: Long, val isCompleted: Boolean) : QuestionsListIntent
    data object OpenCreateQuestion : QuestionsListIntent
}
