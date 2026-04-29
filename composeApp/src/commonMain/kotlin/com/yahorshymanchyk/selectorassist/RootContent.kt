package com.yahorshymanchyk.selectorassist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.entry.ui.EntryScreen
import com.yahorshymanchyk.selectorassist.questions.ui.CreateQuestionScreen
import com.yahorshymanchyk.selectorassist.questions.ui.QuestionsListScreen
import com.yahorshymanchyk.selectorassist.report.ui.ReportScreen
import com.yahorshymanchyk.selectorassist.settings.ui.SettingsScreen
import com.yahorshymanchyk.selectorassist.ui.theme.AppTheme

@Composable
fun RootContent(component: RootComponent) {
    AppTheme {
        val stack by component.stack.subscribeAsState()
        Children(stack = stack) { child ->
            when (val instance = child.instance) {
                is RootComponent.RootChild.Biometry -> BiometryScreen(instance.component)
                is RootComponent.RootChild.Home -> HomeContent(instance.component)
            }
        }
    }
}

@Composable
private fun HomeContent(component: HomeComponent) {
    val stack by component.stack.subscribeAsState()

    Children(stack = stack) { child ->
        when (val instance = child.instance) {
            is HomeComponent.HomeChild.QuestionsList -> QuestionsListScreen(instance.component)
            is HomeComponent.HomeChild.CreateQuestion -> CreateQuestionScreen(instance.component)
            is HomeComponent.HomeChild.Entry -> EntryScreen(instance.component)
            is HomeComponent.HomeChild.Report -> ReportScreen(instance.component)
            is HomeComponent.HomeChild.Settings -> SettingsScreen(instance.component)
        }
    }
}
