package com.yahorshymanchyk.selectorassist.domain.model

// Aggregated statistics computed from all entries for a question
data class QuestionStats(
    val sliderDistribution: Map<Int, Int>,
    val tagFrequency: Map<Tag, Int>,
    val poleAArguments: List<String>,
    val poleBArguments: List<String>,
)
