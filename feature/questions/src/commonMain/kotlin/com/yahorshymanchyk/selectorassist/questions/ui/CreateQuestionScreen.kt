package com.yahorshymanchyk.selectorassist.questions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.questions.component.CreateQuestionComponent
import com.yahorshymanchyk.selectorassist.questions.presentation.CreateQuestionIntent
import com.yahorshymanchyk.selectorassist.ui.components.BackButton
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import com.yahorshymanchyk.selectorassist.ui.theme.isAndroid
import androidx.compose.material3.Text

private const val PRESET_DURATION_SHORT = 7
private const val PRESET_DURATION_OPTIMAL = 30
private const val PRESET_DURATION_LONG = 100
private val PRESET_DURATIONS = listOf(PRESET_DURATION_SHORT, PRESET_DURATION_OPTIMAL, PRESET_DURATION_LONG)
private val FieldShape = RoundedCornerShape(12.dp)
private const val BUTTON_HEIGHT = 72

@Composable
fun CreateQuestionScreen(component: CreateQuestionComponent) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        ScreenHeader(onBack = component::onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            FormCard {
                TitleSection(
                    title = state.title,
                    onTitleChange = { component.onIntent(CreateQuestionIntent.UpdateTitle(it)) },
                )
                SectionDivider()
                PolesSection(
                    poleA = state.poleA,
                    poleB = state.poleB,
                    onPoleAChange = { component.onIntent(CreateQuestionIntent.UpdatePoleA(it)) },
                    onPoleBChange = { component.onIntent(CreateQuestionIntent.UpdatePoleB(it)) },
                )
                SectionDivider()
                DurationSection(
                    durationDays = state.durationDays,
                    onDurationChange = { component.onIntent(CreateQuestionIntent.SetDuration(it)) },
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        SubmitButton(
            canSubmit = state.canSubmit,
            onClick = { component.onIntent(CreateQuestionIntent.Submit) },
        )
    }
}

@Composable
private fun ScreenHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isAndroid) 4.dp else 8.dp, end = 20.dp, top = 56.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackButton(onClick = onBack)
        Text(
            text = "Новый вопрос",
            color = AppColors.TextPrimary,
            fontSize = if (isAndroid) 22.sp else 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun FormCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        content()
    }
}

@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(1.dp)
            .background(AppColors.Divider),
    )
}

@Composable
private fun TitleSection(title: String, onTitleChange: (String) -> Unit) {
    SectionLabel(text = "Ваша дилемма")
    Spacer(Modifier.height(10.dp))
    FormTextField(
        value = title,
        onValueChange = onTitleChange,
        placeholder = "Уволиться или остаться?",
        minHeight = 96.dp,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next,
        ),
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Сформулируйте вопрос так, как он звучит у вас внутри",
        color = AppColors.TextSecondary,
        fontSize = 12.sp,
    )
}

@Composable
private fun PolesSection(
    poleA: String,
    poleB: String,
    onPoleAChange: (String) -> Unit,
    onPoleBChange: (String) -> Unit,
) {
    SectionLabel(text = "Два полюса")
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Первый вариант", color = AppColors.TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            FormTextField(
                value = poleA,
                onValueChange = onPoleAChange,
                placeholder = "Уйти",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Второй вариант", color = AppColors.TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            FormTextField(
                value = poleB,
                onValueChange = onPoleBChange,
                placeholder = "Остаться",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
            )
        }
    }
}

@Composable
private fun DurationSection(durationDays: Int, onDurationChange: (Int) -> Unit) {
    SectionLabel(text = "Срок наблюдения")
    Spacer(Modifier.height(10.dp))
    DurationStepper(
        days = durationDays,
        onDecrement = { onDurationChange(durationDays - 1) },
        onIncrement = { onDurationChange(durationDays + 1) },
    )
    Spacer(Modifier.height(10.dp))
    DurationPresets(selected = durationDays, onSelect = onDurationChange)
    if (durationDays == PRESET_DURATION_OPTIMAL) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Оптимально для большинства дилемм",
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun DurationStepper(days: Int, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.InputField)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "−",
            color = AppColors.TextPrimary,
            fontSize = 22.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onDecrement)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$days",
                color = AppColors.TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "дн",
                color = AppColors.TextSecondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        Text(
            text = "+",
            color = AppColors.TextPrimary,
            fontSize = 22.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onIncrement)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun DurationPresets(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PRESET_DURATIONS.forEach { days ->
            val isSelected = selected == days
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AppColors.InputFieldSelected else AppColors.InputField)
                    .then(
                        if (isSelected) Modifier.border(1.dp, AppColors.InputFieldSelectedBorder, RoundedCornerShape(10.dp))
                        else Modifier
                    )
                    .clickable { onSelect(days) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$days дн",
                    color = if (isSelected) AppColors.TextPrimary else AppColors.TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, color = AppColors.TextSecondary, fontSize = 13.sp)
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minHeight: androidx.compose.ui.unit.Dp = 48.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val textStyle = remember {
        TextStyle(color = AppColors.TextPrimary, fontSize = 17.sp)
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(FieldShape)
            .background(AppColors.InputField)
            .padding(horizontal = 14.dp, vertical = 14.dp)
            .then(if (!singleLine) Modifier.height(minHeight) else Modifier),
        textStyle = textStyle,
        cursorBrush = SolidColor(AppColors.TextPrimary),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(text = placeholder, color = AppColors.TextTertiary, fontSize = 17.sp)
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun SubmitButton(canSubmit: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT.dp)
            .background(AppColors.Surface)
            .clickable(enabled = canSubmit, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "начать наблюдение",
            color = if (canSubmit) AppColors.TextPrimary else AppColors.TextTertiary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
