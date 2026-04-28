package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.questions.component.DefaultQuestionsListComponent
import com.yahorshymanchyk.selectorassist.questions.component.QuestionsListComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext, KoinComponent {

    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase by inject()
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase by inject()

    override val questionsListComponent: QuestionsListComponent = DefaultQuestionsListComponent(
        componentContext = childContext(key = "questions_list"),
        onNavigateToQuestion = { /* TODO */ },
        onNavigateToCreate = { /* TODO */ },
        getActiveQuestionSummaries = getActiveQuestionSummaries,
        getCompletedQuestionSummaries = getCompletedQuestionSummaries,
    )
}
