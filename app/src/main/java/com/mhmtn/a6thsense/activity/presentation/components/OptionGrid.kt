package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.domain.Option

@Composable
fun OptionGrid(
    options: List<Option>,
    state: DailyActivityContract.State,
    onAction: (DailyActivityContract.Action) -> Unit,
    playSound: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}