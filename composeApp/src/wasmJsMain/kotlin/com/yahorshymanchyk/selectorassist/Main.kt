package com.yahorshymanchyk.selectorassist

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.yahorshymanchyk.selectorassist.di.domainModule
import com.yahorshymanchyk.selectorassist.di.webDataModule
import com.yahorshymanchyk.selectorassist.di.webPlatformModule
import com.yahorshymanchyk.selectorassist.ui.WebGallery
import com.yahorshymanchyk.selectorassist.ui.theme.AppTheme
import kotlinx.browser.document
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(webPlatformModule, webDataModule, domainModule)
    }
    ComposeViewport(document.body!!) {
        AppTheme {
            WebGallery()
        }
    }
}
