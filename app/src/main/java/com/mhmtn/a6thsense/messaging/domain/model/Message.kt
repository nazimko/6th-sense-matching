package com.mhmtn.a6thsense.messaging.domain.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val reactions: Map<String, String> = emptyMap(),
    val timestamp: Long? = null,
    val isRead: Boolean = false
)