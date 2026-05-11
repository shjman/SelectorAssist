@file:Suppress("MagicNumber")

package com.yahorshymanchyk.selectorassist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.WebCreateQuestionComponent
import com.yahorshymanchyk.selectorassist.WebCurrentDateProvider
import com.yahorshymanchyk.selectorassist.WebEntryComponent
import com.yahorshymanchyk.selectorassist.WebQuestionsListComponent
import com.yahorshymanchyk.selectorassist.WebReportComponent
import com.yahorshymanchyk.selectorassist.domain.model.Question
import com.yahorshymanchyk.selectorassist.domain.usecase.CreateQuestionUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetActiveQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetCompletedQuestionSummariesUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionByIdUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetQuestionStatsUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.GetTodayEntryUseCase
import com.yahorshymanchyk.selectorassist.domain.usecase.SaveEntryUseCase
import com.yahorshymanchyk.selectorassist.entry.ui.EntryScreen
import com.yahorshymanchyk.selectorassist.questions.ui.CreateQuestionScreen
import com.yahorshymanchyk.selectorassist.questions.ui.QuestionsListScreen
import com.yahorshymanchyk.selectorassist.report.ui.ReportScreen
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import org.koin.compose.koinInject

private val PHONE_WIDTH = 390.dp
private val PHONE_HEIGHT = 800.dp
private val FRAME_CORNER = 32.dp
private val FRAME_GAP = 24.dp
private val FRAME_BORDER = 2.dp

@Composable
fun WebGallery() {
    val clock = koinInject<WebCurrentDateProvider>()
    val offsetDays by clock.offsetDays.collectAsState()
    val getActiveQuestionSummaries = koinInject<GetActiveQuestionSummariesUseCase>()
    val getCompletedQuestionSummaries = koinInject<GetCompletedQuestionSummariesUseCase>()
    val createQuestion = koinInject<CreateQuestionUseCase>()

    val questionsListComponent =
        remember {
            WebQuestionsListComponent(getActiveQuestionSummaries, getCompletedQuestionSummaries)
        }
    DisposableEffect(Unit) { onDispose { questionsListComponent.cancel() } }

    var formKey by remember { mutableStateOf(0) }
    key(formKey) {
        val createQuestionComponent =
            remember {
                WebCreateQuestionComponent(createQuestion, onCreated = { formKey++ })
            }
        DisposableEffect(formKey) { onDispose { createQuestionComponent.cancel() } }
        val questionsState by questionsListComponent.state.subscribeAsState()

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .background(AppColors.Background),
        ) {
            DateSwitcherBar(
                offsetDays = offsetDays,
                onRetreat = { clock.retreat() },
                onAdvance = { clock.advance() },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add a new question to see the question screen.",
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = FRAME_GAP, vertical = FRAME_GAP),
                verticalAlignment = Alignment.Top,
            ) {
                PhoneFrame(label = "Questions") { QuestionsListScreen(questionsListComponent) }
                Spacer(modifier = Modifier.width(FRAME_GAP))
                PhoneFrame(label = "Create Question") { CreateQuestionScreen(createQuestionComponent) }
                questionsState.activeQuestions.forEach { summary ->
                    key(summary.question.id) {
                        QuestionFramePair(
                            summary.question,
                            completed = false
                        )
                    }
                }
                questionsState.completedQuestions.forEach { summary ->
                    key("completed_${summary.question.id}") {
                        QuestionFramePair(
                            summary.question,
                            completed = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionFramePair(
    question: Question,
    completed: Boolean,
) {
    val getQuestionById = koinInject<GetQuestionByIdUseCase>()
    val getTodayEntry = koinInject<GetTodayEntryUseCase>()
    val saveEntry = koinInject<SaveEntryUseCase>()
    val clock = koinInject<WebCurrentDateProvider>()
    val getQuestionStats = koinInject<GetQuestionStatsUseCase>()

    val entryComponent =
        remember {
            WebEntryComponent(question.id, getQuestionById, getTodayEntry, saveEntry, clock)
        }
    DisposableEffect(question.id) { onDispose { entryComponent.cancel() } }

    val reportComponent =
        remember {
            WebReportComponent(question.id, getQuestionById, getQuestionStats)
        }
    DisposableEffect(question.id) { onDispose { reportComponent.cancel() } }

    val suffix = if (completed) " (done)" else ""
    Spacer(modifier = Modifier.width(FRAME_GAP))
    PhoneFrame(label = "Entry$suffix · ${question.title}") { EntryScreen(entryComponent) }
    Spacer(modifier = Modifier.width(FRAME_GAP))
    PhoneFrame(label = "Report$suffix · ${question.title}") { ReportScreen(reportComponent) }
}

@Composable
private fun PhoneFrame(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier =
                Modifier
                    .width(PHONE_WIDTH)
                    .height(PHONE_HEIGHT)
                    .border(FRAME_BORDER, AppColors.Divider, RoundedCornerShape(FRAME_CORNER))
                    .clip(RoundedCornerShape(FRAME_CORNER))
                    .background(AppColors.Background),
        ) {
            content()
        }
    }
}
