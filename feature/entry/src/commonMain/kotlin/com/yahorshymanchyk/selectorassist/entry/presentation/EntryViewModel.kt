package com.yahorshymanchyk.selectorassist.entry.presentation

import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MILLIS_PER_DAY = CurrentDateProvider.DAY_MS
private const val SLIDER_STORAGE_MAX = 10

class EntryViewModel(
    private val questionId: Long,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val getTodayEntry: GetTodayEntryUseCase,
    private val saveEntry: SaveEntryUseCase,
    private val clock: CurrentDateProvider,
    private val coroutineScope: CoroutineScope,
    private val onSaved: () -> Unit,
) {
    private val _state = MutableStateFlow(EntryState())
    val state: StateFlow<EntryState> = _state.asStateFlow()

    init {
        coroutineScope.launch {
            val question = getQuestionById(questionId).first() ?: return@launch
            combine(getTodayEntry(questionId), clock.nowMs) { entry, nowMs ->
                Pair(entry, nowMs)
            }.collect { (entry, nowMs) ->
                val (currentDay, totalDays) = computeDays(question, nowMs)
                _state.update {
                    EntryState(
                        questionTitle = question.title,
                        poleA = question.poleA,
                        poleB = question.poleB,
                        currentDay = currentDay,
                        totalDays = totalDays,
                        sliderValue = entry?.sliderValue?.div(SLIDER_STORAGE_MAX.toFloat()) ?: 0.5f,
                        selectedTags = entry?.tags?.toSet() ?: emptySet(),
                        comment = entry?.comment ?: "",
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onIntent(intent: EntryIntent) {
        when (intent) {
            is EntryIntent.SliderChanged -> _state.update { it.copy(sliderValue = intent.value) }
            is EntryIntent.TagToggled -> _state.update {
                val tags = it.selectedTags.toMutableSet()
                if (intent.tag in tags) tags.remove(intent.tag) else tags.add(intent.tag)
                it.copy(selectedTags = tags)
            }
            is EntryIntent.CommentChanged -> _state.update { it.copy(comment = intent.text) }
            EntryIntent.Save -> save()
        }
    }

    private fun save() {
        if (_state.value.isSaving) return
        coroutineScope.launch {
            _state.update { it.copy(isSaving = true) }
            val s = _state.value
            saveEntry(
                questionId = questionId,
                sliderValue = (s.sliderValue * SLIDER_STORAGE_MAX).toInt(),
                tags = s.selectedTags.toList(),
                comment = s.comment.takeIf { it.isNotBlank() },
            )
            onSaved()
        }
    }

    private fun computeDays(question: Question, nowMs: Long): Pair<Int, Int> {
        val totalDays = ((question.deadlineAt - question.createdAt) / MILLIS_PER_DAY).toInt().coerceAtLeast(1)
        val currentDay = ((nowMs - question.createdAt) / MILLIS_PER_DAY + 1L)
            .toInt()
            .coerceIn(1, totalDays)
        return currentDay to totalDays
    }
}
