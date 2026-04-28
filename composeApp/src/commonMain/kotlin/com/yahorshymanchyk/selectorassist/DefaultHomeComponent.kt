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
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.entry.component.DefaultEntryComponent
import com.yahorshymanchyk.selectorassist.questions.component.DefaultCreateQuestionComponent
import com.yahorshymanchyk.selectorassist.questions.component.DefaultQuestionsListComponent

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase,
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase,
    private val createQuestion: CreateQuestionUseCase,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val getTodayEntry: GetTodayEntryUseCase,
    private val saveEntry: SaveEntryUseCase,
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
                    onNavigateToQuestion = { navigation.push(HomeConfig.Entry(it)) },
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
            is HomeConfig.Entry -> HomeComponent.HomeChild.Entry(
                DefaultEntryComponent(
                    componentContext = context,
                    questionId = config.questionId,
                    onNavigateBack = { navigation.pop() },
                    getQuestionById = getQuestionById,
                    getTodayEntry = getTodayEntry,
                    saveEntry = saveEntry,
                )
            )
        }

    private sealed interface HomeConfig {
        data object QuestionsList : HomeConfig
        data object CreateQuestion : HomeConfig
        data class Entry(val questionId: Long) : HomeConfig
    }
}
