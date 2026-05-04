package com.yahorshymanchyk.selectorassist.data

import com.yahorshymanchyk.selectorassist.domain.model.Entry
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryEntryRepository : EntryRepository {

    private var nextId = 1L
    // key = Pair(questionId, dateMs) where dateMs is midnight of the day
    private val entries = MutableStateFlow<Map<Pair<Long, Long>, Entry>>(emptyMap())

    override fun observeByDate(questionId: Long, dateMs: Long): Flow<Entry?> =
        entries.map { it[Pair(questionId, dateMs)] }

    override fun observeAll(questionId: Long): Flow<List<Entry>> =
        entries.map { map ->
            map.values
                .filter { it.questionId == questionId }
                .sortedBy { it.date }
        }

    override suspend fun upsert(questionId: Long, date: Long, sliderValue: Int, tags: List<Tag>, comment: String?) {
        val key = Pair(questionId, date)
        entries.update { map ->
            val id = map[key]?.id ?: nextId++
            map + (key to Entry(
                id = id,
                questionId = questionId,
                date = date,
                sliderValue = sliderValue,
                tags = tags,
                comment = comment,
            ))
        }
    }
}
