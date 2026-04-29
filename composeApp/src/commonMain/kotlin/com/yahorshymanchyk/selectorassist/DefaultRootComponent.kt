package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext, KoinComponent {

    private val getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase by inject()
    private val getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase by inject()
    private val createQuestion: CreateQuestionUseCase by inject()
    private val getQuestionById: GetQuestionByIdUseCase by inject()
    private val getTodayEntry: GetTodayEntryUseCase by inject()
    private val saveEntry: SaveEntryUseCase by inject()
    private val getQuestionStats: GetQuestionStatsUseCase by inject()

    override val homeComponent: HomeComponent = DefaultHomeComponent(
        componentContext = childContext(key = "home"),
        getActiveQuestionSummaries = getActiveQuestionSummaries,
        getCompletedQuestionSummaries = getCompletedQuestionSummaries,
        createQuestion = createQuestion,
        getQuestionById = getQuestionById,
        getTodayEntry = getTodayEntry,
        saveEntry = saveEntry,
        getQuestionStats = getQuestionStats,
    )
}
