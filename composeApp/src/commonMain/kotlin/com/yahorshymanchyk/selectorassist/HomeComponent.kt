package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.entry.component.EntryComponent
import com.yahorshymanchyk.selectorassist.questions.component.CreateQuestionComponent
import com.yahorshymanchyk.selectorassist.questions.component.QuestionsListComponent

interface HomeComponent {
    val stack: Value<ChildStack<*, HomeChild>>

    sealed class HomeChild {
        class QuestionsList(val component: QuestionsListComponent) : HomeChild()
        class CreateQuestion(val component: CreateQuestionComponent) : HomeChild()
        class Entry(val component: EntryComponent) : HomeChild()
    }
}
