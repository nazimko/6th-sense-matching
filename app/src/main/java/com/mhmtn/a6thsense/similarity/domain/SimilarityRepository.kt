package com.mhmtn.a6thsense.similarity.domain

import com.google.firebase.firestore.DocumentSnapshot
import com.mhmtn.a6thsense.auth.domain.AuthUser

interface SimilarityRepository {
    suspend fun getCurrentUser(): AuthUser
    suspend fun getMatchedUser(): AuthUser?

    suspend fun getSimilarity(): Int?
    suspend fun getSimilarityResult(): SimilarityResult?
    suspend fun getMatchDocument(uid: String): DocumentSnapshot?
    suspend fun createConversation(
        currentUserId: String,
        matchedUserId: String
    ):String
}
