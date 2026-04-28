package com.yahorshymanchyk.selectorassist.di

import com.yahorshymanchyk.selectorassist.data.DatabaseDriverFactory
import org.koin.dsl.module

val iosPlatformModule = module {
    single { DatabaseDriverFactory() }
}
