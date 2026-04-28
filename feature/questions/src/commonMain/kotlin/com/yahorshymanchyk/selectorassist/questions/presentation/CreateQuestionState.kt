package com.yahorshymanchyk.selectorassist.questions.presentation

private const val DEFAULT_DURATION_DAYS = 30

data class CreateQuestionState(
    val title: String = "",
    val poleA: String = "",
    val poleB: String = "",
    val durationDays: Int = DEFAULT_DURATION_DAYS,
    val isLoading: Boolean = false,
) {
    val canSubmit: Boolean
        get() = title.isNotBlank() && poleA.isNotBlank() && poleB.isNotBlank() && !isLoading
}
