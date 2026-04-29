package com.yahorshymanchyk.selectorassist.di

import com.yahorshymanchyk.selectorassist.data.DatabaseDriverFactory
import com.yahorshymanchyk.selectorassist.data.db.AppDatabase
import com.yahorshymanchyk.selectorassist.data.repository.AppSettingsRepositoryImpl
import com.yahorshymanchyk.selectorassist.data.repository.EntryRepositoryImpl
import com.yahorshymanchyk.selectorassist.data.repository.QuestionRepositoryImpl
import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.DeleteQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetAppSettingsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SetBiometryEnabledUseCase
import org.koin.dsl.module

// Requires DatabaseDriverFactory to be provided by a platform-specific module
val dataModule = module {
    single { AppDatabase(get<DatabaseDriverFactory>().create()) }
    single<QuestionRepository> { QuestionRepositoryImpl(get()) }
    single<EntryRepository> { EntryRepositoryImpl(get()) }
    single<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
}

val domainModule = module {
    factory { GetActiveQuestionsUseCase(get()) }
    factory { GetCompletedQuestionsUseCase(get()) }
    factory { GetActiveQuestionSummariesUseCase(get(), get()) }
    factory { GetCompletedQuestionSummariesUseCase(get(), get()) }
    factory { GetQuestionByIdUseCase(get()) }
    factory { CreateQuestionUseCase(get()) }
    factory { DeleteQuestionUseCase(get()) }
    factory { GetTodayEntryUseCase(get()) }
    factory { SaveEntryUseCase(get()) }
    factory { GetQuestionStatsUseCase(get()) }
    factory { GetAppSettingsUseCase(get()) }
    factory { SetBiometryEnabledUseCase(get()) }
}
