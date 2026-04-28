package com.yahorshymanchyk.selectorassist.domain.model

// Hardcoded classification attached to an entry to identify the source of a decision
enum class Tag(val group: TagGroup) {
    // Noise — false filters that distort judgment
    FEAR_OF_FUTURE(TagGroup.NOISE),
    OPINION_OF_OTHERS(TagGroup.NOISE),
    PAST_EXPERIENCE(TagGroup.NOISE),
    GUILT(TagGroup.NOISE),
    EMOTIONS_IMPULSES(TagGroup.NOISE),
    SELF_DOUBT(TagGroup.NOISE),
    FATIGUE_BURNOUT(TagGroup.NOISE),
    SOCIAL_EXPECTATIONS(TagGroup.NOISE),

    // Healthy — grounded, reliable reasons
    MY_VALUES(TagGroup.HEALTHY),
    FACTS_REASON(TagGroup.HEALTHY),
    INTUITION(TagGroup.HEALTHY),
    SELF_CARE(TagGroup.HEALTHY),
    LONG_TERM_GOALS(TagGroup.HEALTHY),
    PERSONAL_FREEDOM(TagGroup.HEALTHY),
    INNER_PEACE(TagGroup.HEALTHY),
    OBJECTIVE_OPPORTUNITIES(TagGroup.HEALTHY),
}
