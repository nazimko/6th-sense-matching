package com.mhmtn.a6thsense.soulsync.presentation

import com.mhmtn.a6thsense.soulsync.domain.PlayerState

data class SoulSyncState(
    val gameState: GameState = GameState.WAITING,
    val players: Map<String, PlayerState> = emptyMap(),
    val otherPlayerName: String = "",
    val currentRound: Int = 1,
    val currentQuestion: String = "",
    val countdown: Int = 5,
    val myAnswer: String = "",
    val theirAnswer: String = "",
    val answersMatch: Boolean = false,
    val pointsEarned: Int = 0,
    val myScore: Int = 0,
    val theirScore: Int = 0,
    val compatibility: Int = 0
)

enum class GameState {
    WAITING, COUNTDOWN, PLAYING, WAITING_FOR_OTHER, REVEALING, FINISHED
}

sealed class SoulSyncEffect {
    object TriggerConfetti : SoulSyncEffect()
    object PlayCountdownSound : SoulSyncEffect()
    object PlayGoSound : SoulSyncEffect()
    object NavigateBack : SoulSyncEffect()
}