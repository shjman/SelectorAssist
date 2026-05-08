package com.yahorshymanchyk.selectorassist.questions.presentation

sealed interface CreateQuestionIntent {
    data class UpdateTitle(
        val value: String,
    ) : CreateQuestionIntent

    data class UpdatePoleA(
        val value: String,
    ) : CreateQuestionIntent

    data class UpdatePoleB(
        val value: String,
    ) : CreateQuestionIntent

    data class SetDuration(
        val days: Int,
    ) : CreateQuestionIntent

    data object Submit : CreateQuestionIntent
}
