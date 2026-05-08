package com.yahorshymanchyk.selectorassist.domain.model

// Aggregated statistics computed from all entries for a question
data class QuestionStats(
    val totalEntries: Int,
    val poleATendencyPercent: Int,
    val poleBTendencyPercent: Int,
    val noiseInfluencePole: Pole?, // null = tie or no entries with noise tags
    val healthyInfluencePole: Pole?, // null = tie or no entries with healthy tags
    val poleAArguments: List<String>,
    val poleBArguments: List<String>,
)
