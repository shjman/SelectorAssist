package com.yahorshymanchyk.selectorassist.questions.presentation

import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MIN_DURATION_DAYS = 7
private const val MAX_DURATION_DAYS = 365

class CreateQuestionViewModel(
    private val createQuestionUseCase: CreateQuestionUseCase,
    private val coroutineScope: CoroutineScope,
    private val onCreated: () -> Unit,
) {
    private val _state = MutableStateFlow(CreateQuestionState())
    val state: StateFlow<CreateQuestionState> = _state.asStateFlow()

    fun onIntent(intent: CreateQuestionIntent) {
        when (intent) {
            is CreateQuestionIntent.UpdateTitle -> _state.update { it.copy(title = intent.value) }
            is CreateQuestionIntent.UpdatePoleA -> _state.update { it.copy(poleA = intent.value) }
            is CreateQuestionIntent.UpdatePoleB -> _state.update { it.copy(poleB = intent.value) }
            is CreateQuestionIntent.SetDuration ->
                _state.update {
                    it.copy(durationDays = intent.days.coerceIn(MIN_DURATION_DAYS, MAX_DURATION_DAYS))
                }
            CreateQuestionIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val s = _state.value
        if (!s.canSubmit) return
        coroutineScope.launch {
            _state.update { it.copy(isLoading = true) }
            createQuestionUseCase(s.title.trim(), s.poleA.trim(), s.poleB.trim(), s.durationDays)
            onCreated()
        }
    }
}
