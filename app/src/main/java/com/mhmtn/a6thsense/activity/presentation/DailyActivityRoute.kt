package com.mhmtn.a6thsense.activity.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun DailyActivityRoute(
    navController: NavController,
    sessionType: DailyActivityContract.SessionType,
    modifier: Modifier = Modifier,
    viewModel: DailyActivityViewModel = hiltViewModel()
) {
    Log.d("DailyActivityRoute", "Received sessionType: $sessionType")

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(sessionType) {
        // ViewModel zaten SavedStateHandle'dan alıyor
        // Bu LaunchedEffect sadece doğru sessionType'la çalıştığından emin olmak için
    }

    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(DailyActivityContract.Action.Enter)
        analyticsHelper.logScreenView("DailyActivityScreen")
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DailyActivityContract.Effect.NavigateToSimilarity -> {
                    navController.navigate(Routes.SIMILARITY) {
                        popUpTo(Routes.DAILY) { inclusive = true }
                    }
                }

                DailyActivityContract.Effect.ShowNoMatch -> {
                    navController.navigate(Routes.NO_MATCH) {
                        popUpTo(Routes.DAILY) { inclusive = true }
                    }
                }

                DailyActivityContract.Effect.ShowAlreadyCompleted -> {
                    navController.navigate(Routes.ALREADY_COMPLETED)
                }
                is DailyActivityContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DailyActivityScreen(
        modifier = modifier,
        state = state,
        onAction = viewModel::onAction
    )
}
