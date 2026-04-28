package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext, KoinComponent {

    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase by inject()
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase by inject()
    private val createQuestion: CreateQuestionUseCase by inject()

    override val homeComponent: HomeComponent = DefaultHomeComponent(
        componentContext = childContext(key = "home"),
        getActiveQuestionSummaries = getActiveQuestionSummaries,
        getCompletedQuestionSummaries = getCompletedQuestionSummaries,
        createQuestion = createQuestion,
    )
}
