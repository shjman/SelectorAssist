@file:Suppress("MagicNumber") // Threshold fractions and layout constants are self-documenting in context

package com.yahorshymanchyk.selectorassist.entry.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.domain.model.Tag
import com.yahorshymanchyk.selectorassist.domain.model.TagGroup
import com.yahorshymanchyk.selectorassist.entry.component.EntryComponent
import com.yahorshymanchyk.selectorassist.entry.presentation.EntryIntent
import com.yahorshymanchyk.selectorassist.ui.components.BackButton
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors

private val CardShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private const val BUTTON_HEIGHT = 72
private const val COMMENT_FIELD_HEIGHT = 96
private const val SLIDER_LOW_THRESHOLD = 0.35f
private const val SLIDER_HIGH_THRESHOLD = 0.65f

@Composable
fun EntryScreen(component: EntryComponent) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        EntryHeader(
            title = state.questionTitle,
            currentDay = state.currentDay,
            totalDays = state.totalDays,
            onBack = component::onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            SliderCard(
                poleA = state.poleA,
                poleB = state.poleB,
                value = state.sliderValue,
                onValueChange = { component.onIntent(EntryIntent.SliderChanged(it)) },
            )

            Spacer(Modifier.height(20.dp))

            TagsSection(
                tags = Tag.entries.filter { it.group == TagGroup.NOISE },
                selectedTags = state.selectedTags,
                groupLabel = "Ложные фильтры",
                groupColor = AppColors.TagGroupNoise,
                onTagToggle = { component.onIntent(EntryIntent.TagToggled(it)) },
            )

            Spacer(Modifier.height(16.dp))

            TagsSection(
                tags = Tag.entries.filter { it.group == TagGroup.HEALTHY },
                selectedTags = state.selectedTags,
                groupLabel = "Опора",
                groupColor = AppColors.TagGroupHealthy,
                onTagToggle = { component.onIntent(EntryIntent.TagToggled(it)) },
            )

            Spacer(Modifier.height(20.dp))

            CommentField(
                value = state.comment,
                onValueChange = { component.onIntent(EntryIntent.CommentChanged(it)) },
            )

            Spacer(Modifier.height(16.dp))
        }

        SaveButton(
            isSaving = state.isSaving,
            onClick = { component.onIntent(EntryIntent.Save) },
        )
    }
}

@Composable
private fun EntryHeader(
    title: String,
    currentDay: Int,
    totalDays: Int,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BackButton(onClick = onBack, tint = AppColors.TextSecondary)
            Text(
                text = "ЕЖЕДНЕВНЫЙ ВВОД",
                color = AppColors.TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            color = AppColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        if (currentDay > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "День $currentDay из $totalDays",
                color = AppColors.TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun SliderCard(
    poleA: String,
    poleB: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(AppColors.Surface)
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = poleA, color = AppColors.PoleA, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = poleB, color = AppColors.PoleB, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.Center)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(listOf(AppColors.PoleA, AppColors.PoleB))
                    ),
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                ),
            )
        }

        Text(
            text = sliderHint(value, poleA, poleB),
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

private fun sliderHint(value: Float, poleA: String, poleB: String): String = when {
    value < SLIDER_LOW_THRESHOLD -> "Ближе к «$poleA»"
    value > SLIDER_HIGH_THRESHOLD -> "Ближе к «$poleB»"
    else -> "Примерно посередине"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
    tags: List<Tag>,
    selectedTags: Set<Tag>,
    groupLabel: String,
    groupColor: Color,
    onTagToggle: (Tag) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(groupColor),
        )
        Text(
            text = groupLabel,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "необязательно",
            color = AppColors.TextTertiary,
            fontSize = 12.sp,
        )
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            TagChip(
                label = tag.label,
                isSelected = tag in selectedTags,
                onClick = { onTagToggle(tag) },
            )
        }
    }
}

@Composable
private fun TagChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(if (isSelected) AppColors.InputFieldSelected else AppColors.InputField)
            .then(
                if (isSelected) Modifier.border(1.dp, AppColors.InputFieldSelectedBorder, ChipShape)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun CommentField(value: String, onValueChange: (String) -> Unit) {
    val textStyle = remember { TextStyle(color = AppColors.TextPrimary, fontSize = 16.sp) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(FieldShape)
            .background(AppColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .height(COMMENT_FIELD_HEIGHT.dp),
        textStyle = textStyle,
        cursorBrush = SolidColor(AppColors.TextPrimary),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Default,
        ),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = "Аргумент или ощущение  •  необязательно",
                        color = AppColors.TextTertiary,
                        fontSize = 16.sp,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun SaveButton(isSaving: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT.dp)
            .background(AppColors.Surface)
            .clickable(enabled = !isSaving, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "сохранить",
            color = if (!isSaving) AppColors.TextPrimary else AppColors.TextTertiary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private val Tag.label: String
    get() = when (this) {
        Tag.FEAR_OF_FUTURE -> "Страх будущего"
        Tag.OPINION_OF_OTHERS -> "Мнение других"
        Tag.PAST_EXPERIENCE -> "Прошлый опыт"
        Tag.GUILT -> "Чувство вины"
        Tag.EMOTIONS_IMPULSES -> "Эмоции и импульсы"
        Tag.SELF_DOUBT -> "Неуверенность в себе"
        Tag.FATIGUE_BURNOUT -> "Усталость / Выгорание"
        Tag.SOCIAL_EXPECTATIONS -> "Социальные ожидания"
        Tag.MY_VALUES -> "Мои ценности"
        Tag.FACTS_REASON -> "Факты и логика"
        Tag.INTUITION -> "Интуиция"
        Tag.SELF_CARE -> "Забота о себе"
        Tag.LONG_TERM_GOALS -> "Долгосрочные цели"
        Tag.PERSONAL_FREEDOM -> "Личная свобода"
        Tag.INNER_PEACE -> "Внутреннее спокойствие"
        Tag.OBJECTIVE_OPPORTUNITIES -> "Объективные возможности"
    }
