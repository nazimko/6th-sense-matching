package com.mhmtn.a6thsense.matchhistory.domain

import com.mhmtn.a6thsense.friends.domain.model.FriendshipStatus

data class MatchHistoryItem(
    val matchId: String = "",
    val matchedUserId: String = "",
    val matchedUserName: String = "",
    val matchedUserPhotoUrl: String = "",
    val similarityScore: Int = 0,
    val timestamp: Long = 0L,
    val conversationId: String = "",
    val isPremium: Boolean = false,
    val friendshipStatus: FriendshipStatus? = null
)
