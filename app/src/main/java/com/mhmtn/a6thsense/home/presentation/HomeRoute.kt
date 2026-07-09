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
) {// Source code removed.}