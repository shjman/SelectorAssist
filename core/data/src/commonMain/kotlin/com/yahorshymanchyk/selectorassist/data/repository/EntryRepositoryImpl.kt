package com.yahorshymanchyk.selectorassist.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.yahorshymanchyk.selectorassist.data.db.AppDatabase
import com.yahorshymanchyk.selectorassist.data.mapper.toDomain
import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EntryRepositoryImpl(
    private val database: AppDatabase,
) : EntryRepository {
    override fun observeByDate(
        questionId: Long,
        dateMs: Long,
    ): Flow<Entry?> =
        database.entriesQueries
            .selectByDate(questionId, dateMs)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.let {
                    val tagNames = database.entry_tagsQueries.selectAllForEntry(it.id).executeAsList()
                    it.toDomain(tagNames)
                }
            }.flowOn(Dispatchers.Default)

    override fun observeAll(questionId: Long): Flow<List<Entry>> =
        database.entriesQueries
            .selectAllByQuestion(questionId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    val tagNames = database.entry_tagsQueries.selectAllForEntry(entity.id).executeAsList()
                    entity.toDomain(tagNames)
                }
            }.flowOn(Dispatchers.Default)

    override suspend fun upsert(
        questionId: Long,
        date: Long,
        sliderValue: Int,
        tags: List<Tag>,
        comment: String?,
    ) {
        withContext(Dispatchers.Default) {
            database.transaction {
                val existing = database.entriesQueries.selectByDate(questionId, date).executeAsOneOrNull()
                if (existing != null) {
                    database.entriesQueries.update(sliderValue.toLong(), comment, existing.id)
                    database.entry_tagsQueries.deleteAllForEntry(existing.id)
                } else {
                    database.entriesQueries.insert(questionId, date, sliderValue.toLong(), comment)
                }
                val entryId =
                    database.entriesQueries
                        .selectByDate(questionId, date)
                        .executeAsOne()
                        .id
                tags.forEach { tag ->
                    database.entry_tagsQueries.insert(entryId, tag.name)
                }
            }
        }
    }
}
