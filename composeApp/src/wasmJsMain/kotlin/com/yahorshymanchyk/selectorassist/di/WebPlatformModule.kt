package com.yahorshymanchyk.selectorassist.di

import com.yahorshymanchyk.selectorassist.WebCurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import org.koin.dsl.module

val webPlatformModule = module {
    single { WebCurrentDateProvider() }
    single<CurrentDateProvider> { get<WebCurrentDateProvider>() }
}
