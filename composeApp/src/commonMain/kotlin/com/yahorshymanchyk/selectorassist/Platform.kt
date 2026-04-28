package com.yahorshymanchyk.selectorassist

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
