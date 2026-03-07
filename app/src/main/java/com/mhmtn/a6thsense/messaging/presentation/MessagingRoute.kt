package com.mhmtn.a6thsense.messaging.presentation

import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun MessagingRoute(
    onBackClick: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    viewModel: MessagingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showPremiumSnackbar by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("MessagingScreen")
    }


    // 👈 Effect'leri Route'ta dinle
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MessagingContract.Effect.ScrollToBottom -> {
                    coroutineScope.launch {
                        if (state.messages.isNotEmpty()) {
                            listState.animateScrollToItem(state.messages.size - 1)
                        }
                    }
                }
                MessagingContract.Effect.ShowPaywall -> {
                    showPremiumSnackbar = true
                }
                MessagingContract.Effect.NavigateBack -> {
                    onBackClick()
                }
                is MessagingContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    MessagingScreen(
        matchedUserName = state.matchedUserName, // 👈 State'ten al
        matchedUserPhotoUrl = state.matchedUserPhotoUrl, // 👈
        state = state,
        listState = listState,
        showPremiumSnackbar = showPremiumSnackbar,
        onDismissSnackbar = { showPremiumSnackbar = false },
        onUpgradeClick = {
            showPremiumSnackbar = false
            onNavigateToPaywall()
        },
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )
}
