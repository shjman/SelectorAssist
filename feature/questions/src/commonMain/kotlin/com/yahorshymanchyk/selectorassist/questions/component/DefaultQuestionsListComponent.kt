package com.yahorshymanchyk.selectorassist.questions.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListIntent
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListState
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DefaultQuestionsListComponent(
    componentContext: ComponentContext,
    private val onNavigateToEntry: (Long) -> Unit,
    private val onNavigateToReport: (Long) -> Unit,
    private val onNavigateToCreate: () -> Unit,
    private val onNavigateToSettings: () -> Unit,
    getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase,
    getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase,
) : QuestionsListComponent,
    ComponentContext by componentContext {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel =
        QuestionsListViewModel(
            getActiveQuestionSummaries = getActiveQuestionSummaries,
            getCompletedQuestionSummaries = getCompletedQuestionSummaries,
            scope = scope,
        )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<QuestionsListState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch {
            viewModel.state.collect { _state.value = it }
        }
    }

    override fun onIntent(intent: QuestionsListIntent) {
        when (intent) {
            is QuestionsListIntent.OpenQuestion -> {
                if (intent.isCompleted) {
                    onNavigateToReport(intent.questionId)
                } else {
                    onNavigateToEntry(intent.questionId)
                }
            }
            QuestionsListIntent.OpenCreateQuestion -> onNavigateToCreate()
            QuestionsListIntent.OpenSettings -> onNavigateToSettings()
        }
    }
}
