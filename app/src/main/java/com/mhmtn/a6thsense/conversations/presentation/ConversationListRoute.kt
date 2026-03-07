package com.mhmtn.a6thsense.conversations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun ConversationListRoute(
    onNavigateToMessaging: (String) -> Unit,
    viewModel: ConversationListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("ConversationListScreen")
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ConversationListContract.Effect.NavigateToMessaging -> {
                    onNavigateToMessaging(
                        Routes.messagingRoute(
                            conversationId = effect.conversationId,
                            matchedUserName = effect.matchedUserName,
                            matchedUserPhotoUrl = effect.matchedUserPhotoUrl,
                            matchedUserId = effect.matchedUserId
                        )
                    )
                }
            }
        }
    }

    ConversationListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}