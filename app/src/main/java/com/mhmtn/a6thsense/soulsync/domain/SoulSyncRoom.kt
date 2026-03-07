package com.mhmtn.a6thsense.soulsync.domain

data class SoulSyncRoom(
    val matchId: String = "",
    val players: Map<String, PlayerState> = emptyMap(),
    val gameState: String = "waiting", // waiting, countdown, playing, revealing, finished
    val currentRound: Int = 1,
    val currentQuestion: String = "",
    val countdownStartTime: Long = 0,
    val createdAt: Long = 0
)
