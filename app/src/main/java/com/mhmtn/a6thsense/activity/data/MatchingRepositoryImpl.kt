package com.mhmtn.a6thsense.activity.data

import android.system.Os.close
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.MatchResult
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource
import com.mhmtn.a6thsense.home.domain.TodayMatch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MatchingRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dataSource: FirebaseSelectionDataSource
) : MatchingRepository {

    override suspend fun saveUserSelections(selections: List<Option>) {
        val uid = auth.currentUser?.uid ?: return
        dataSource.saveSelections(uid, selections)
    }

    override fun getTodayMatches(uid: String, date: String): Flow<List<TodayMatch>> = callbackFlow {

        Log.d("MatchingRepo", "=== getTodayMatches Started ===")
        Log.d("MatchingRepo", "uid: $uid, date: $date")

        val listener = firestore.collection("matches")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                Log.d("MatchingRepo", "Snapshot received: ${snapshot?.documents?.size} matches")


                val matches = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val participants = doc.get("participants") as? List<String> ?: return@mapNotNull null
                        val otherUid = participants.firstOrNull { it != uid } ?: return@mapNotNull null
                        val timestamp = doc.getLong("timestamp") ?: 0L

                        // Bugünkü match mi kontrol et
                        val matchDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(timestamp))

                        if (matchDate != date) {
                            Log.d("MatchingRepo", "Skipping: not today's match")
                            return@mapNotNull null
                        }

                        val sessionTypeStr = doc.getString("sessionType") ?: "INTUITION"
                        val sessionType = try {
                            DailyActivityContract.SessionType.valueOf(sessionTypeStr)
                        } catch (e: Exception) {
                            DailyActivityContract.SessionType.INTUITION
                        }

                        val match = TodayMatch(
                            matchId = doc.id,
                            userId = otherUid,
                            userName = doc.getString("matchedUserName_$uid") ?: "Unknown",
                            userPhoto = doc.getString("matchedUserPhoto_$uid") ?: "",
                            similarity = doc.getLong("similarity")?.toInt() ?: 0,
                            sessionType = sessionType,
                            timestamp = timestamp
                        )
                        Log.d("MatchingRepo", "Added match: ${match.userName}, similarity=${match.similarity}%")
                        match
                    } catch (e: Exception) {
                        Log.e("MatchingRepo", "Error parsing match: ${e.message}", e)
                        null
                    }
                }?.sortedByDescending { it.similarity } ?: emptyList()
                Log.d("MatchingRepo", "Sending ${matches.size} matches to flow")

                trySend(matches)
            }

        awaitClose {
            Log.d("MatchingRepo", "Flow closed")
            listener.remove() }
    }

    override suspend fun completeSession(
        uid: String,
        sessionType: DailyActivityContract.SessionType,
        selections: List<String>,
        freeTextAnswers: Map<String, String>
    ): MatchResult {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date())

            val existingSession = firestore
                .collection("sessions")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", today)
                .whereEqualTo("type", sessionType.name)
                .get()
                .await()

            if (!existingSession.isEmpty) {
                return MatchResult.AlreadyCompleted
            }

            val tags = selections

            // 👇 document(uid) yerine yeni document - her aktivite ayrı kayıt
            val sessionRef = firestore.collection("sessions").document()

            sessionRef.set(
                mapOf(
                    "uid" to uid, // 👈 uid field olarak eklendi
                    "date" to today,
                    "selections" to selections,
                    "tags" to tags,
                    "type" to sessionType,
                    "freeTextAnswers" to freeTextAnswers,
                    "completedAt" to FieldValue.serverTimestamp(),
                    "timestamp" to System.currentTimeMillis(), // 👈 streak için
                    "matched" to false
                )
            ).await()

            val isPremium = checkIfPremium(uid)
            val maxMatches = if (isPremium) 3 else 1
            val matches = findAllMatches(uid, sessionRef.id, tags, sessionType, maxMatches)

            if (matches.isNotEmpty()) {
                Log.d("MatchingRepo", "Found ${matches.size} matches")

                matches.forEach { (matchedUid, similarity) ->
                    createMatch(uid, matchedUid, similarity, sessionType)
                }

                return MatchResult.Matched
            }
            Log.d("MatchingRepo", "No match found")
            return MatchResult.NoMatch

        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error completing session: ${e.message}", e)
            throw e
        }
    }
    /*
            val bestMatch = findBestMatch(uid, sessionRef.id, tags, sessionType)

            if (bestMatch != null) {
                val (matchedUid, similarity) = bestMatch
                Log.d("MatchingRepo", "Match found: $matchedUid with $similarity% similarity")

                // Match oluştur
                createMatch(uid, matchedUid, similarity)

                return MatchResult.Matched
            } else {
                Log.d("MatchingRepo", "No match found (minimum 60% required)")
                return MatchResult.NoMatch
            }

        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error completing session: ${e.message}", e)
            throw e
        }
    }
*/

    override suspend fun startDailySession() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("matches")
            .document(uid)
            .delete()
            .await()
    }

    private suspend fun findBestMatch(
        myUid: String,
        mySessionId: String,
        myTags: List<String>,
        sessionType: DailyActivityContract.SessionType
    ): Pair<String, Int>? {

        Log.d("MatchingRepo", "Finding best match for user: $myUid")
        Log.d("MatchingRepo", "My tags: $myTags")

        // 1️⃣ İlk 10 tag ile aday session'ları bul (Firestore limit)
        val searchTags = myTags.take(10)

        if (searchTags.isEmpty()) {
            Log.w("MatchingRepo", "No tags to search with")
            return null
        }

        val startTime = System.currentTimeMillis()

        val candidatesQuery = firestore.collection("sessions")
            .whereEqualTo("matched", false)
            .whereEqualTo("type", sessionType.name)
            .whereArrayContainsAny("tags", searchTags) // 👈 INDEX kullanır
            .limit(100) // Max 100 aday

        val candidates = candidatesQuery.get().await()

        val queryTime = System.currentTimeMillis() - startTime
        Log.d("MatchingRepo", "Found ${candidates.size()} candidates in ${queryTime}ms")

        if (candidates.isEmpty) {
            Log.d("MatchingRepo", "No candidates found")
            return null
        }

        // 2️⃣ Client-side tam similarity hesapla
        val matches = candidates.documents
            .asSequence()
            .filter { doc ->
                val docId = doc.id
                val docUid = doc.getString("uid")

                // Kendini ve kendi session'ını hariç tut
                val isValid = docId != mySessionId && docUid != myUid && docUid != null

                if (!isValid) {
                    Log.d("MatchingRepo", "Skipping document: $docId (self or invalid)")
                }

                isValid
            }
            .map { doc ->
                val otherUid = doc.getString("uid") ?: return@map null
                val otherTags = doc.get("tags") as? List<String> ?: emptyList()

                val similarity = calculateTagSimilarity(myTags, otherTags)

                Log.d(
                    "MatchingRepo",
                    "Candidate $otherUid: $similarity% similarity (tags: $otherTags)"
                )

                Triple(otherUid, similarity, doc.id)
            }
            .filterNotNull()
            .filter { it.second > 0 } // Min %60 benzerlik
            .sortedByDescending { it.second }
            .toList()

        if (matches.isEmpty()) {
            Log.d("MatchingRepo", "No matches above 60% threshold")
            return null
        }

        val bestMatch = matches.first()
        Log.d(
            "MatchingRepo",
            "✅ Best match: ${bestMatch.first} with ${bestMatch.second}% similarity"
        )

        // 3️⃣ Her iki session'ı da "matched" olarak işaretle
        try {
            val mySessionUpdate = firestore.collection("sessions")
                .document(mySessionId)
                .update("matched", true)

            val otherSessionUpdate = firestore.collection("sessions")
                .document(bestMatch.third) // Session ID
                .update("matched", true)

            // Her ikisini de paralel güncelle
            Tasks.await(Tasks.whenAll(mySessionUpdate, otherSessionUpdate))

            Log.d("MatchingRepo", "Both sessions marked as matched")
        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error marking sessions as matched: ${e.message}", e)
        }

        return bestMatch.first to bestMatch.second
    }

    // 👇 Similarity hesaplama
    private fun calculateTagSimilarity(tags1: List<String>, tags2: List<String>): Int {
        if (tags1.isEmpty() || tags2.isEmpty()) return 0

        val set1 = tags1.toSet()
        val set2 = tags2.toSet()

        val common = set1.intersect(set2).size
        val total = set1.union(set2).size

        val similarity = if (total > 0) (common * 100 / total) else 0

        Log.d(
            "MatchingRepo",
            "Similarity calculation: $common common out of $total total = $similarity%"
        )

        return similarity
    }

    private suspend fun findAllMatches(
        myUid: String,
        mySessionId: String,
        myTags: List<String>,
        sessionType: DailyActivityContract.SessionType,
        maxMatches: Int
    ): List<Pair<String, Int>> {

        Log.d("MatchingRepo", "Finding up to $maxMatches matches for user: $myUid")

        if (myTags.isEmpty()) return emptyList()

        val searchTags = myTags.take(10)

        // Adayları bul
        val candidatesQuery = firestore.collection("sessions")
            .whereEqualTo("type", sessionType.name)
            .whereArrayContainsAny("tags", searchTags)
            .limit(100)

        val candidates = candidatesQuery.get().await()

        Log.d("MatchingRepo", "Found ${candidates.size()} candidates")

        // 👇 Her candidate için premium status'ü cache'le
        val premiumStatusCache = mutableMapOf<String, Boolean>()

        // Geçerli adayları filtrele ve sırala
        val validMatches = candidates.documents
            .mapNotNull { doc ->
                val docUid = doc.getString("uid") ?: return@mapNotNull null
                val docId = doc.id

                // Kendini hariç tut
                if (docId == mySessionId || docUid == myUid) return@mapNotNull null

                // 👇 Premium status'ü cache'den al veya kontrol et
                val otherIsPremium = premiumStatusCache.getOrPut(docUid) {
                    checkIfPremium(docUid) // runBlocking kullan
                }

                val otherMatchCount = doc.getLong("matchCount")?.toInt() ?: 0
                val otherMaxMatches = if (otherIsPremium) 3 else 1

                // Karşı taraf match alabilir durumda olmalı
                if (otherMatchCount >= otherMaxMatches) {
                    Log.d("MatchingRepo", "User $docUid already has $otherMatchCount matches")
                    return@mapNotNull null
                }

                // Premium ise ve match'i varsa, yeni match daha iyi olmalı
                if (otherMatchCount > 0 && otherIsPremium) {
                    val currentMatches = doc.get("currentMatches") as? List<Map<String, Any>> ?: emptyList()
                    if (currentMatches.isNotEmpty()) {
                        val lowestSimilarity = currentMatches.minOfOrNull {
                            (it["similarity"] as? Long)?.toInt() ?: 0
                        } ?: 0

                        val otherTags = doc.get("tags") as? List<String> ?: emptyList()
                        val newSimilarity = calculateTagSimilarity(myTags, otherTags)

                        if (newSimilarity < lowestSimilarity + 15) {
                            Log.d("MatchingRepo", "New similarity ($newSimilarity%) not better enough than lowest ($lowestSimilarity%)")
                            return@mapNotNull null
                        }
                    }
                }

                // Similarity hesapla
                val otherTags = doc.get("tags") as? List<String> ?: emptyList()
                val similarity = calculateTagSimilarity(myTags, otherTags)

                Log.d("MatchingRepo", "Valid candidate $docUid: $similarity% similarity")

                Triple(docUid, similarity, docId)
            }
            .sortedByDescending { it.second } // En yüksek similarity önce

        if (validMatches.isEmpty()) {
            return emptyList()
        }

        // En fazla maxMatches kadar al
        val selectedMatches = validMatches.take(maxMatches)

        // Match count'ları güncelle
        selectedMatches.forEach { (otherUid, similarity, otherSessionId) ->
            incrementMatchCount(mySessionId, otherUid, similarity)
            incrementMatchCount(otherSessionId, myUid, similarity)
        }

        Log.d("MatchingRepo", "✅ Selected ${selectedMatches.size} matches")

        return selectedMatches.map { it.first to it.second }
    }

    // 👇 YENİ: Match count ve currentMatches güncelle
    private suspend fun incrementMatchCount(
        sessionId: String,
        otherUid: String,
        similarity: Int
    ) {
        try {
            val sessionDoc = firestore.collection("sessions").document(sessionId).get().await()
            val currentMatches =
                sessionDoc.get("currentMatches") as? List<Map<String, Any>> ?: emptyList()

            val newMatch = mapOf(
                "matchId" to "", // Match oluşturulunca set edilecek
                "otherUid" to otherUid,
                "similarity" to similarity,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("sessions")
                .document(sessionId)
                .update(
                    mapOf(
                        "matchCount" to FieldValue.increment(1),
                        "currentMatches" to FieldValue.arrayUnion(newMatch),
                        "matched" to true // Backward compatibility
                    )
                )
                .await()

            Log.d("MatchingRepo", "Match count incremented for session: $sessionId")
        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error incrementing match count: ${e.message}", e)
        }
    }

    private suspend fun createMatch(
        uid1: String, uid2: String, similarity: Int, sessionType: DailyActivityContract.SessionType
    ) {
        try {
            Log.d("MatchingRepo", "Creating match between $uid1 and $uid2")

            // User bilgilerini al
            val user1Doc = firestore.collection("users").document(uid1).get().await()
            val user2Doc = firestore.collection("users").document(uid2).get().await()

            val user1Name = user1Doc.getString("name") ?: "Unknown"
            val user1Photo = user1Doc.getString("photoUrl") ?: ""
            val user2Name = user2Doc.getString("name") ?: "Unknown"
            val user2Photo = user2Doc.getString("photoUrl") ?: ""

            val matchRef = firestore.collection("matches").document()
            val matchData = hashMapOf(
                "participants" to listOf(uid1, uid2),
                "selectionSimilarity" to similarity,
                "soulSyncScore" to 0,
                "soulSyncCompleted" to false,
                "sessionType" to sessionType.name,
                "similarity" to similarity,
                "matchedUserName_$uid1" to user2Name,
                "matchedUserPhoto_$uid1" to user2Photo,
                "matchedUserName_$uid2" to user1Name,
                "matchedUserPhoto_$uid2" to user1Photo,
                "timestamp" to System.currentTimeMillis()
            )

            matchRef.set(matchData).await()

            updateMatchIdInSession(uid1, uid2, matchRef.id)
            updateMatchIdInSession(uid2, uid1, matchRef.id)

            Log.d(
                "MatchingRepo",
                "✅ Match created: ${matchRef.id} with $similarity% similarity"
            )
        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error creating match: ${e.message}", e)
            throw e
        }
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
                    if (match["otherUid"] == otherUid) {
                        match.toMutableMap().apply {
                            put("matchId", matchId)
                        }
                    } else {
                        match
                    }
                }

                doc.reference.update("currentMatches", updatedMatches).await()
            }
        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error updating matchId: ${e.message}", e)
        }
    }

    private suspend fun checkIfPremium(uid: String): Boolean {
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            userDoc.getBoolean("isPremium") ?: false
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun unmatch(
        matchId: String,
        conversationId: String,
        myUid: String
    ): Result<Unit> {
        return try {
            // Match dökümanını sil
            firestore.collection("matches")
                .document(matchId)
                .delete()
                .await()

            val conversationDoc = firestore.collection("conversations")
                .document(conversationId)
                .get()
                .await()

            val participants = conversationDoc.get("participants") as? List<String>

            firestore.collection("conversations")
                .document(conversationId)
                .delete()
                .await()

            participants?.let { uidList ->

                val sessionSnapshot = firestore.collection("sessions")
                    .whereIn("uid", uidList)   // burada whereIn kullanıyoruz
                    .get()
                    .await()

                for (doc in sessionSnapshot.documents) {
                    doc.reference
                        .update("matched", false)
                        .await()
                }
            }

            Log.d("MatchingRepo", "Match deleted: $matchId by user: $myUid")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error deleting match: ${e.message}", e)
            Result.failure(e)
        }
    }
}