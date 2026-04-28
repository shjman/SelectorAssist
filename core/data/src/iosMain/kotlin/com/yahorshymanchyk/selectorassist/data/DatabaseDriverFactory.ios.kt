package com.yahorshymanchyk.selectorassist.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.yahorshymanchyk.selectorassist.data.db.AppDatabase

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver = NativeSqliteDriver(AppDatabase.Schema, "app_database.db")
}
