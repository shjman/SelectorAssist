package com.yahorshymanchyk.selectorassist.questions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.domain.model.ActiveQuestionSummary
import com.yahorshymanchyk.selectorassist.domain.model.CompletedQuestionSummary
import com.yahorshymanchyk.selectorassist.questions.component.QuestionsListComponent
import com.yahorshymanchyk.selectorassist.questions.presentation.QuestionsListIntent
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import androidx.compose.material3.Text

@Composable
fun QuestionsListScreen(component: QuestionsListComponent) {
    val state by component.state.subscribeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                ScreenHeader(
                    onCreateClick = { component.onIntent(QuestionsListIntent.OpenCreateQuestion) },
                )
            }

            items(
                items = state.activeQuestions,
                key = { it.question.id },
            ) { summary ->
                ActiveQuestionCard(
                    summary = summary,
                    onClick = {
                        component.onIntent(QuestionsListIntent.OpenQuestion(summary.question.id))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                )
            }

            if (state.completedQuestions.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(1.dp)
                            .background(AppColors.Divider),
                    )
                }

                items(
                    items = state.completedQuestions,
                    key = { "completed_${it.question.id}" },
                ) { summary ->
                    CompletedQuestionRow(
                        summary = summary,
                        onClick = {
                            component.onIntent(QuestionsListIntent.OpenQuestion(summary.question.id))
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(onCreateClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Мои вопросы",
            color = AppColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(AppColors.Surface)
                .clickable(onClick = onCreateClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = AppColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}

@Composable
private fun ActiveQuestionCard(
    summary: ActiveQuestionSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(16.dp)
    val isPending = !summary.hasTodayEntry

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(AppColors.Surface)
            .then(
                if (isPending) {
                    Modifier.border(width = 1.dp, color = AppColors.PendingBorder, shape = cardShape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Column {
            if (isPending) PendingBadge()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = summary.question.title,
                    color = AppColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(text = "›", color = AppColors.Chevron, fontSize = 22.sp, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(Modifier.height(8.dp))

            PoleLabels(poleA = summary.question.poleA, poleB = summary.question.poleB)

            Spacer(Modifier.height(12.dp))

            CardProgress(currentDay = summary.currentDay, totalDays = summary.totalDays)
        }
    }
}

@Composable
private fun PendingBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(AppColors.PendingIndicator),
        )
        Spacer(Modifier.width(6.dp))
        Text(text = "ещё не отмечено", color = AppColors.PendingIndicator, fontSize = 13.sp)
    }
}

@Composable
private fun PoleLabels(poleA: String, poleB: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = poleA, color = AppColors.PoleA, fontSize = 14.sp)
        Text(text = " · ", color = AppColors.TextSecondary, fontSize = 14.sp)
        Text(text = poleB, color = AppColors.PoleB, fontSize = 14.sp)
    }
}

@Composable
private fun CardProgress(currentDay: Int, totalDays: Int) {
    LinearProgressIndicator(
        progress = currentDay.toFloat() / totalDays.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(50)),
        color = AppColors.ProgressFill,
        trackColor = AppColors.ProgressTrack,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = "день $currentDay из $totalDays",
        color = AppColors.TextSecondary,
        fontSize = 13.sp,
    )
}

@Composable
private fun CompletedQuestionRow(
    summary: CompletedQuestionSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = summary.question.title,
                color = AppColors.TextSecondary,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "›",
                color = AppColors.Chevron,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = summary.question.poleA,
                color = AppColors.PoleA.copy(alpha = 0.65f),
                fontSize = 13.sp,
            )
            Text(
                text = " · ",
                color = AppColors.TextTertiary,
                fontSize = 13.sp,
            )
            Text(
                text = summary.question.poleB,
                color = AppColors.PoleB.copy(alpha = 0.65f),
                fontSize = 13.sp,
            )
            Text(
                text = "  ${summary.dominantPercentage}% → ${summary.dominantPole}",
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}
