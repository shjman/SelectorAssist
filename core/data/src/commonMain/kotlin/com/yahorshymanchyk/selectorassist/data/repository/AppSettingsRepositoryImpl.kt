package com.yahorshymanchyk.selectorassist.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.yahorshymanchyk.selectorassist.data.db.AppDatabase
import com.yahorshymanchyk.selectorassist.domain.model.AppSettings
import com.yahorshymanchyk.selectorassist.domain.repository.AppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppSettingsRepositoryImpl(private val database: AppDatabase) : AppSettingsRepository {

    override fun observe(): Flow<AppSettings> =
        database.settingsQueries.selectSettings()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { row -> AppSettings(isBiometryEnabled = row == 1L) }
            .flowOn(Dispatchers.Default)

    override suspend fun setBiometryEnabled(enabled: Boolean) {
        withContext(Dispatchers.Default) {
            database.settingsQueries.upsertBiometryEnabled(if (enabled) 1L else 0L)
        }
    }
}
