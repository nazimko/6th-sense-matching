package com.mhmtn.a6thsense.discover.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mhmtn.a6thsense.discover.domain.DiscoverRepository
import com.mhmtn.a6thsense.discover.domain.DiscoverUser
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DiscoverRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DiscoverRepository {

    override suspend fun getActiveUsers(currentUid: String): List<DiscoverUser> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Bugün aktivite yapan kullanıcılar
        val todaySessions = firestore
            .collection("sessions")
            .whereEqualTo("date", today)
            .get()
            .await()

        val activeUids = todaySessions.documents
            .mapNotNull { it.getString("uid") }
            .filter { it != currentUid }
            .distinct()

        if (activeUids.isEmpty()) return emptyList()

        val swipedToday = getSwipedUsersToday(currentUid)

        // Mevcut matchleri al
        val existingMatches = firestore
            .collection("matches")
            .whereArrayContains("participants", currentUid)
            .get()
            .await()
            .documents
            .flatMap { doc ->
                (doc.get("participants") as? List<*>)
                    ?.filterNot { it == currentUid }
                    ?.mapNotNull { it?.toString() }
                    ?: emptyList()
            }
            .toSet()

        val filteredUids = activeUids.filterNot {
            it in existingMatches || it in swipedToday
        }

        // Kullanıcı bilgilerini çek
        val users = filteredUids.mapNotNull { uid ->
            val userDoc = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            if (!userDoc.exists()) return@mapNotNull null

            val showInDiscover = userDoc.getBoolean("showInDiscover")
            if (showInDiscover == false) {
                return@mapNotNull null // Bu kullanıcıyı atla
            }
            // Similarity hesapla (basit versiyon)
            val currentUserSession = todaySessions.documents
                .firstOrNull { it.getString("uid") == currentUid }
            val otherUserSession = todaySessions.documents
                .firstOrNull { it.getString("uid") == uid }

            val similarity = if (currentUserSession != null && otherUserSession != null) {
                val currentSelections = currentUserSession
                    .get("selections") as? List<*> ?: emptyList<Any>()
                val otherSelections = otherUserSession
                    .get("selections") as? List<*> ?: emptyList<Any>()
                val common = currentSelections.intersect(otherSelections.toSet()).size
                val total = maxOf(currentSelections.size, otherSelections.size)
                if (total > 0) (common * 100 / total) else 0
            } else 0

            DiscoverUser(
                uid = uid,
                name = userDoc.getString("name") ?: "User",
                photoUrl = userDoc.getString("photoUrl") ?: "",
                similarityScore = similarity,
                isMatched = existingMatches.contains(uid),
                lastActiveDate = today,
                isPremium = userDoc.getBoolean("isPremium") ?: false
            )
        }
            // Uyum skoruna göre sırala
            .sortedByDescending { it.similarityScore }

        return users
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
                    currentUserId to (matchedUserDoc.getString("photoUrl") ?: ""),
                    matchedUserId to (currentUserDoc.getString("photoUrl") ?: "")
                )
            )
        ).await()

        return conversationRef.id
    }

    private suspend fun getSwipedUsersToday(currentUid: String): Set<String> {
        // DataStore'da "swiped_users_{date}" anahtarında sakla
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return try {
            val swipedDoc = firestore
                .collection("swipe_history")
                .document("${currentUid}_$today")
                .get()
                .await()

            (swipedDoc.get("swipedUsers") as? List<*>)
                ?.mapNotNull { it?.toString() }
                ?.toSet()
                ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    // 👇 Swipe yapılınca kaydet
    override suspend fun recordSwipe(currentUid: String, swipedUid: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        firestore
            .collection("swipe_history")
            .document("${currentUid}_$today")
            .set(
                mapOf(
                    "swipedUsers" to FieldValue.arrayUnion(swipedUid),
                    "date" to today
                ),
                SetOptions.merge()
            )
            .await()
    }
}