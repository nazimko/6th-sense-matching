package com.mhmtn.a6thsense.activity.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

@Composable
fun DailyActivityScreen(
    modifier: Modifier = Modifier,
    state: DailyActivityContract.State,
    onAction: (DailyActivityContract.Action) -> Unit
) {// Source code removed.}

@Composable
private fun PhaseHeader(
    phase: DailyActivityContract.Phase,
    step: Int,
    sessionType: DailyActivityContract.SessionType,
    maxStep: Int,
    phaseTitle: String? = null,
    phaseDescription: String? = null
) {// Source code removed.}

@Composable
private fun ProgressDots(
    current: Int,
    total: Int
) {// Source code removed.}

private fun getPhaseEmoji(phase: DailyActivityContract.Phase): String {// Source code removed.}