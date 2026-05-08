package com.yahorshymanchyk.selectorassist.report.presentation

import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MILLIS_PER_DAY = 86_400_000L

class ReportViewModel(
    private val questionId: Long,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val getQuestionStats: GetQuestionStatsUseCase,
    private val coroutineScope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                getQuestionById(questionId),
                getQuestionStats(questionId),
            ) { question, stats -> question to stats }
                .collect { (question, stats) ->
                    if (question == null) return@collect
                    val totalDays =
                        ((question.deadlineAt - question.createdAt) / MILLIS_PER_DAY)
                            .toInt()
                            .coerceAtLeast(1)
                    _state.update {
                        ReportState(
                            questionTitle = question.title,
                            poleA = question.poleA,
                            poleB = question.poleB,
                            totalDays = totalDays,
                            totalEntries = stats.totalEntries,
                            poleATendencyPercent = stats.poleATendencyPercent,
                            poleBTendencyPercent = stats.poleBTendencyPercent,
                            noiseInfluencePole = stats.noiseInfluencePole,
                            healthyInfluencePole = stats.healthyInfluencePole,
                            poleAArguments = stats.poleAArguments,
                            poleBArguments = stats.poleBArguments,
                            isLoading = false,
                        )
                    }
                }
        }
    }
}
