package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

@Composable
fun HorizontalChoices(
    modifier: Modifier,
    state: DailyActivityContract.State,
    playSound: () -> Unit,
    onAction: (DailyActivityContract.Action) -> Unit
) {// Source code removed.}