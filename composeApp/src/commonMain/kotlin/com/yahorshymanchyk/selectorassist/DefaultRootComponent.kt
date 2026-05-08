package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SetBiometryEnabledUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent,
    ComponentContext by componentContext,
    KoinComponent {
    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase by inject()
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase by inject()
    private val createQuestion: CreateQuestionUseCase by inject()
    private val getQuestionById: GetQuestionByIdUseCase by inject()
    private val getTodayEntry: GetTodayEntryUseCase by inject()
    private val saveEntry: SaveEntryUseCase by inject()
    private val getQuestionStats: GetQuestionStatsUseCase by inject()
    private val getAppSettings: GetAppSettingsUseCase by inject()
    private val setBiometryEnabled: SetBiometryEnabledUseCase by inject()
    private val clock: CurrentDateProvider by inject()

    private val navigation = StackNavigation<RootConfig>()

    override val stack: Value<ChildStack<*, RootComponent.RootChild>> =
        childStack(
            source = navigation,
            serializer = null,
            initialConfiguration = RootConfig.Biometry,
            handleBackButton = false,
            childFactory = ::createChild,
        )

    private fun createChild(
        config: RootConfig,
        context: ComponentContext,
    ): RootComponent.RootChild =
        when (config) {
            RootConfig.Biometry ->
                RootComponent.RootChild.Biometry(
                    DefaultBiometryComponent(
                        componentContext = context,
                        getAppSettings = getAppSettings,
                        onAuthenticated = { navigation.replaceAll(RootConfig.Home) },
                    ),
                )
            RootConfig.Home ->
                RootComponent.RootChild.Home(
                    DefaultHomeComponent(
                        componentContext = context,
                        getActiveQuestionSummaries = getActiveQuestionSummaries,
                        getCompletedQuestionSummaries = getCompletedQuestionSummaries,
                        createQuestion = createQuestion,
                        getQuestionById = getQuestionById,
                        getTodayEntry = getTodayEntry,
                        saveEntry = saveEntry,
                        getQuestionStats = getQuestionStats,
                        getAppSettings = getAppSettings,
                        setBiometryEnabled = setBiometryEnabled,
                        clock = clock,
                    ),
                )
        }

    private sealed interface RootConfig {
        data object Biometry : RootConfig

        data object Home : RootConfig
    }
}
