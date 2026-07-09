package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

@Composable
fun RenderPreferenceMode(
    state: DailyActivityContract.State,
    step: Int,
    onAction: (DailyActivityContract.Action) -> Unit,
    playSound: () -> Unit
) {// Source code removed.}