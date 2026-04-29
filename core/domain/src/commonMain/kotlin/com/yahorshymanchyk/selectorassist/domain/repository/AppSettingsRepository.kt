package com.yahorshymanchyk.selectorassist.domain.repository

import com.yahorshymanchyk.selectorassist.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun observe(): Flow<AppSettings>
    suspend fun setBiometryEnabled(enabled: Boolean)
}
