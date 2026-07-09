package com.mhmtn.a6thsense.matchhistory.presentation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun MatchHistoryRoute(
    onBackClick: () -> Unit,
    onNavigateToMessaging: (String) -> Unit,
    onNavigateToPaywall: () -> Unit,
    viewModel: MatchHistoryViewModel = hiltViewModel()
) {// Source code removed.}