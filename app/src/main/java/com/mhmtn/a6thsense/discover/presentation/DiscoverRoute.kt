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
    onNavigateToMessaging: (String) -> Unit,
    onNavigateToPaywall: () -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showPremiumSnackbar by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }

    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("DiscoverScreen")
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DiscoverContract.Effect.NavigateToMessaging -> {
                    onNavigateToMessaging(
                        Routes.messagingRoute(
                            conversationId = effect.conversationId,
                            matchedUserName = effect.matchedUserName,
                            matchedUserPhotoUrl = effect.matchedUserPhotoUrl,
                            matchedUserId = effect.matchedUserId
                        )
                    )
                }
                is DiscoverContract.Effect.ShowToast -> {
                    // Toast göster
                }
                is DiscoverContract.Effect.ShowPaywall -> {
                    showPremiumSnackbar = true
                }
            }
        }
    }

    DiscoverScreen(
        state = state,
        showPremiumSnackbar = showPremiumSnackbar,
        onDismissSnackbar = { showPremiumSnackbar = false },
        onUpgradeClick = {
            showPremiumSnackbar = false
            onNavigateToPaywall()
        },
        onAction = viewModel::onAction
    )
}