package com.mhmtn.a6thsense.conversations.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mhmtn.a6thsense.conversations.domain.ConversationItem
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
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
                        val userPhotos = doc.get("userPhotos") as? Map<*, *>
                        val unreadMap = doc.get("unreadCount") as? Map<*, *>

                        // 👇 Artık await() çalışır
                        val otherUserDoc = try {
                            firestore
                                .collection("users")
                                .document(otherUserId)
                                .get()
                                .await()
                        } catch (e: Exception) {
                            null
                        }

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
                        } catch (e: Exception) {
                            null
                        }

                        val similarity = matchDoc?.getLong("similarity")?.toInt() ?: 0

                        ConversationItem(
                            conversationId = doc.id,
                            otherUserId = otherUserId,
                            otherUserName = userNames?.get(uid)?.toString() ?: "User",
                            otherUserPhotoUrl = userPhotos?.get(uid)?.toString() ?: "",
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

    // ConversationRepositoryImpl.kt
    override fun getTotalUnreadCount(uid: String): Flow<Int> = callbackFlow {
        val listener = firestore
            .collection("conversations")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ConversationRepo", "getTotalUnreadCount error: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                launch {
                    var totalUnread = 0

                    snapshot?.documents?.forEach { doc ->
                        val unreadMap = doc.get("unreadCount") as? Map<*, *>
                        val count = (unreadMap?.get(uid) as? Long)?.toInt() ?: 0
                        Log.d("ConversationRepo", "Conversation ${doc.id}: unread=$count") // 👈
                        totalUnread += count
                    }
                    Log.d("ConversationRepo", "Total unread: $totalUnread") // 👈
                    trySend(totalUnread)
                }
            }

        awaitClose { listener.remove() }
    }
}