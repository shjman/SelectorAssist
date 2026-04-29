package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.model.AppSettings
import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow

class GetAppSettingsUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.observe()
}
