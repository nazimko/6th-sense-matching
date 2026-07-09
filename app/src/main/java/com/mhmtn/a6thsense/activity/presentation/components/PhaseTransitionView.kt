package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

@Composable
fun PhaseTransitionView(
    phase: DailyActivityContract.Phase,
    onTransitionComplete: () -> Unit
) {// Source code removed.}

@Composable
private fun PulsingText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight
) {// Source code removed.}