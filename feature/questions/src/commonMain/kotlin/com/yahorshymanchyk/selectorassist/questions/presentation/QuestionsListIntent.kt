package com.yahorshymanchyk.selectorassist.questions.presentation

sealed interface QuestionsListIntent {
    data class OpenQuestion(val questionId: Long) : QuestionsListIntent
    data object OpenCreateQuestion : QuestionsListIntent
}
