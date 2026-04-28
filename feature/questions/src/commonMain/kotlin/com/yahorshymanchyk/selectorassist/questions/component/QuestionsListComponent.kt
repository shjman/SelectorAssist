package com.yahorshymanchyk.selectorassist.questions.component

import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListIntent
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListState

interface QuestionsListComponent {
    val state: Value<QuestionsListState>
    fun onIntent(intent: QuestionsListIntent)
}
