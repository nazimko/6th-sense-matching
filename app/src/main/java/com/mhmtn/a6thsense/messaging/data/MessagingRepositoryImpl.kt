package com.mhmtn.a6thsense.messaging.data


import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mhmtn.a6thsense.messaging.domain.MessagingRepository
import com.mhmtn.a6thsense.messaging.domain.model.Conversation
import com.mhmtn.a6thsense.messaging.domain.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagingRepository {

    override fun getMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Message::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        // timestamp null ise geç
                        Log.e("MessagingRepo", "Error parsing message: ${e.message}")
                        null
                    }
                }
                    ?.sortedBy { it.timestamp ?: 0L } // 👈 Null ise 0 kabul et
                    ?: emptyList()

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    // ConversationRepositoryImpl.kt
    override suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        recipientId: String,
        messageText: String
    ) {
        val messageRef = firestore
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .document()

        val currentTime = System.currentTimeMillis()

        val message = mapOf(
            "senderId" to senderId,
            "text" to messageText,
            "timestamp" to currentTime
        )

        try {
            messageRef.set(message).await()
            // Conversation güncelle
            firestore
                .collection("conversations")
                .document(conversationId)
                .update(
                    mapOf(
                        "lastMessage" to messageText,
                        "lastMessageTimestamp" to currentTime,
                        "unreadCount.$recipientId" to FieldValue.increment(1)
                    )
                )
                .await()

        } catch (e: Exception) {
        }
    }

    override suspend fun getMatchIdFromConversation(conversationId: String): String? {
        return try {
            // 1. Conversation'dan participants'ı al
            val conversationDoc = firestore.collection("conversations")
                .document(conversationId)
                .get()
                .await()

            val participants = conversationDoc.get("participants") as? List<String>

            if (participants == null || participants.size != 2) {
                return null
            }

            // 2. Bu participants'a sahip match'i bul
            val matchSnapshot = firestore.collection("matches")
                .whereArrayContains("participants", participants[0])
                .get()
                .await()

            val matchDoc = matchSnapshot.documents.firstOrNull { doc ->
                val matchParticipants = doc.get("participants") as? List<String> ?: emptyList()
                matchParticipants.containsAll(participants)
            }

            matchDoc?.id
        } catch (e: Exception) {
            Log.e("MessagingRepo", "Error getting matchId: ${e.message}", e)
            null
        }
    }

    override suspend fun addReaction(
        conversationId: String,
        messageId: String,
        userId: String,
        emoji: String
    ) {
        firestore
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .document(messageId)
            .update("reactions.$userId", emoji)
            .await()
    }

    override suspend fun removeReaction(
        conversationId: String,
        messageId: String,
        userId: String
    ) {
        firestore
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .document(messageId)
            .update("reactions.$userId", com.google.firebase.firestore.FieldValue.delete())
            .await()
    }
    // MessagingRepositoryImpl
    override suspend fun markAsRead(conversationId: String, uid: String) {
        if (conversationId.isBlank()) return

        firestore
            .collection("conversations")
            .document(conversationId)
            .update("unreadCount.$uid", 0)
            .await()
    }
}