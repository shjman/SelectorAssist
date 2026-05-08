package com.yahorshymanchyk.selectorassist.di

import com.yahorshymanchyk.selectorassist.data.DatabaseDriverFactory
import com.yahorshymanchyk.selectorassist.data.db.AppDatabase
import com.yahorshymanchyk.selectorassist.data.repository.AppSettingsRepositoryImpl
import com.yahorshymanchyk.selectorassist.data.repository.EntryRepositoryImpl
import com.yahorshymanchyk.selectorassist.data.repository.QuestionRepositoryImpl
import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.SystemCurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule =
    module {
        single<CurrentDateProvider> { SystemCurrentDateProvider() }
        single { DatabaseDriverFactory(androidContext()) }
        single { AppDatabase(get<DatabaseDriverFactory>().create()) }
        single<QuestionRepository> { QuestionRepositoryImpl(get()) }
        single<EntryRepository> { EntryRepositoryImpl(get()) }
        single<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
    }
