package com.mhmtn.a6thsense.home.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.presentation.Routes
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    isDark: Boolean,
    onNavigateToDaily: () -> Unit,
    onNavigateToSimilarity: (String, String, String, Int) -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigatetoSession: (SessionType, Int) -> Unit,
    onNavigateToMessaging: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }

    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onAction(HomeAction.Load)
        analyticsHelper.logScreenView("HomeScreen")
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToDaily ->
                    onNavigateToDaily()

                is HomeEffect.NavigateToSimilarity ->
                    onNavigateToSimilarity(
                        effect.matchId,
                        effect.otherUserName,
                        effect.otherUserPhoto,
                        effect.similarity
                    )

                HomeEffect.NavigateToAuth ->
                    onNavigateToAuth()

                HomeEffect.NavigateToSettings ->
                    onNavigateToSettings()

                HomeEffect.NavigateToPaywall -> onNavigateToPaywall()
                is HomeEffect.NavigateToSession -> {
                    Log.d("HomeRoute", "Navigate to session: ${effect.type} with threshold: ${effect.threshold}")
                    onNavigatetoSession(effect.type, effect.threshold)
                }
            }
        }
    }

    HomeScreen(
        state = state,
        isDark = isDark,
        showSheet = showSheet,
        onDismiss = { showSheet = false },
        onShowSheet = { showSheet = true },
        onAction = viewModel::onAction
    )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsEntryPoint {
    fun analyticsHelper(): AnalyticsHelper
}
