package com.mhmtn.a6thsense.settings.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToContactUs: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {// Source code removed.}