package com.mhmtn.a6thsense.discover.domain

interface DiscoverRepository {
    suspend fun getActiveUsers(currentUid: String): List<DiscoverUser>
    suspend fun getOrCreateConversation(
        currentUserId: String,
        matchedUserId: String
    ): String
    suspend fun recordSwipe(currentUid: String, swipedUid: String)
}