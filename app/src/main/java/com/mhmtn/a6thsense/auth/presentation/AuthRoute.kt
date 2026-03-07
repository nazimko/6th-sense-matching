package com.mhmtn.a6thsense.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun AuthRoute(
    onNavigateHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }

    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("AuthScreen")
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                AuthContract.Effect.NavigateHome -> onNavigateHome()
            }
        }
    }

    AuthScreen(
        state = state,
        googleSignInClient = viewModel.googleSignInClient,
        onAction = viewModel::onAction
    )
}
