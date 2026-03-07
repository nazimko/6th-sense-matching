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
    onSubmitAnswer: (String) -> Unit,
    onJoinRoom: () -> Unit,
    onSaveAndExit: suspend () -> Unit,
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUid = currentUser?.uid

    // 👇 Debug log
    LaunchedEffect(state.players) {
        Log.d("SoulSyncScreen", "Current UID: $currentUid")
        Log.d("SoulSyncScreen", "Players map: ${state.players}")
        Log.d("SoulSyncScreen", "Players keys: ${state.players.keys}")
        state.players.forEach { (key, player) ->
            Log.d("SoulSyncScreen", "Player key: $key, uid: ${player.uid}, status: ${player.status}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (state.gameState) {
            GameState.WAITING -> {
                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                val otherPlayer = state.players.values.firstOrNull { it.uid != currentUid }

                Log.d("SoulSyncScreen", "Current player: $currentUser")
                Log.d("SoulSyncScreen", "Other player: $otherPlayer")


                WaitingForOpponent(
                    currentPlayer = state.players[currentUser],
                    otherPlayer = otherPlayer,
                    onJoinRoom = onJoinRoom
                )
            }

            GameState.COUNTDOWN -> {
                CountdownScreen(
                    question = state.currentQuestion,
                    countdown = state.countdown
                )
            }

            GameState.PLAYING -> {
                AnswerInputScreen(
                    question = state.currentQuestion,
                    onSubmit = { answer ->
                        onSubmitAnswer(answer)
                    }
                )
            }

            GameState.WAITING_FOR_OTHER -> {
                WaitingForOtherPlayerScreen(
                    question = state.currentQuestion,
                    myAnswer = state.myAnswer
                )
            }

            GameState.REVEALING -> {
                RevealAnswersScreen(
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
                    myScore = state.myScore,
                    theirScore = state.theirScore,
                    compatibility = state.compatibility,
                    showConfetti = showConfetti,
                    onContinue = {
                        coroutineScope.launch {
                            onSaveAndExit()
                        }
                    }
                )
            }
        }
    }
}