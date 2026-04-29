package com.yahorshymanchyk.selectorassist.report.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.report.presentation.ReportState
import com.yahorshymanchyk.selectorassist.report.presentation.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DefaultReportComponent(
    componentContext: ComponentContext,
    questionId: Long,
    private val onNavigateBack: () -> Unit,
    getQuestionById: GetQuestionByIdUseCase,
    getQuestionStats: GetQuestionStatsUseCase,
) : ReportComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel = ReportViewModel(
        questionId = questionId,
        getQuestionById = getQuestionById,
        getQuestionStats = getQuestionStats,
        coroutineScope = scope,
    )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<ReportState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    override fun onBack() = onNavigateBack()
}
