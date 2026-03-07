package com.mhmtn.a6thsense.messaging.domain.model

data class Conversation(
    val id: String = "",
    val matchedUserId: String = "",
    val matchedUserName: String = "",
    val matchedUserPhotoUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0
)
