package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.report.component.ReportComponent
import com.yahorshymanchyk.selectorassist.report.presentation.ReportState
import com.yahorshymanchyk.selectorassist.report.presentation.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WebReportComponent(
    questionId: Long,
    getQuestionById: GetQuestionByIdUseCase,
    getQuestionStats: GetQuestionStatsUseCase,
) : ReportComponent {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel =
        ReportViewModel(
            questionId = questionId,
            getQuestionById = getQuestionById,
            getQuestionStats = getQuestionStats,
            coroutineScope = scope,
        )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<ReportState> = _state

    init {
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    fun cancel() = scope.cancel()

    override fun onBack() = Unit
}
