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
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("SettingsScreen")
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
                SettingsContract.Effect.NavigateToContactUs -> {
                    onNavigateToContactUs()
                }
                SettingsContract.Effect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )
}