package com.yahorshymanchyk.selectorassist.questions.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionIntent
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionState
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DefaultCreateQuestionComponent(
    componentContext: ComponentContext,
    private val onNavigateBack: () -> Unit,
    createQuestionUseCase: CreateQuestionUseCase,
) : CreateQuestionComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val viewModel = CreateQuestionViewModel(
        createQuestionUseCase = createQuestionUseCase,
        coroutineScope = scope,
        onCreated = onNavigateBack,
    )

    private val _state = MutableValue(viewModel.state.value)
    override val state: Value<CreateQuestionState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }
        scope.launch {
            viewModel.state.collect { _state.value = it }
        }
    }

    override fun onIntent(intent: CreateQuestionIntent) = viewModel.onIntent(intent)
    override fun onBack() = onNavigateBack()
}
