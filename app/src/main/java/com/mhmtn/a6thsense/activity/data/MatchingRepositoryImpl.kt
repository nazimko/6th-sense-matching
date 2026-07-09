package com.mhmtn.a6thsense.activity.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.MatchResult
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource
import com.mhmtn.a6thsense.home.domain.TodayMatch
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MatchingRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dataSource: FirebaseSelectionDataSource,
    @ApplicationContext private val context: Context
) : MatchingRepository {

    override suspend fun saveUserSelections(selections: List<Option>) {
        val uid = auth.currentUser?.uid ?: return
        dataSource.saveSelections(uid, selections)
    }

    override fun getTodayMatches(uid: String, date: String): Flow<List<TodayMatch>> = callbackFlow {
        val listener = firestore.collection("matches")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }

                val matches = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val participants = doc.get("participants") as? List<String> ?: return@mapNotNull null
                        val otherUid = participants.firstOrNull { it != uid } ?: return@mapNotNull null
                        val timestamp = doc.getLong("timestamp") ?: 0L

                        val matchDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
                        if (matchDate != date) return@mapNotNull null

                        val sessionTypeStr = doc.getString("sessionType") ?: "INTUITION"
                        val sessionType = try {
                            DailyActivityContract.SessionType.valueOf(sessionTypeStr)
                        } catch (e: Exception) {
                            DailyActivityContract.SessionType.INTUITION
                        }

                        TodayMatch(
                            matchId = doc.id,
                            userId = otherUid,
                            userName = doc.getString("matchedUserName_$uid") ?: "Unknown",
                            userPhoto = doc.getString("matchedUserPhoto_$uid") ?: "",
                            similarity = doc.getLong("similarity")?.toInt() ?: 0,
                            sessionType = sessionType,
                            timestamp = timestamp
                        )
                    } catch (e: Exception) { null }
                }?.sortedByDescending { it.similarity } ?: emptyList()

                trySend(matches)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun completeSession(
        uid: String,
        sessionType: DailyActivityContract.SessionType,
        selections: List<String>,
        minSimilarity: Int,
        freeTextAnswers: Map<String, String>
    ): MatchResult {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val todaySession = firestore.collection("sessions")
                .whereEqualTo("uid", uid)
                .whereEqualTo("type", sessionType.name)
                .whereEqualTo("date", today)
                .get()
                .await()

            if (!todaySession.isEmpty) {
                return MatchResult.AlreadyCompleted
            }

            // 👇 KALICI GEÇMİŞ GÜNCELLEME
            firestore.collection("users").document(uid).update(
                "activityHistory", FieldValue.arrayUnion(today)
            ).await()

            val oldSessions = firestore.collection("sessions")
                .whereEqualTo("uid", uid)
                .whereEqualTo("type", sessionType.name)
                .get()
                .await()

            for (doc in oldSessions.documents) {
                doc.reference.delete().await()
            }

            // 👇 Seçenekleri yerelleştir (Display için)
            val localizedSelections = selections.map { selection ->
                try {
                    val option = Option.valueOf(selection)
                    context.getString(option.displayNameRes)
                } catch (e: Exception) {
                    selection // Option enum'da yoksa orijinal hali kalsın
                }
            }

            val sessionRef = firestore.collection("sessions").document()
            sessionRef.set(
                mapOf(
                    "uid" to uid,
                    "date" to today,
                    "selections" to localizedSelections, // Localize edilmiş liste
                    "tags" to selections,                // Orijinal Option isimleri (Eşleşme için)
                    "type" to sessionType.name,
                    "minSimilarity" to minSimilarity,
                    "freeTextAnswers" to freeTextAnswers,
                    "completedAt" to FieldValue.serverTimestamp(),
                    "timestamp" to System.currentTimeMillis(),
                    "matched" to false,
                    "matchCount" to 0
                )
            ).await()

            val isPremium = checkIfPremium(uid)
            val maxMatches = if (isPremium) 3 else 1
            // Similarity hesaplarken orijinal 'selections' (yani tags) listesini gönderiyoruz
            val matches = findAllMatches(uid, sessionRef.id, selections, freeTextAnswers, sessionType, minSimilarity,  maxMatches)

            return if (matches.isNotEmpty()) {
                matches.forEach { (matchedUid, similarity) ->
                    createMatch(uid, matchedUid, similarity, sessionType)
                }
                MatchResult.Matched
            } else {
                MatchResult.NoMatch
            }

        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error completing session: ${e.message}", e)
            throw e
        }
    }

    override suspend fun startDailySession() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("matches").document(uid).delete().await()
    }

    private fun calculateTagSimilarity(
        tags1: List<String>,
        tags2: List<String>,
        freeText1: Map<String, String>,
        freeText2: Map<String, String>
    ): Int {
        if (tags1.isEmpty() && freeText1.isEmpty()) return 0

        // ✅ DÜZELTME: İndeks bazlı (pozisyonel) karşılaştırma
        var commonTags = 0
        val tagCompareCount = minOf(tags1.size, tags2.size)
        for (i in 0 until tagCompareCount) {
            if (tags1[i] == tags2[i]) {
                commonTags++
            }
        }

        // ✅ Serbest Metin Ortaklar
        var commonFreeText = 0
        freeText1.forEach { (questionId, answer1) ->
            val answer2 = freeText2[questionId]
            if (answer2 != null) {
                val n1 = answer1.trim().lowercase(Locale.getDefault())
                val n2 = answer2.trim().lowercase(Locale.getDefault())
                if (n1 == n2 && n1.isNotEmpty()) {
                    commonFreeText++
                }
            }
        }

        // ✅ Payda Hesabı (Toplam soru sayısı)
        val totalQuestions = maxOf(tags1.size, tags2.size)
        val allFreeTextQuestionIds = (freeText1.keys + freeText2.keys).distinct()
        val totalFreeTextQuestions = allFreeTextQuestionIds.size

        val totalItems = totalQuestions + totalFreeTextQuestions
        val totalCommon = commonTags + commonFreeText

        return if (totalItems > 0) {
            (totalCommon * 100 / totalItems)
        } else {
            0
        }
    }

    private suspend fun findAllMatches(
        myUid: String,
        mySessionId: String,
        myTags: List<String>,
        freeTextAnswers: Map<String, String>,
        sessionType: DailyActivityContract.SessionType,
        minSimilarityThreshold: Int,
        maxMatches: Int
    ): List<Pair<String, Int>> {
        if (myTags.isEmpty()) return emptyList()
        val searchTags = myTags.take(30)

        val candidates = firestore.collection("sessions")
            .whereEqualTo("type", sessionType.name)
            .whereArrayContainsAny("tags", searchTags)
            .limit(100)
            .get()
            .await()

        val premiumStatusCache = mutableMapOf<String, Boolean>()

        val validMatches = candidates.documents
            .mapNotNull { doc ->
                val docUid = doc.getString("uid") ?: return@mapNotNull null
                val docId = doc.id
                if (docId == mySessionId || docUid == myUid) return@mapNotNull null

                val otherIsPremium = premiumStatusCache.getOrPut(docUid) { checkIfPremium(docUid) }
                val otherMatchCount = doc.getLong("matchCount")?.toInt() ?: 0
                val otherMaxMatches = if (otherIsPremium) 3 else 1

                if (otherMatchCount >= otherMaxMatches) return@mapNotNull null

                val otherTags = doc.get("tags") as? List<String> ?: emptyList()
                val otherFreeText = doc.get("freeTextAnswers") as? Map<String, String> ?: emptyMap()

                val otherMinThreshold = doc.getLong("minSimilarity")?.toInt() ?: 0

                val similarity = calculateTagSimilarity(myTags, otherTags, freeTextAnswers, otherFreeText)
                if (similarity < minSimilarityThreshold || similarity < otherMinThreshold) {
                    Log.d("MatchingRepo", "Eşleşme reddedildi: Benzerlik $similarity. " +
                            "Benim Eşiğim: $minSimilarityThreshold, Onun Eşiği: $otherMinThreshold")
                    return@mapNotNull null
                }
                Triple(docUid, similarity, docId)
            }
            .sortedByDescending { it.second }

        if (validMatches.isEmpty()) return emptyList()

        val selectedMatches = validMatches.take(maxMatches)
        selectedMatches.forEach { (otherUid, similarity, otherSessionId) ->
            incrementMatchCount(mySessionId, otherUid, similarity)
            incrementMatchCount(otherSessionId, myUid, similarity)
        }

        return selectedMatches.map { it.first to it.second }
    }

    private suspend fun incrementMatchCount(sessionId: String, otherUid: String, similarity: Int) {
        try {
            val newMatch = mapOf(
                "matchId" to "",
                "otherUid" to otherUid,
                "similarity" to similarity,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("sessions").document(sessionId).update(
                mapOf(
                    "matchCount" to FieldValue.increment(1),
                    "currentMatches" to FieldValue.arrayUnion(newMatch),
                    "matched" to true
                )
            ).await()
        } catch (e: Exception) { Log.e("MatchingRepo", "Error incrementing match count", e) }
    }

    private suspend fun createMatch(
        uid1: String, uid2: String, similarity: Int, sessionType: DailyActivityContract.SessionType
    ) {
        try {
            val user1Doc = firestore.collection("users").document(uid1).get().await()
            val user2Doc = firestore.collection("users").document(uid2).get().await()

            val photo1 = user1Doc.getString("profileImageUrl") ?: user1Doc.getString("photoUrl") ?: ""
            val photo2 = user2Doc.getString("profileImageUrl") ?: user2Doc.getString("photoUrl") ?: ""

            val matchRef = firestore.collection("matches").document()
            val matchData = hashMapOf(
                "participants" to listOf(uid1, uid2),
                "selectionSimilarity" to similarity,
                "soulSyncScore" to 0,
                "soulSyncCompleted" to false,
                "sessionType" to sessionType.name,
                "similarity" to similarity,
                "matchedUserName_$uid1" to (user2Doc.getString("name") ?: "Unknown"),
                "matchedUserPhoto_$uid1" to photo2,
                "matchedUserName_$uid2" to (user1Doc.getString("name") ?: "Unknown"),
                "matchedUserPhoto_$uid2" to photo1,
                "timestamp" to System.currentTimeMillis()
            )

            matchRef.set(matchData).await()
            updateMatchIdInSession(uid1, uid2, matchRef.id)
            updateMatchIdInSession(uid2, uid1, matchRef.id)
        } catch (e: Exception) { Log.e("MatchingRepo", "Error creating match", e) }
    }

    private suspend fun updateMatchIdInSession(uid: String, otherUid: String, matchId: String) {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val sessions = firestore.collection("sessions")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", today)
                .get()
                .await()

            sessions.documents.forEach { doc ->
                val currentMatches = doc.get("currentMatches") as? List<Map<String, Any>> ?: emptyList()
                val updatedMatches = currentMatches.map { match ->
                    if (match["otherUid"] == otherUid) match.toMutableMap().apply { put("matchId", matchId) }
                    else match
                }
                doc.reference.update("currentMatches", updatedMatches).await()
            }
        } catch (e: Exception) { Log.e("MatchingRepo", "Error updating matchId", e) }
    }

    private suspend fun checkIfPremium(uid: String): Boolean {
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            userDoc.getBoolean("isPremium") ?: false
        } catch (e: Exception) { false }
    }

    override suspend fun unmatch(matchId: String, conversationId: String, myUid: String): Result<Unit> {
        return try {
            firestore.collection("matches").document(matchId).delete().await()
            val conversationDoc = firestore.collection("conversations").document(conversationId).get().await()
            val participants = conversationDoc.get("participants") as? List<String>
            firestore.collection("conversations").document(conversationId).delete().await()

            participants?.let { uidList ->
                val sessionSnapshot = firestore.collection("sessions").whereIn("uid", uidList).get().await()
                for (doc in sessionSnapshot.documents) {
                    doc.reference.update("matched", false).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
