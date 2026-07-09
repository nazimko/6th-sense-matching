package com.mhmtn.a6thsense.soulsync.presentation

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.R
import kotlinx.coroutines.delay

@Composable
fun SoulSyncRoute(
    onExit: () -> Unit,
    isDark: Boolean,
    viewModel: SoulSyncViewModel = hiltViewModel()
) {// Source code removed.}