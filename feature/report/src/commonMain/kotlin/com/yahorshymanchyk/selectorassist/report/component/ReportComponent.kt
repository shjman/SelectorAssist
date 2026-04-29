package com.yahorshymanchyk.selectorassist.report.component

import com.arkivanov.decompose.value.Value
import com.yahorshymanchyk.selectorassist.report.presentation.ReportState

interface ReportComponent {
    val state: Value<ReportState>
    fun onBack()
}
