package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val stack: Value<ChildStack<*, RootChild>>

    sealed class RootChild {
        class Biometry(val component: BiometryComponent) : RootChild()
        class Home(val component: HomeComponent) : RootChild()
    }
}
