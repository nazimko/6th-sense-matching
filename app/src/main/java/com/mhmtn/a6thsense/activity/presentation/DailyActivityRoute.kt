package com.mhmtn.a6thsense.activity.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

@Composable
fun DailyActivityRoute(
    navController: NavController,
    sessionType: DailyActivityContract.SessionType,
    threshold: Int,
    modifier: Modifier = Modifier,
    viewModel: DailyActivityViewModel = hiltViewModel()
) {// Source code removed.}