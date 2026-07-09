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
) {// Source code removed.}