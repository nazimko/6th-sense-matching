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
    viewModel: SoulSyncViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showConfetti by remember { mutableStateOf(false) }

    val countdownSound = remember {
        MediaPlayer.create(context, R.raw.drop) // 👈 Ses dosyası eklemen gerekecek
    }
    val goSound = remember {
        MediaPlayer.create(context, R.raw.drop) // 👈 Ses dosyası eklemen gerekecek
    }

    LaunchedEffect(Unit) {
        if (viewModel.roomId.isBlank()) {
            Log.e("SoulSyncRoute", "Room ID is blank! Exiting...")
            Toast.makeText(context, "Oda bulunamadı", Toast.LENGTH_SHORT).show()
            onExit()
            return@LaunchedEffect
        }

        Log.d("SoulSyncRoute", "Soul Sync started with room: ${viewModel.roomId}")
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SoulSyncEffect.NavigateBack -> {
                    onExit()
                }
                SoulSyncEffect.TriggerConfetti -> {
                    showConfetti = true
                    // 3 saniye sonra kapat
                    kotlinx.coroutines.delay(3000)
                    showConfetti = false
                }
                SoulSyncEffect.PlayCountdownSound -> {
                    countdownSound.apply {
                        if (isPlaying) {
                            seekTo(0)
                        } else {
                            start()
                        }
                    }
                }
                SoulSyncEffect.PlayGoSound -> {
                    goSound.start()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            countdownSound.release()
            goSound.release()
        }
    }

    SoulSyncScreen(
        state = state,
        showConfetti = showConfetti,
        onJoinRoom = { viewModel.joinRoom() },
        onSubmitAnswer = { answer -> viewModel.submitAnswer(answer) },
        onSaveAndExit = {
            viewModel.saveScoresToMatch()
            delay(500)
            onExit()
        }
    )
}