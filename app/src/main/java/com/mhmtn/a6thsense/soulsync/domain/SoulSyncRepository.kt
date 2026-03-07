package com.mhmtn.a6thsense.soulsync.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface SoulSyncRepository {
    suspend fun createRoom(matchId: String, player1: Player, player2: Player): String
    suspend fun joinRoom(roomId: String)
    fun observeRoom(roomId: String): Flow<SoulSyncRoom?>
    suspend fun startGame(roomId: String, context: Context)
    suspend fun submitAnswer(roomId: String, round: Int, answer: String)
    suspend fun updateScores(roomId: String, player1Uid: String, player1Score: Int, player2Uid: String, player2Score: Int)
    suspend fun nextRound(roomId: String, nextRound: Int, context: Context)
    suspend fun finishGame(roomId: String)
    suspend fun setGameState(roomId: String, gameState: String)
    suspend fun setGameStateToPlaying(roomId: String)
}