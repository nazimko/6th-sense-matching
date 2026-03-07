package com.mhmtn.a6thsense.conversations.domain

data class ConversationItem(
    val conversationId: String = "",
    val otherUserId: String = "",
    val otherUserName: String = "",
    val otherUserPhotoUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val similarity: Int = 0,
    val unreadCount: Int = 0,
    val isPremium: Boolean = false
)
