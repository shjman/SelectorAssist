package com.yahorshymanchyk.selectorassist.data

import com.yahorshymanchyk.selectorassist.domain.model.AppSettings
import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InMemoryAppSettingsRepository : AppSettingsRepository {
    private val settings = MutableStateFlow(AppSettings(isBiometryEnabled = false))

    override fun observe(): Flow<AppSettings> = settings

    override suspend fun setBiometryEnabled(enabled: Boolean) {
        settings.update { it.copy(isBiometryEnabled = enabled) }
    }
}
