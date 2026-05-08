package com.yahorshymanchyk.selectorassist

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.entry.component.EntryComponent
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryIntent
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryState
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WebEntryComponent(
    questionId: Long,
    getQuestionById: GetQuestionByIdUseCase,
    getTodayEntry: GetTodayEntryUseCase,
    saveEntry: SaveEntryUseCase,
    clock: CurrentDateProvider,
) : EntryComponent {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel =
        EntryViewModel(
            questionId = questionId,
            getQuestionById = getQuestionById,
            getTodayEntry = getTodayEntry,
            saveEntry = saveEntry,
            clock = clock,
            coroutineScope = scope,
            onSaved = {},
        )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<EntryState> = _state

    init {
        scope.launch { viewModel.state.collect { _state.value = it } }
    }

    fun cancel() = scope.cancel()

    override fun onIntent(intent: EntryIntent) = viewModel.onIntent(intent)

    override fun onBack() = Unit
}
