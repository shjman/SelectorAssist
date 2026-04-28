package com.yahorshymanchyk.selectorassist.domain.model

data class CompletedQuestionSummary(
    val question: Question,
    val dominantPole: String,
    val dominantPercentage: Int,
)
