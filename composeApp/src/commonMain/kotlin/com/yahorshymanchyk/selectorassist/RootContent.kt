package com.yahorshymanchyk.selectorassist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.questions.ui.CreateQuestionScreen
import com.yahorshymanchyk.selectorassist.questions.ui.QuestionsListScreen
import com.yahorshymanchyk.selectorassist.ui.theme.AppTheme

@Composable
fun RootContent(component: RootComponent) {
    AppTheme {
        HomeContent(component.homeComponent)
    }
}

@Composable
private fun HomeContent(component: HomeComponent) {
    val stack by component.stack.subscribeAsState()

    Children(stack = stack) { child ->
        when (val instance = child.instance) {
            is HomeComponent.HomeChild.QuestionsList -> QuestionsListScreen(instance.component)
            is HomeComponent.HomeChild.CreateQuestion -> CreateQuestionScreen(instance.component)
        }
    }
}
