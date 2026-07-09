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
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUid = currentUser?.uid

    Box(modifier = Modifier.fillMaxSize()) {
        when (state.gameState) {
            GameState.WAITING -> {
                val otherPlayer = state.players.values.firstOrNull { it.uid != currentUid }

                WaitingForOpponent(
                    isDark = isDark,
                    currentPlayer = state.players[currentUid ?: ""],
                    otherPlayer = otherPlayer,
                    onJoinRoom = onJoinRoom
                )
            }

            GameState.COUNTDOWN -> {
                CountdownScreen(
                    isDark = isDark,
                    question = state.currentQuestion,
                    countdown = state.countdown
                )
            }

            GameState.PLAYING -> {
                AnswerInputScreen(
                    question = state.currentQuestion,
                    isDark = isDark,
                    onSubmit = { answer ->
                        onSubmitAnswer(answer)
                    }
                )
            }

            GameState.WAITING_FOR_OTHER -> {
                WaitingForOtherPlayerScreen(
                    isDark = isDark,
                    question = state.currentQuestion,
                    myAnswer = state.myAnswer
                )
            }

            GameState.REVEALING -> {
                RevealAnswersScreen(
                    isDark = isDark,
                    question = state.currentQuestion,
                    myAnswer = state.myAnswer,
                    theirAnswer = state.theirAnswer,
                    isMatch = state.answersMatch,
                    pointsEarned = state.pointsEarned,
                    showConfetti = showConfetti
                )
            }

            GameState.FINISHED -> {
                val coroutineScope = rememberCoroutineScope()
                ResultsScreen(
                    isDark = isDark,
                    myScore = state.myScore,
                    theirScore = state.theirScore,
                    otherPlayerName = state.otherPlayerName, // 👈 Added
                    compatibility = state.compatibility,
                    showConfetti = showConfetti,
                    onContinue = {
                        coroutineScope.launch {
                            onSaveAndExit()
                        }
                    }
                )
            }

            GameState.CANCELLED -> {

            }
        }
    }
}
