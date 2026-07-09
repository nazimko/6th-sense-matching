package com.mhmtn.a6thsense.friends.domain.model

data class Friendship(
    val id: String = "",
    val user1: String = "",
    val user2: String = "",
    val status: FriendshipStatus = FriendshipStatus.PENDING,
    val invitedBy: String = "",
    val createdAt: Long = 0L,
    val acceptedAt: Long? = null
)

enum class FriendshipStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

data class Friend(
    val uid: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val isPremium: Boolean = false,
    val hasCompletedToday: Boolean = false,
    val friendshipId: String = ""
)

data class CompatibilityTestResult(
    val testId: String = "",
    val friendName: String = "",
    val friendPhotoUrl: String = "",
    val similarity: Int = 0,
    val commonSelections: List<String> = emptyList(),
    val totalSelections: Int = 0,
    val mySelections: List<String> = emptyList(),
    val theirSelections: List<String> = emptyList(),
    val visibleFor: List<String> = emptyList(),
    val timestamp: Long = 0L
)