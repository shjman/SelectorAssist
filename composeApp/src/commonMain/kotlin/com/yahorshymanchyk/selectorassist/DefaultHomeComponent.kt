package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.questions.component.DefaultCreateQuestionComponent
import com.yahorshymanchyk.selectorassist.questions.component.DefaultQuestionsListComponent

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase,
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase,
    private val createQuestion: CreateQuestionUseCase,
) : HomeComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<HomeConfig>()

    override val stack: Value<ChildStack<*, HomeComponent.HomeChild>> = childStack(
        source = navigation,
        serializer = null,
        initialConfiguration = HomeConfig.QuestionsList,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: HomeConfig, context: ComponentContext): HomeComponent.HomeChild =
        when (config) {
            HomeConfig.QuestionsList -> HomeComponent.HomeChild.QuestionsList(
                DefaultQuestionsListComponent(
                    componentContext = context,
                    onNavigateToQuestion = { /* TODO */ },
                    onNavigateToCreate = { navigation.push(HomeConfig.CreateQuestion) },
                    getActiveQuestionSummaries = getActiveQuestionSummaries,
                    getCompletedQuestionSummaries = getCompletedQuestionSummaries,
                )
            )
            HomeConfig.CreateQuestion -> HomeComponent.HomeChild.CreateQuestion(
                DefaultCreateQuestionComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() },
                    createQuestionUseCase = createQuestion,
                )
            )
        }

    private sealed interface HomeConfig {
        data object QuestionsList : HomeConfig
        data object CreateQuestion : HomeConfig
    }
}
