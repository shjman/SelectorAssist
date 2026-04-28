package com.yahorshymanchyk.selectorassist.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.yahorshymanchyk.selectorassist.data.db.AppDatabase
import com.yahorshymanchyk.selectorassist.data.mapper.toDomain
import com.yahorshymanchyk.selectorassist.domain.SystemClock
import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.repository.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class QuestionRepositoryImpl(private val database: AppDatabase) : QuestionRepository {

    override fun observeActive(): Flow<List<Question>> =
        database.questionsQueries.selectActive(SystemClock.now())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeCompleted(): Flow<List<Question>> =
        database.questionsQueries.selectCompleted(SystemClock.now())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeById(questionId: Long): Flow<Question?> =
        database.questionsQueries.selectById(questionId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }

    override suspend fun create(title: String, poleA: String, poleB: String, deadlineAt: Long) {
        withContext(Dispatchers.Default) {
            database.questionsQueries.insert(
                title = title,
                poleA = poleA,
                poleB = poleB,
                createdAt = SystemClock.now(),
                deadlineAt = deadlineAt,
            )
        }
    }

    override suspend fun delete(questionId: Long) {
        withContext(Dispatchers.Default) {
            database.transaction {
                database.entry_tagsQueries.deleteAllForQuestion(questionId)
                database.entriesQueries.deleteAllByQuestion(questionId)
                database.questionsQueries.deleteById(questionId)
            }
        }
    }
}
