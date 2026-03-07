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
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("PaywallScreen")
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PaywallContract.Effect.Dismiss -> onDismiss()
                PaywallContract.Effect.SubscriptionSuccess -> onDismiss()
            }
        }
    }

    PaywallScreen(
        state = state,
        activity = activity,
        onAction = { action ->
            when (action) {
                is PaywallContract.Action.Subscribe -> {
                    activity?.let { act ->
                        viewModel.onAction(action, act)
                    }
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}