package com.yahorshymanchyk.selectorassist.data.mapper

import com.yahorshymanchyk.selectorassist.data.db.Questions
import com.yahorshymanchyk.selectorassist.domain.SystemClock
import com.yahorshymanchyk.selectorassist.domain.model.Question

internal fun Questions.toDomain(): Question = Question(
    id = id,
    title = title,
    poleA = pole_a,
    poleB = pole_b,
    createdAt = created_at,
    deadlineAt = deadline_at,
    isCompleted = deadline_at <= SystemClock.now(),
)
