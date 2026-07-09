package com.mhmtn.a6thsense.messaging.presentation

import android.media.SoundPool
import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun MessagingRoute(
    onBackClick: () -> Unit,
    isDark: Boolean,
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

    val soundPool = remember { SoundPool.Builder().setMaxStreams(1).build() }
    val incomingSoundId = remember { soundPool.load(context, R.raw.received, 1) }
    DisposableEffect(Unit) { onDispose { soundPool.release() } }

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

                is MessagingContract.Effect.PlayIncomingMessageSound -> {
                    soundPool.play(incomingSoundId, 0.4f, 0.4f, 0, 0, 1f)
                }

                is MessagingContract.Effect.ShowError -> {
                    Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                }
                MessagingContract.Effect.ShowPaywall -> {
                    showPremiumSnackbar = true
                }
                MessagingContract.Effect.NavigateBack -> {
                    onBackClick()
                }
                is MessagingContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    MessagingScreen(
        matchedUserName = state.matchedUserName, // 👈 State'ten al
        matchedUserPhotoUrl = state.matchedUserPhotoUrl, // 👈
        state = state,
        isDark = isDark,
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
