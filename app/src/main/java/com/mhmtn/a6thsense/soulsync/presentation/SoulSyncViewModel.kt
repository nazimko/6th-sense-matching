package com.mhmtn.a6thsense.soulsync.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.soulsync.data.SoulSyncRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    
    private val cleanupScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(SoulSyncState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SoulSyncEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        Log.d("SoulSyncVM", "ViewModel init with roomId: $roomId")
        observeRoom()
    }

    override fun onCleared() {
        super.onCleared()
        cleanupScope.launch {
            try {
                repository.leaveRoom(roomId)
            } catch (e: Exception) {
                Log.e("SoulSyncVM", "Error leaving room on clear", e)
            }
        }
    }

    private fun observeRoom() {
        viewModelScope.launch {
            repository.observeRoom(roomId).collect { room ->
                if (room == null) return@collect

                val currentUid = auth.currentUser?.uid ?: return@collect
                val players = room.players
                val me = players[currentUid]
                val other = players.values.firstOrNull { it.uid != currentUid }

                // Karşı oyuncu ayrıldıysa (left) oyunu CANCELLED yap ve çık
                if (other?.status == "left" && _state.value.gameState != GameState.FINISHED) {
                    Log.d("SoulSyncVM", "Other player left, cancelling game...")
                    repository.setGameState(roomId, "cancelled")
                    _state.update { it.copy(gameState = GameState.CANCELLED) }
                    _effect.emit(SoulSyncEffect.ShowToast(UiText.StringResource(R.string.your_partner_left)))
                    delay(1500)
                    _effect.emit(SoulSyncEffect.NavigateBack)
                    return@collect
                }

                if (room.gameState == "cancelled" && _state.value.gameState != GameState.CANCELLED) {
                    _state.update { it.copy(gameState = GameState.CANCELLED) }
                    _effect.emit(SoulSyncEffect.ShowToast(UiText.StringResource(R.string.game_cancelled)))
                    delay(1000)
                    _effect.emit(SoulSyncEffect.NavigateBack)
                    return@collect
                }

                // Gelen index'i yerel kaynaklardan soruya dönüştür
                val questions = context.resources.getStringArray(R.array.profile_questions_array)
                val displayQuestion = questions.getOrNull(room.currentQuestionIndex) ?: ""

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
                        val shouldStartCountdown = _state.value.gameState != GameState.COUNTDOWN ||
                                _state.value.currentRound != room.currentRound
                        _state.update {
                            it.copy(
                                gameState = GameState.COUNTDOWN,
                                currentQuestion = displayQuestion,
                                currentRound = room.currentRound,
                                myAnswer = "",
                                theirAnswer = ""
                            )
                        }
                        if (shouldStartCountdown) startCountdown()
                    }
                    "playing" -> {
                        val currentRound = room.currentRound
                        val myAnswer = me?.answers?.get("round$currentRound")
                        val otherAnswer = other?.answers?.get("round$currentRound")
                        val iHaveAnswered = myAnswer != null && myAnswer.isNotBlank()
                        val otherHasAnswered = otherAnswer != null && otherAnswer.isNotBlank()

                        when {
                            !iHaveAnswered -> {
                                if (_state.value.gameState != GameState.PLAYING) {
                                    _state.update { it.copy(gameState = GameState.PLAYING, currentQuestion = displayQuestion, currentRound = currentRound) }
                                }
                            }
                            iHaveAnswered && !otherHasAnswered -> {
                                if (_state.value.gameState != GameState.WAITING_FOR_OTHER) {
                                    _state.update { it.copy(gameState = GameState.WAITING_FOR_OTHER, myAnswer = myAnswer ?: "") }
                                }
                            }
                            iHaveAnswered && otherHasAnswered -> {
                                if (_state.value.gameState != GameState.REVEALING) {
                                    repository.setGameState(roomId, "revealing")
                                }
                            }
                        }
                    }
                    "revealing" -> {
                        if (_state.value.gameState == GameState.REVEALING) return@collect
                        val currentRound = room.currentRound
                        val myAnswer = me?.answers?.get("round$currentRound") ?: ""
                        val theirAnswer = other?.answers?.get("round$currentRound") ?: ""
                        val isMatch = myAnswer.isNotBlank() && myAnswer.equals(theirAnswer, ignoreCase = true)
                        val isSimilar = !isMatch && myAnswer.isNotBlank() && theirAnswer.isNotBlank() &&
                                (myAnswer.contains(theirAnswer, ignoreCase = true) || theirAnswer.contains(myAnswer, ignoreCase = true))
                        val points = when { isMatch -> 20; isSimilar -> 10; else -> 0 }

                        _state.update { it.copy(gameState = GameState.REVEALING, myAnswer = myAnswer, theirAnswer = theirAnswer, answersMatch = isMatch, pointsEarned = points) }
                        if (isMatch) _effect.emit(SoulSyncEffect.TriggerConfetti)

                        viewModelScope.launch {
                            delay(4000)
                            if (me != null && other != null) {
                                repository.updateScores(roomId, me.uid, me.score + points, other.uid, other.score + points)
                                delay(1000)
                                if (currentRound < 5) repository.nextRound(roomId, currentRound + 1, context)
                                else repository.finishGame(roomId)
                            }
                        }
                    }
                    "finished" -> {
                        if (_state.value.gameState == GameState.FINISHED) return@collect
                        val myScore = me?.score ?: 0
                        val theirScore = other?.score ?: 0
                        val compatibility = ((myScore * 100) / 100).coerceIn(0, 100)
                        _state.update { it.copy(gameState = GameState.FINISHED, myScore = myScore, theirScore = theirScore, compatibility = compatibility) }
                    }
                }
                _state.update { it.copy(players = players, otherPlayerName = other?.name ?: "") }
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _state.update { it.copy(countdown = i) }
                _effect.emit(SoulSyncEffect.PlayCountdownSound)
                delay(1000)
            }
            repository.setGameStateToPlaying(roomId)
            _effect.emit(SoulSyncEffect.PlayGoSound)
        }
    }

    fun joinRoom() {
        viewModelScope.launch {
            Log.d("SoulSyncVM", "Joining room: $roomId")
            repository.joinRoom(roomId)
        }
    }

    fun submitAnswer(answer: String) {
        viewModelScope.launch {
            val currentRound = _state.value.currentRound
            repository.submitAnswer(roomId, currentRound, answer)
            _state.update { it.copy(gameState = GameState.WAITING_FOR_OTHER, myAnswer = answer) }
        }
    }

    fun saveScoresToMatch() {
        viewModelScope.launch {
            try {
                val currentUid = auth.currentUser?.uid ?: return@launch
                val finalScore = _state.value.myScore

                val matchSnapshot = firestore.collection("matches").whereArrayContains("participants", currentUid).get().await()
                val matchDoc = matchSnapshot.documents.firstOrNull()

                if (matchDoc != null) {
                    val selectionSimilarity = matchDoc.getLong("selectionSimilarity")?.toInt() ?: 0
                    val soulSyncSimilarity = (finalScore * 100) / 60
                    val finalSimilarity = ((selectionSimilarity * 0.7) + (soulSyncSimilarity * 0.3)).toInt()

                    matchDoc.reference.update(mapOf("soulSyncScore" to finalScore, "soulSyncCompleted" to true, "similarity" to finalSimilarity)).await()
                }
            } catch (e: Exception) {
                Log.e("SoulSyncVM", "Error saving scores", e)
            }
        }
    }
}
