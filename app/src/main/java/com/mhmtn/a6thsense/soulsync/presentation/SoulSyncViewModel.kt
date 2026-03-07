package com.mhmtn.a6thsense.soulsync.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.soulsync.data.SoulSyncRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SoulSyncViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val repository: SoulSyncRepositoryImpl,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    val roomId: String = savedStateHandle["roomId"] ?: ""

    private val _state = MutableStateFlow(SoulSyncState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SoulSyncEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        Log.d("SoulSyncVM", "ViewModel init with roomId: $roomId")

        if (roomId.isBlank()) {
            Log.e("SoulSyncVM", "Room ID is blank!")
            _state.update { it.copy(gameState = GameState.WAITING) }
        }

        observeRoom()
        //analyticsHelper.logEvent(AnalyticsEvent.SoulSyncStarted())
    }

    private fun observeRoom() {
        viewModelScope.launch {
            repository.observeRoom(roomId).collect { room ->
                if (room == null) return@collect

                val currentUid = auth.currentUser?.uid ?: return@collect
                val players = room.players
                val me = players[currentUid]
                val other = players.values.firstOrNull { it.uid != currentUid }

                Log.d("SoulSyncVM", "Room update: gameState=${room.gameState}, round=${room.currentRound}")

                when (room.gameState) {
                    "waiting" -> {
                        _state.update { it.copy(gameState = GameState.WAITING) }

                        val allReady = players.values.all { it.status == "ready" }
                        if (allReady && players.size == 2) {
                            delay(1000)
                            repository.startGame(roomId, context)
                        }
                    }

                    "countdown" -> {
                        Log.d("SoulSyncVM", "Countdown state - Question: ${room.currentQuestion}, Round: ${room.currentRound}")

                        // 👇 Her countdown'da state'i güncelle (soru + round)
                        val shouldStartCountdown = _state.value.gameState != GameState.COUNTDOWN ||
                                _state.value.currentRound != room.currentRound

                        _state.update {
                            it.copy(
                                gameState = GameState.COUNTDOWN,
                                currentQuestion = room.currentQuestion, // 👈 Her seferinde güncelle
                                currentRound = room.currentRound,
                                myAnswer = "", // 👈 Önceki cevabı temizle
                                theirAnswer = "" // 👈 Önceki cevabı temizle
                            )
                        }

                        if (shouldStartCountdown) {
                            Log.d("SoulSyncVM", "Starting new countdown for round ${room.currentRound}")
                            startCountdown()
                        }
                    }

                    "playing" -> {
                        // 👇 Cevapları kontrol et
                        val currentRound = room.currentRound
                        val myAnswer = me?.answers?.get("round$currentRound")
                        val otherAnswer = other?.answers?.get("round$currentRound")

                        Log.d("SoulSyncVM", "Playing - Round: $currentRound")
                        Log.d("SoulSyncVM", "My answer exists: ${myAnswer != null}, value: $myAnswer")
                        Log.d("SoulSyncVM", "Other answer exists: ${otherAnswer != null}, value: $otherAnswer")

                        // 👇 Explicit conditions (smart cast fix)
                        val iHaveAnswered = myAnswer != null && myAnswer.isNotBlank()
                        val otherHasAnswered = otherAnswer != null && otherAnswer.isNotBlank()

                        when {
                            !iHaveAnswered -> {
                                // Ben henüz cevap vermedim → PLAYING
                                Log.d("SoulSyncVM", "I haven't answered yet → PLAYING")
                                if (_state.value.gameState != GameState.PLAYING) {
                                    _state.update {
                                        it.copy(
                                            gameState = GameState.PLAYING,
                                            currentQuestion = room.currentQuestion,
                                            currentRound = currentRound
                                        )
                                    }
                                }
                            }
                            iHaveAnswered && !otherHasAnswered -> {
                                // Ben verdim, diğeri vermedi → WAITING_FOR_OTHER
                                Log.d("SoulSyncVM", "I answered, waiting for other → WAITING_FOR_OTHER")
                                if (_state.value.gameState != GameState.WAITING_FOR_OTHER) {
                                    _state.update {
                                        it.copy(
                                            gameState = GameState.WAITING_FOR_OTHER,
                                            myAnswer = myAnswer ?: ""
                                        )
                                    }
                                }
                            }
                            iHaveAnswered && otherHasAnswered -> {
                                // İKİSİ DE verdi → REVEALING'e geç
                                Log.d("SoulSyncVM", "Both answered! → Triggering REVEALING")

                                if (_state.value.gameState != GameState.REVEALING) {
                                    Log.d("SoulSyncVM", "Calling setGameState(revealing)")
                                    repository.setGameState(roomId, "revealing")
                                } else {
                                    Log.d("SoulSyncVM", "Already in REVEALING state, skipping")
                                }
                            }
                        }
                    }

                    "revealing" -> {
                        Log.d("SoulSyncVM", "Revealing state received from DB")

                        // 👇 Zaten revealing işlemini yapıyorsak skip
                        if (_state.value.gameState == GameState.REVEALING) {
                            Log.d("SoulSyncVM", "Already processing revealing, skipping")
                            return@collect
                        }

                        val currentRound = room.currentRound
                        val myAnswer = me?.answers?.get("round$currentRound") ?: ""
                        val theirAnswer = other?.answers?.get("round$currentRound") ?: ""

                        Log.d("SoulSyncVM", "Revealing answers: mine='$myAnswer', theirs='$theirAnswer'")

                        val isMatch = myAnswer.isNotBlank() && myAnswer.equals(theirAnswer, ignoreCase = true)
                        val isSimilar = !isMatch && myAnswer.isNotBlank() && theirAnswer.isNotBlank() &&
                                (myAnswer.contains(theirAnswer, ignoreCase = true) ||
                                        theirAnswer.contains(myAnswer, ignoreCase = true))

                        val points = when {
                            isMatch -> 20
                            isSimilar -> 10
                            else -> 0
                        }

                        _state.update {
                            it.copy(
                                gameState = GameState.REVEALING,
                                myAnswer = myAnswer,
                                theirAnswer = theirAnswer,
                                answersMatch = isMatch,
                                pointsEarned = points
                            )
                        }

                        if (isMatch) {
                            _effect.emit(SoulSyncEffect.TriggerConfetti)
                        }

                        // 👇 Skorları güncelle ve sonraki tura geç
                        viewModelScope.launch {
                            delay(4000) // Revealing ekranı 4 saniye

                            if (me != null && other != null) {
                                val newMyScore = me.score + points
                                val newTheirScore = other.score + points

                                Log.d("SoulSyncVM", "Updating scores: $newMyScore, $newTheirScore")

                                repository.updateScores(
                                    roomId = roomId,
                                    player1Uid = me.uid,
                                    player1Score = newMyScore,
                                    player2Uid = other.uid,
                                    player2Score = newTheirScore
                                )

                                delay(1000)

                                if (currentRound < 3) {
                                    Log.d("SoulSyncVM", "Next round: ${currentRound + 1}")
                                    repository.nextRound(roomId, currentRound + 1, context)
                                } else {
                                    Log.d("SoulSyncVM", "Game finished!")
                                    repository.finishGame(roomId)
                                }
                            }
                        }
                    }

                    "finished" -> {
                        if (_state.value.gameState == GameState.FINISHED) {
                            return@collect
                        }

                        val myScore = me?.score ?: 0
                        val theirScore = other?.score ?: 0

                        val finalScore = myScore

                        val compatibility = ((finalScore * 100) / 60).coerceIn(0, 100)

                        _state.update {
                            it.copy(
                                gameState = GameState.FINISHED,
                                myScore = myScore,
                                theirScore = theirScore,
                                compatibility = compatibility
                            )
                        }
                        /*
                        analyticsHelper.logEvent(
                            AnalyticsEvent.SoulSyncCompleted(compatibility)
                        )

                         */
                    }
                }

                _state.update {
                    it.copy(
                        players = players,
                        otherPlayerName = other?.name ?: ""
                    )
                }
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            Log.d("SoulSyncVM", "Starting countdown from 5")

            for (i in 5 downTo 1) {
                _state.update { it.copy(countdown = i) }
                _effect.emit(SoulSyncEffect.PlayCountdownSound)
                delay(1000)
            }

            Log.d("SoulSyncVM", "Countdown finished, setting game state to 'playing' in DB")

            // 👇 Realtime DB'yi "playing"e çek
            repository.setGameStateToPlaying(roomId)

            _effect.emit(SoulSyncEffect.PlayGoSound)
        }
    }

    fun joinRoom() {
        viewModelScope.launch {
            try {
                Log.d("SoulSyncVM", "Calling joinRoom for roomId: $roomId")
                repository.joinRoom(roomId)
                Log.d("SoulSyncVM", "joinRoom completed")
            } catch (e: Exception) {
                Log.e("SoulSyncVM", "Error joining room: ${e.message}", e)
            }
        }
    }

    fun submitAnswer(answer: String) {
        viewModelScope.launch {
            try {
                val currentRound = _state.value.currentRound
                val currentUid = auth.currentUser?.uid ?: return@launch

                Log.d("SoulSyncVM", "Submitting answer: $answer for round $currentRound")

                // 1️⃣ Cevabı Realtime DB'ye yaz
                repository.submitAnswer(roomId, currentRound, answer)

                // 2️⃣ Local state'i "waiting_for_other" yap (revealing değil!)
                _state.update {
                    it.copy(
                        gameState = GameState.WAITING_FOR_OTHER, // 👈 Yeni state
                        myAnswer = answer
                    )
                }

                Log.d("SoulSyncVM", "Answer submitted, waiting for other player")

            } catch (e: Exception) {
                Log.e("SoulSyncVM", "Error submitting answer: ${e.message}", e)
            }
        }
    }

    fun saveScoresToMatch() {
        viewModelScope.launch {
            try {
                val currentUid = auth.currentUser?.uid ?: return@launch
                val finalScore = _state.value.myScore

                Log.d("SoulSyncVM", "Saving Soul Sync score: $finalScore")

                // Match document'i bul
                val matchSnapshot = firestore
                    .collection("matches")
                    .whereArrayContains("participants", currentUid)
                    .get()
                    .await()

                val matchDoc = matchSnapshot.documents.firstOrNull()

                if (matchDoc != null) {
                    // Soul Sync puanını kaydet
                    val selectionSimilarity = matchDoc.getLong("selectionSimilarity")?.toInt() ?: 0
                    val soulSyncSimilarity = (finalScore * 100) / 60

                    // Final benzerlik: %70 seçimli + %30 soul sync
                    val finalSimilarity = ((selectionSimilarity * 0.7) + (soulSyncSimilarity * 0.3)).toInt()

                    matchDoc.reference.update(
                        mapOf(
                            "soulSyncScore" to finalScore,
                            "soulSyncCompleted" to true,
                            "similarity" to finalSimilarity // 👈 Final benzerliği güncelle
                        )
                    ).await()

                    Log.d("SoulSyncVM", "Match updated - Selection: $selectionSimilarity%, Soul Sync: $soulSyncSimilarity%, Final: $finalSimilarity%")
                } else {
                    Log.e("SoulSyncVM", "Match document not found!")
                }
            } catch (e: Exception) {
                Log.e("SoulSyncVM", "Error saving scores: ${e.message}", e)
            }
        }
    }
}