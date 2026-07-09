package com.mhmtn.a6thsense.matchhistory.domain

import kotlinx.coroutines.flow.Flow

interface MatchHistoryRepository {
    fun getMatchHistory(uid: String, isPremium: Boolean): Flow<Pair<List<MatchHistoryItem>, Int>>
    suspend fun getOrCreateConversation(
        currentUserId: String,
        matchedUserId: String
    ): String
    suspend fun deleteMatch(matchId: String): Result<Unit> // 👇 Yeni
}
