package com.yahorshymanchyk.selectorassist.di

import com.yahorshymanchyk.selectorassist.data.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
}
