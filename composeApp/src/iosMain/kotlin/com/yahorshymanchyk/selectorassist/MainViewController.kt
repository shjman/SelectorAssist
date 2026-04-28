package com.yahorshymanchyk.selectorassist

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.yahorshymanchyk.selectorassist.di.dataModule
import com.yahorshymanchyk.selectorassist.di.domainModule
import com.yahorshymanchyk.selectorassist.di.iosPlatformModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(iosPlatformModule, dataModule, domainModule)
        }
    }
    val rootComponent = DefaultRootComponent(
        DefaultComponentContext(lifecycle = ApplicationLifecycle())
    )
    RootContent(rootComponent)
}
