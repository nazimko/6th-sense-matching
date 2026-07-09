package com.mhmtn.a6thsense.discover.presentation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun DiscoverRoute(
    isDark: Boolean,
    onNavigateToMessaging: (String) -> Unit,
    onNavigateToPaywall: () -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel()
) {// Source code removed.}