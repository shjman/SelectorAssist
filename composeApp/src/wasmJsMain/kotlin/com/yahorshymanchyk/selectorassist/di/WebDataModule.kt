package com.yahorshymanchyk.selectorassist.di

import com.yahorshymanchyk.selectorassist.data.InMemoryAppSettingsRepository
import com.yahorshymanchyk.selectorassist.data.InMemoryEntryRepository
import com.yahorshymanchyk.selectorassist.data.InMemoryQuestionRepository
import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import org.koin.dsl.module

val webDataModule = module {
    single<QuestionRepository> { InMemoryQuestionRepository(get()) }
    single<EntryRepository> { InMemoryEntryRepository() }
    single<AppSettingsRepository> { InMemoryAppSettingsRepository() }
}
