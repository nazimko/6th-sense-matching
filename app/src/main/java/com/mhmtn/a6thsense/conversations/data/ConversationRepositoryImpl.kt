package com.mhmtn.a6thsense.conversations.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.conversations.domain.ConversationItem
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ConversationRepository {

    override fun getConversations(uid: String): Flow<List<ConversationItem>> = callbackFlow {
        val listener = firestore
            .collection("conversations")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                launch {
                    val conversations = snapshot?.documents?.mapNotNull { doc ->
                        val participants = doc.get("participants") as? List<*>
                            ?: return@mapNotNull null
                        val otherUserId = participants
                            .firstOrNull { it != uid }?.toString()
                            ?: return@mapNotNull null

                        val userNames = doc.get("userNames") as? Map<*, *>
                        val unreadMap = doc.get("unreadCount") as? Map<*, *>

                        // 👇 Karşı tarafın en güncel verilerini 'users' koleksiyonundan alıyoruz
                        val otherUserDoc = try {
                            firestore.collection("users").document(otherUserId).get().await()
                        } catch (e: Exception) { null }

                        val resolvedPhotoUrl = otherUserDoc?.getString("profileImageUrl") 
                            ?: otherUserDoc?.getString("photoUrl") 
                            ?: ""

                        val matchDoc = try {
                            firestore
                                .collection("matches")
                                .whereArrayContains("participants", uid)
                                .get()
                                .await()
                                .documents
                                .firstOrNull { matchDoc ->
                                    val matchParticipants = matchDoc.get("participants") as? List<*>
                                    matchParticipants?.contains(otherUserId) == true
                                }
                        } catch (e: Exception) { null }

                        val similarity = matchDoc?.getLong("similarity")?.toInt() ?: 0

                        ConversationItem(
                            conversationId = doc.id,
                            otherUserId = otherUserId,
                            otherUserName = otherUserDoc?.getString("name") ?: userNames?.get(uid)?.toString() ?: "User",
                            otherUserPhotoUrl = resolvedPhotoUrl, // 👈 Gerçek profil resmi URL'si
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTimestamp = doc.getLong("lastMessageTimestamp") ?: 0L,
                            unreadCount = (unreadMap?.get(uid) as? Long)?.toInt() ?: 0,
                            isPremium = otherUserDoc?.getBoolean("isPremium") ?: false,
                            similarity = similarity
                        )
                    }
                        ?.sortedByDescending { it.lastMessageTimestamp }
                        ?: emptyList()

                    trySend(conversations)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getTotalUnreadCount(uid: String): Flow<Int> = callbackFlow {
        val listener = firestore
            .collection("conversations")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ConversationRepo", "getTotalUnreadCount error: ${error.message}")
                    return@addSnapshotListener
                }

                launch {
                    var totalUnread = 0
                    snapshot?.documents?.forEach { doc ->
                        val unreadMap = doc.get("unreadCount") as? Map<*, *>
                        val count = (unreadMap?.get(uid) as? Long)?.toInt() ?: 0
                        totalUnread += count
                    }
                    trySend(totalUnread)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteConversation(uid: String, conversationId: String): Result<Unit> {
        return try {
            firestore.collection("conversations").document(conversationId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
