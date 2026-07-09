package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

@Composable
fun RenderIntuitionMode(
    state: DailyActivityContract.State,
    step: Int,
    onAction: (DailyActivityContract.Action) -> Unit,
    modifier: Modifier = Modifier,
    playSound: () -> Unit
) {// Source code removed.}