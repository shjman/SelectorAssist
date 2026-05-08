package com.yahorshymanchyk.selectorassist.questions.component

import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionIntent
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionState

interface CreateQuestionComponent {
    val state: Value<CreateQuestionState>

    fun onIntent(intent: CreateQuestionIntent)

    fun onBack()
}
