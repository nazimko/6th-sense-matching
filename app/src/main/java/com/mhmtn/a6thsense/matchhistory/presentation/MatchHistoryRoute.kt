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
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }

    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("MatchHistoryScreen")
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MatchHistoryContract.Effect.NavigateToMessaging -> {
                    onNavigateToMessaging(
                        Routes.messagingRoute(
                            conversationId = effect.conversationId,
                            matchedUserName = effect.matchedUserName,
                            matchedUserPhotoUrl = effect.matchedUserPhotoUrl,
                            matchedUserId = effect.matchedUserId
                        )
                    )
                }
                MatchHistoryContract.Effect.NavigateToPaywall -> {
                    onNavigateToPaywall()
                }
                is MatchHistoryContract.Effect.ShowToast -> { // 👈 YENİ
                    Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    MatchHistoryScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )
}