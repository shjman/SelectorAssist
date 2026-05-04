package com.yahorshymanchyk.selectorassist.entry.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.yahorshymanchyk.selectorassist.domain.CurrentDateProvider
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryIntent
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryState
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DefaultEntryComponent(
    componentContext: ComponentContext,
    questionId: Long,
    private val onNavigateBack: () -> Unit,
    getQuestionById: GetQuestionByIdUseCase,
    getTodayEntry: GetTodayEntryUseCase,
    saveEntry: SaveEntryUseCase,
    clock: CurrentDateProvider,
) : EntryComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel = EntryViewModel(
        questionId = questionId,
        getQuestionById = getQuestionById,
        getTodayEntry = getTodayEntry,
        saveEntry = saveEntry,
        clock = clock,
        coroutineScope = scope,
        onSaved = onNavigateBack,
    )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<EntryState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch {
            viewModel.state.collect { _state.value = it }
        }
    }

    override fun onIntent(intent: EntryIntent) = viewModel.onIntent(intent)
    override fun onBack() = onNavigateBack()
}
