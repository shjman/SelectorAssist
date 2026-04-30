@file:Suppress("MagicNumber")

package com.yahorshymanchyk.selectorassist.report.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yahorshymanchyk.selectorassist.domain.model.Pole
import com.yahorshymanchyk.selectorassist.report.component.ReportComponent
import selectorassist.feature.report.generated.resources.Res
import selectorassist.feature.report.generated.resources.report_header_stats
import selectorassist.feature.report.generated.resources.report_healthy_group_label
import selectorassist.feature.report.generated.resources.report_healthy_verb
import selectorassist.feature.report.generated.resources.report_noise_group_label
import selectorassist.feature.report.generated.resources.report_noise_verb
import selectorassist.feature.report.generated.resources.report_section_arguments
import selectorassist.feature.report.generated.resources.report_section_influence
import selectorassist.feature.report.generated.resources.report_section_tendency
import com.yahorshymanchyk.selectorassist.report.presentation.ReportState
import com.yahorshymanchyk.selectorassist.ui.components.BackButton
import com.yahorshymanchyk.selectorassist.ui.theme.AppColors
import org.jetbrains.compose.resources.stringResource

private val CardShape = RoundedCornerShape(16.dp)
private val ArgumentShape = RoundedCornerShape(12.dp)

@Composable
fun ReportScreen(component: ReportComponent) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        BackButton(onBack = component::onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
        ) {
            item { HeaderCard(state) }
            item { Spacer(Modifier.height(12.dp)) }

            val hasNoiseInfluence = state.noiseInfluencePole != null
            val hasHealthyInfluence = state.healthyInfluencePole != null
            if (!state.isLoading && (hasNoiseInfluence || hasHealthyInfluence)) {
                item { InfluenceCard(state) }
                item { Spacer(Modifier.height(12.dp)) }
            }

            if (!state.isLoading && (state.poleAArguments.isNotEmpty() || state.poleBArguments.isNotEmpty())) {
                item { ArgumentsSection(state) }
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    BackButton(
        onClick = onBack,
        tint = AppColors.TextSecondary,
        modifier = Modifier.padding(start = 8.dp, top = 56.dp, bottom = 4.dp),
    )
}

@Composable
private fun HeaderCard(state: ReportState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(AppColors.Surface)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = state.questionTitle,
            color = AppColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.report_header_stats, state.totalDays, state.totalEntries),
            color = AppColors.TextSecondary,
            fontSize = 13.sp,
        )

        if (!state.isLoading) {
            Spacer(Modifier.height(20.dp))
            SectionLabel(stringResource(Res.string.report_section_tendency))
            Spacer(Modifier.height(12.dp))
            TendencyRow(
                label = state.poleA,
                percent = state.poleATendencyPercent,
                color = AppColors.PoleA,
            )
            Spacer(Modifier.height(10.dp))
            TendencyRow(
                label = state.poleB,
                percent = state.poleBTendencyPercent,
                color = AppColors.PoleB,
            )
        }
    }
}

@Composable
private fun TendencyRow(label: String, percent: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .weight(2f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AppColors.ProgressTrack),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = percent / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
        Text(
            text = "$percent%",
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

@Composable
private fun InfluenceCard(state: ReportState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(AppColors.Surface)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionLabel(stringResource(Res.string.report_section_influence))
        Spacer(Modifier.height(2.dp))

        state.noiseInfluencePole?.let { pole ->
            InfluenceLine(
                groupLabel = stringResource(Res.string.report_noise_group_label),
                groupColor = AppColors.TagGroupNoise,
                verb = stringResource(Res.string.report_noise_verb),
                poleName = if (pole == Pole.A) state.poleA else state.poleB,
                poleColor = if (pole == Pole.A) AppColors.PoleA else AppColors.PoleB,
            )
        }
        state.healthyInfluencePole?.let { pole ->
            InfluenceLine(
                groupLabel = stringResource(Res.string.report_healthy_group_label),
                groupColor = AppColors.TagGroupHealthy,
                verb = stringResource(Res.string.report_healthy_verb),
                poleName = if (pole == Pole.A) state.poleA else state.poleB,
                poleColor = if (pole == Pole.A) AppColors.PoleA else AppColors.PoleB,
            )
        }
    }
}

@Composable
private fun InfluenceLine(
    groupLabel: String,
    groupColor: Color,
    verb: String,
    poleName: String,
    poleColor: Color,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = groupColor, fontWeight = FontWeight.Bold)) {
                append(groupLabel)
            }
            withStyle(SpanStyle(color = AppColors.TextPrimary)) {
                append(" $verb ")
            }
            withStyle(SpanStyle(color = poleColor, fontWeight = FontWeight.Bold)) {
                append(poleName)
            }
        },
        fontSize = 15.sp,
    )
}

@Composable
private fun ArgumentsSection(state: ReportState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(Res.string.report_section_arguments))
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = state.poleA,
                color = AppColors.PoleA,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = state.poleB,
                color = AppColors.PoleB,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(8.dp))

        val maxRows = maxOf(state.poleAArguments.size, state.poleBArguments.size)
        repeat(maxRows) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ArgumentCard(
                    text = state.poleAArguments.getOrNull(index),
                    modifier = Modifier.weight(1f),
                )
                ArgumentCard(
                    text = state.poleBArguments.getOrNull(index),
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ArgumentCard(text: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(ArgumentShape)
            .background(if (text != null) AppColors.Surface else AppColors.Background),
    ) {
        if (text != null) {
            Text(
                text = text,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = AppColors.TextTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp,
    )
}
