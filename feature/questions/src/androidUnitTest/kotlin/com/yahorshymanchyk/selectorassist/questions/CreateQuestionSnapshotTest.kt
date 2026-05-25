package com.yahorshymanchyk.selectorassist.questions

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.takahirom.roborazzi.captureRoboImage
import com.yahorshymanchyk.selectorassist.questions.component.CreateQuestionComponent
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionIntent
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionState
import com.yahorshymanchyk.selectorassist.questions.ui.CreateQuestionScreen
import com.yahorshymanchyk.selectorassist.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CreateQuestionSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun empty() {
        composeTestRule.setContent {
            AppTheme {
                CreateQuestionScreen(FakeCreateQuestionComponent(CreateQuestionState()))
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun filled() {
        composeTestRule.setContent {
            AppTheme {
                CreateQuestionScreen(
                    FakeCreateQuestionComponent(
                        CreateQuestionState(
                            title = "Уволиться и запустить свой проект?",
                            poleA = "Остаться",
                            poleB = "Уйти",
                            durationDays = 30,
                        ),
                    ),
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}

private class FakeCreateQuestionComponent(
    state: CreateQuestionState,
) : CreateQuestionComponent {
    override val state: Value<CreateQuestionState> = MutableValue(state)

    override fun onIntent(intent: CreateQuestionIntent) = Unit

    override fun onBack() = Unit
}
