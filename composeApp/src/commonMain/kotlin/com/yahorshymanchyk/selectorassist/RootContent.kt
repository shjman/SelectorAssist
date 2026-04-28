package com.yahorshymanchyk.selectorassist

import androidx.compose.runtime.Composable
import com.yahorshymanchyk.selectorassist.questions.ui.QuestionsListScreen
import com.yahorshymanchyk.selectorassist.ui.theme.AppTheme

@Composable
fun RootContent(component: RootComponent) {
    AppTheme {
        QuestionsListScreen(component.questionsListComponent)
    }
}
