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
import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SetBiometryEnabledUseCase
import com.yahorshymanchyk.selectorassist.entry.component.DefaultEntryComponent
import com.yahorshymanchyk.selectorassist.questions.component.DefaultCreateQuestionComponent
import com.yahorshymanchyk.selectorassist.questions.component.DefaultQuestionsListComponent
import com.yahorshymanchyk.selectorassist.report.component.DefaultReportComponent
import com.yahorshymanchyk.selectorassist.settings.component.DefaultSettingsComponent

// All parameters are use cases passed straight to child components — no logic here, pure DI wiring.
@Suppress("LongParameterList")
class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase,
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase,
    private val createQuestion: CreateQuestionUseCase,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val getTodayEntry: GetTodayEntryUseCase,
    private val saveEntry: SaveEntryUseCase,
    private val getQuestionStats: GetQuestionStatsUseCase,
    private val getAppSettings: GetAppSettingsUseCase,
    private val setBiometryEnabled: SetBiometryEnabledUseCase,
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
                    onNavigateToEntry = { navigation.push(HomeConfig.Entry(it)) },
                    onNavigateToReport = { navigation.push(HomeConfig.Report(it)) },
                    onNavigateToCreate = { navigation.push(HomeConfig.CreateQuestion) },
                    onNavigateToSettings = { navigation.push(HomeConfig.Settings) },
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
            is HomeConfig.Report -> HomeComponent.HomeChild.Report(
                DefaultReportComponent(
                    componentContext = context,
                    questionId = config.questionId,
                    onNavigateBack = { navigation.pop() },
                    getQuestionById = getQuestionById,
                    getQuestionStats = getQuestionStats,
                )
            )
            HomeConfig.Settings -> HomeComponent.HomeChild.Settings(
                DefaultSettingsComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() },
                    getAppSettings = getAppSettings,
                    setBiometryEnabled = setBiometryEnabled,
                )
            )
        }

    private sealed interface HomeConfig {
        data object QuestionsList : HomeConfig
        data object CreateQuestion : HomeConfig
        data class Entry(val questionId: Long) : HomeConfig
        data class Report(val questionId: Long) : HomeConfig
        data object Settings : HomeConfig
    }
}
