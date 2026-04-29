package com.yahorshymanchyk.selectorassist.domain.usecase

import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository

class SetBiometryEnabledUseCase(private val repository: AppSettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setBiometryEnabled(enabled)
}
