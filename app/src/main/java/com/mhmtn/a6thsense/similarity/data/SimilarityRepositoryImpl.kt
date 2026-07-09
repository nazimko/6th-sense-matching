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

        val photo = doc.getString("profileImageUrl") ?: doc.getString("photoUrl")

        return AuthUser(
            uid = uid,
            name = doc.getString("name") ?: "You",
            photoUrl = photo
        )
    }

    override suspend fun getMatchedUser(matchId: String): AuthUser? {
        val uid = auth.currentUser!!.uid

        // ✅ Artık matchId ile tam olarak hangi eşleşme olduğunu biliyoruz
        val matchDoc = firestore
            .collection("matches")
            .document(matchId)
            .get()
            .await()

        if (!matchDoc.exists()) return null

        val otherUid = (matchDoc.get("participants") as? List<*>)
            ?.firstOrNull { it != uid }?.toString() ?: return null

        val matchedUserDoc = firestore.collection("users").document(otherUid).get().await()
        
        val photo = matchedUserDoc.getString("profileImageUrl")
            ?: matchDoc.getString("matchedUserPhoto_$uid")
            ?: matchedUserDoc.getString("photoUrl")

        return AuthUser(
            uid = otherUid,
            name = matchDoc.getString("matchedUserName_$uid") ?: matchedUserDoc.getString("name") ?: "Unknown",
            photoUrl = photo
        )
    }

    override suspend fun getSimilarity(matchId: String): Int? {
        val matchDoc = firestore
            .collection("matches")
            .document(matchId)
            .get()
            .await()

        return if (matchDoc.exists()) matchDoc.getLong("similarity")?.toInt() ?: 0 else null
    }

    override suspend fun getSimilarityResult(matchId: String): SimilarityResult? {
        val uid = auth.currentUser!!.uid

        val matchDoc = firestore
            .collection("matches")
            .document(matchId)
            .get()
            .await()

        if (!matchDoc.exists()) return null

        val similarity = matchDoc.getLong("similarity")?.toInt() ?: return null

        val otherUid = (matchDoc.get("participants") as? List<*>)
            ?.firstOrNull { it != uid }?.toString() ?: return null

        val meDoc = firestore.collection("users").document(uid).get().await()
        val matchedDoc = firestore.collection("users").document(otherUid).get().await()

        val myPhoto = meDoc.getString("profileImageUrl") ?: meDoc.getString("photoUrl")
        val otherPhoto = matchedDoc.getString("profileImageUrl") 
            ?: matchDoc.getString("matchedUserPhoto_$uid") 
            ?: matchedDoc.getString("photoUrl")

        return SimilarityResult(
            similarity = similarity,
            currentUser = AuthUser(
                uid = uid,
                name = meDoc.getString("name") ?: "",
                photoUrl = myPhoto
            ),
            matchedUser = AuthUser(
                uid = otherUid,
                name = matchDoc.getString("matchedUserName_$uid") ?: matchedDoc.getString("name") ?: "",
                photoUrl = otherPhoto
            )
        )
    }

    override suspend fun getMatchDocument(matchId: String): DocumentSnapshot? {
        val doc = firestore
            .collection("matches")
            .document(matchId)
            .get()
            .await()
        return if (doc.exists()) doc else null
    }

    override suspend fun createConversation(
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

        val currentUserDoc = firestore.collection("users").document(currentUserId).get().await()
        val matchedUserDoc = firestore.collection("users").document(matchedUserId).get().await()

        val myPhoto = currentUserDoc.getString("profileImageUrl") ?: currentUserDoc.getString("photoUrl") ?: ""
        val otherPhoto = matchedUserDoc.getString("profileImageUrl") ?: matchedUserDoc.getString("photoUrl") ?: ""

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
                currentUserId to otherPhoto,
                matchedUserId to myPhoto
            )
        )

        conversationRef.set(conversation).await()
        return conversationRef.id
    }
}
