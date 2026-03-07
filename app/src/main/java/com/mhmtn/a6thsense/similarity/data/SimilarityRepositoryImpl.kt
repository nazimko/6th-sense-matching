package com.mhmtn.a6thsense.similarity.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.similarity.domain.SimilarityRepository
import com.mhmtn.a6thsense.similarity.domain.SimilarityResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SimilarityRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SimilarityRepository {

    override suspend fun getCurrentUser(): AuthUser {
        val uid = auth.currentUser!!.uid
        val doc = firestore.collection("users").document(uid).get().await()

        return AuthUser(
            uid = uid,
            name = doc.getString("name") ?: "You",
            photoUrl = doc.getString("photoUrl")
        )
    }

    override suspend fun getMatchedUser(): AuthUser? {
        val uid = auth.currentUser!!.uid

        val matchDoc = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return null

        val otherUid = (matchDoc.get("participants") as? List<*>)
            ?.firstOrNull { it != uid }?.toString() ?: return null

        return AuthUser(
            uid = otherUid,
            name = matchDoc.getString("matchedUserName_$uid") ?: "Unknown",
            photoUrl = matchDoc.getString("matchedUserPhoto_$uid")
        )
    }

    override suspend fun getSimilarity(): Int? {
        val uid = auth.currentUser!!.uid

        // 👇 Aynı şekilde güncellendi
        val matchDoc = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return null

        return matchDoc.getLong("similarity")?.toInt() ?: 0
    }

    override suspend fun getSimilarityResult(): SimilarityResult? {
        val uid = auth.currentUser!!.uid

        // 👇 participants array'e göre ara
        val matchDoc = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return null

        val similarity = matchDoc.getLong("similarity")?.toInt() ?: return null

        val otherUid = (matchDoc.get("participants") as? List<*>)
            ?.firstOrNull { it != uid }?.toString() ?: return null

        val meDoc = firestore.collection("users").document(uid).get().await()
        val matchedDoc = firestore.collection("users").document(otherUid).get().await()

        return SimilarityResult(
            similarity = similarity,
            currentUser = AuthUser(
                uid = uid,
                name = meDoc.getString("name") ?: "",
                photoUrl = meDoc.getString("photoUrl")
            ),
            matchedUser = AuthUser(
                uid = otherUid,
                name = matchDoc.getString("matchedUserName_$uid") ?: "",
                photoUrl = matchDoc.getString("matchedUserPhoto_$uid")
            )
        )
    }

    override suspend fun getMatchDocument(uid: String): DocumentSnapshot? {
        return firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()
            .documents
            .firstOrNull()
    }

    override suspend fun createConversation(
        currentUserId: String,
        matchedUserId: String
    ): String {
        // 👇 Önce bu iki kullanıcı arasında conversation var mı kontrol et
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

        // Varsa mevcut id'yi döndür
        if (existing != null) return existing.id

        val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
        val matchedUserDoc = firestore.collection("users").document(matchedUserId).get().await()

        // Yoksa yeni oluştur
        val conversationRef = firestore.collection("conversations").document()
        val conversation = mapOf(
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

        conversationRef.set(conversation).await()
        return conversationRef.id
    }
}
