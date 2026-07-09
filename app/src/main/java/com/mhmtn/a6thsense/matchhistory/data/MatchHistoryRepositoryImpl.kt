package com.mhmtn.a6thsense.matchhistory.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Filter
import com.mhmtn.a6thsense.friends.domain.model.FriendshipStatus
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryItem
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MatchHistoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MatchHistoryRepository {

    override fun getMatchHistory(uid: String, isPremium: Boolean): Flow<Pair<List<MatchHistoryItem>, Int>> =
        callbackFlow {
            Log.d("MatchHistoryRepo", "getMatchHistory called with isPremium: $isPremium")

            var currentMatchDocs = emptyList<com.google.firebase.firestore.DocumentSnapshot>()
            var currentFriendships = emptyMap<String, FriendshipStatus>()

            fun updateAndSend() {
                val matches = currentMatchDocs.mapNotNull { doc ->
                    val participants = doc.get("participants") as? List<*>
                    val otherUid = participants?.firstOrNull { it != uid } as? String

                    if (otherUid != null) {
                        MatchHistoryItem(
                            matchId = doc.id,
                            matchedUserId = otherUid,
                            matchedUserName = doc.getString("matchedUserName_$uid") ?: "Unknown",
                            matchedUserPhotoUrl = doc.getString("matchedUserPhoto_$uid") ?: "",
                            similarityScore = doc.getLong("similarity")?.toInt() ?: 0,
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            friendshipStatus = currentFriendships[otherUid]
                        )
                    } else null
                }

                val totalCount = matches.size
                val limitedMatches = if (!isPremium && matches.size > 1) {
                    matches.take(1)
                } else {
                    matches
                }

                trySend(Pair(limitedMatches, totalCount))
            }

            val matchesListener = firestore.collection("matches")
                .whereArrayContains("participants", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        currentMatchDocs = snapshot.documents
                        updateAndSend()
                    }
                }

            val friendsListener = firestore.collection("friends")
                .where(Filter.or(
                    Filter.equalTo("user1", uid),
                    Filter.equalTo("user2", uid)
                ))
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("MatchHistoryRepo", "Error listening to friendships: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        currentFriendships = snapshot.documents.associate { doc ->
                            val u1 = doc.getString("user1")
                            val u2 = doc.getString("user2")
                            val otherUid = if (u1 == uid) u2 else u1
                            val statusStr = doc.getString("status")
                            val status = try {
                                if (statusStr != null) FriendshipStatus.valueOf(statusStr) else null
                            } catch (e: Exception) {
                                null
                            }
                            (otherUid ?: "") to status
                        }.filterKeys { it.isNotEmpty() }.filterValues { it != null } as Map<String, FriendshipStatus>
                        updateAndSend()
                    }
                }

            awaitClose {
                matchesListener.remove()
                friendsListener.remove()
            }
        }


    override suspend fun getOrCreateConversation(
        currentUserId: String,
        matchedUserId: String
    ): String {
        val existing = firestore
            .collection("conversations")
            .whereArrayContains("participants", currentUserId)
            .get()
            .await()
            .documents
            .firstOrNull { doc ->
                val participants = doc.get("participants") as? List<*>
                participants?.contains(matchedUserId) == true
            }

        if (existing != null) return existing.id

        val currentUserDoc = firestore
            .collection("users")
            .document(currentUserId)
            .get()
            .await()
        val matchedUserDoc = firestore
            .collection("users")
            .document(matchedUserId)
            .get()
            .await()

        val conversationRef = firestore.collection("conversations").document()
        conversationRef.set(
            mapOf(
                "participants" to listOf(currentUserId, matchedUserId),
                "lastMessage" to "",
                "lastMessageTimestamp" to 0L,
                "createdAt" to FieldValue.serverTimestamp(),
                "userNames" to mapOf(
                    currentUserId to (matchedUserDoc.getString("name") ?: ""),
                    matchedUserId to (currentUserDoc.getString("name") ?: "")
                ),
                "userPhotos" to mapOf(
                    currentUserId to (matchedUserDoc.getString("profileImageUrl")
                        ?: matchedUserDoc.getString("photoUrl")
                        ?: ""),
                    matchedUserId to (currentUserDoc.getString("profileImageUrl")
                        ?: currentUserDoc.getString("photoUrl")
                        ?: "")
                )
            )
        ).await()

        return conversationRef.id
    }

    override suspend fun deleteMatch(matchId: String): Result<Unit> {
        return try {
            firestore.collection("matches").document(matchId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MatchHistoryRepo", "Error deleting match: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun checkFriendshipStatus(uid1: String, uid2: String): FriendshipStatus? {
        return try {
            val friendshipSnapshot = firestore.collection("friends")
                .whereIn("user1", listOf(uid1, uid2))
                .whereIn("user2", listOf(uid1, uid2))
                .get()
                .await()

            if (friendshipSnapshot.isEmpty) {
                null // Arkadaş değil
            } else {
                val doc = friendshipSnapshot.documents.first()
                when (doc.getString("status")) {
                    "ACCEPTED" -> FriendshipStatus.ACCEPTED
                    "PENDING" -> FriendshipStatus.PENDING
                    "REJECTED" -> FriendshipStatus.REJECTED
                    else -> null
                }
            }
        } catch (e: Exception) {
            Log.e("MatchHistoryRepo", "Error checking friendship: ${e.message}", e)
            null
        }
    }
}
