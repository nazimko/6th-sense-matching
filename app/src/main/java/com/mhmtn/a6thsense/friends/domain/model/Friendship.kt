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
// Source code removed.
)

data class CompatibilityTestResult(
// Source code removed.
)