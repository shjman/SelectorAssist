package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.questions.component.QuestionsListComponent
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListIntent
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListState
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WebQuestionsListComponent(
    getActiveQuestionSummaries: GetActiveQuestionSummariesUseCase,
    getCompletedQuestionSummaries: GetCompletedQuestionSummariesUseCase,
) : QuestionsListComponent {
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
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    fun cancel() = scope.cancel()

    // Navigation intents are no-ops in gallery mode
    override fun onIntent(intent: QuestionsListIntent) = Unit
}
