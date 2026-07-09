package com.mhmtn.a6thsense.soulsync.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.soulsync.presentation.components.AnswerInputScreen
import com.mhmtn.a6thsense.soulsync.presentation.components.CountdownScreen
import com.mhmtn.a6thsense.soulsync.presentation.components.ResultsScreen
import com.mhmtn.a6thsense.soulsync.presentation.components.RevealAnswersScreen
import com.mhmtn.a6thsense.soulsync.presentation.components.WaitingForOpponent
import com.mhmtn.a6thsense.soulsync.presentation.components.WaitingForOtherPlayerScreen
import kotlinx.coroutines.launch

@Composable
fun SoulSyncScreen(
    state: SoulSyncState,
    showConfetti: Boolean,
    isDark: Boolean,
    onSubmitAnswer: (String) -> Unit,
    onJoinRoom: () -> Unit,
    onSaveAndExit: suspend () -> Unit,
) {// Source code removed.}