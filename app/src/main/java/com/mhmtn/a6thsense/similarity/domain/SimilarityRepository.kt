package com.mhmtn.a6thsense.similarity.domain

import com.google.firebase.firestore.DocumentSnapshot
import com.mhmtn.a6thsense.auth.domain.AuthUser

interface SimilarityRepository {
    suspend fun getCurrentUser(): AuthUser
    suspend fun getMatchedUser(matchId: String): AuthUser?
    suspend fun getSimilarity(matchId: String): Int?
    suspend fun getSimilarityResult(matchId: String): SimilarityResult?
    suspend fun getMatchDocument(matchId: String): DocumentSnapshot?
    suspend fun createConversation(
        currentUserId: String,
        matchedUserId: String
    ):String
}
