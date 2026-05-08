package com.yahorshymanchyk.selectorassist.di

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

val domainModule =
    module {
        factory { GetActiveQuestionsUseCase(get()) }
        factory { GetCompletedQuestionsUseCase(get()) }
        factory { GetActiveQuestionSummariesUseCase(get(), get(), get()) }
        factory { GetCompletedQuestionSummariesUseCase(get(), get()) }
        factory { GetQuestionByIdUseCase(get()) }
        factory { CreateQuestionUseCase(get(), get()) }
        factory { DeleteQuestionUseCase(get()) }
        factory { GetTodayEntryUseCase(get(), get()) }
        factory { SaveEntryUseCase(get(), get()) }
        factory { GetQuestionStatsUseCase(get()) }
        factory { GetAppSettingsUseCase(get()) }
        factory { SetBiometryEnabledUseCase(get()) }
    }
