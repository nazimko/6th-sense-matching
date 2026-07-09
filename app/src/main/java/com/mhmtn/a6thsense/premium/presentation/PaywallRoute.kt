package com.mhmtn.a6thsense.premium.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun PaywallRoute(
    onDismiss: () -> Unit,
    isDark: Boolean,
    viewModel: PaywallViewModel = hiltViewModel()
) {// Source code removed.}