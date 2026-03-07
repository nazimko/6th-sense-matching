package com.mhmtn.a6thsense.messaging.domain

import com.mhmtn.a6thsense.messaging.domain.model.Conversation
import com.mhmtn.a6thsense.messaging.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(conversationId: String, senderId: String, recipientId: String, messageText: String)
    suspend fun addReaction(conversationId: String, messageId: String, userId: String, emoji: String)
    suspend fun removeReaction(conversationId: String, messageId: String, userId: String)
    suspend fun markAsRead(conversationId: String, uid: String)
    suspend fun getMatchIdFromConversation(conversationId: String): String?
}