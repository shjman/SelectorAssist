package com.yahorshymanchyk.selectorassist.data

import app.cash.sqldelight.db.SqlDriver

// Platform-specific factory that creates the SQLite driver
expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}
