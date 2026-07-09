package com.mhmtn.a6thsense.conversations.domain

import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversations(uid: String): Flow<List<ConversationItem>>
    fun getTotalUnreadCount(uid: String): Flow<Int>
    suspend fun deleteConversation(uid: String, conversationId: String): Result<Unit> // 👇 Yeni
}
