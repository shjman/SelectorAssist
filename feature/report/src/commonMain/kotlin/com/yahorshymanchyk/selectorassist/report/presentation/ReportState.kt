package com.yahorshymanchyk.selectorassist.report.presentation

import com.yahorshymanchyk.selectorassist.domain.model.Pole

data class ReportState(
    val questionTitle: String = "",
    val poleA: String = "",
    val poleB: String = "",
    val totalDays: Int = 0,
    val totalEntries: Int = 0,
    val poleATendencyPercent: Int = 0,
    val poleBTendencyPercent: Int = 0,
    val noiseInfluencePole: Pole? = null,
    val healthyInfluencePole: Pole? = null,
    val poleAArguments: List<String> = emptyList(),
    val poleBArguments: List<String> = emptyList(),
    val isLoading: Boolean = true,
)
