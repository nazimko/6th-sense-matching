package com.mhmtn.a6thsense.friends.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Filter
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.friends.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FriendsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FriendsRepository {

    override suspend fun sendFriendRequest(fromUid: String, toUid: String): Result<String> {
        return try {
            if (fromUid == toUid) {
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_same_user)))
            }

            val existingFriendship = firestore.collection("friends")
                .whereIn("user1", listOf(fromUid, toUid))
                .whereIn("user2", listOf(fromUid, toUid))
                .get()
                .await()

            if (!existingFriendship.isEmpty) {
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_already_friend)))
            }

            val friendshipRef = firestore.collection("friends").document()
            val friendship = hashMapOf(
                "user1" to fromUid,
                "user2" to toUid,
                "status" to FriendshipStatus.PENDING.name,
                "invitedBy" to fromUid,
                "createdAt" to FieldValue.serverTimestamp()
            )

            friendshipRef.set(friendship).await()
            Result.success(friendshipRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(friendshipId: String): Result<Unit> {
        return try {
            firestore.collection("friends").document(friendshipId)
                .update(
                    mapOf(
                        "status" to FriendshipStatus.ACCEPTED.name,
                        "acceptedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(friendshipId: String): Result<Unit> {
        return try {
            firestore.collection("friends").document(friendshipId)
                .update("status", FriendshipStatus.REJECTED.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(friendshipId: String): Result<Unit> {
        return try {
            firestore.collection("friends").document(friendshipId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFriends(uid: String): Flow<List<Friend>> = callbackFlow {
        val listener = firestore.collection("friends")
            .whereEqualTo("status", FriendshipStatus.ACCEPTED.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val friendships = snapshot?.documents?.filter { doc ->
                    val user1 = doc.getString("user1")
                    val user2 = doc.getString("user2")
                    user1 == uid || user2 == uid
                } ?: emptyList()

                val friends = friendships.mapNotNull { doc ->
                    val user1 = doc.getString("user1") ?: return@mapNotNull null
                    val user2 = doc.getString("user2") ?: return@mapNotNull null
                    val friendUid = if (user1 == uid) user2 else user1

                    kotlinx.coroutines.runBlocking {
                        try {
                            val friendDoc = firestore.collection("users")
                                .document(friendUid)
                                .get()
                                .await()

                            val hasCompletedToday = checkTodaySession(friendUid)

                            Friend(
                                uid = friendUid,
                                name = friendDoc.getString("name") ?: "Unknown",
                                photoUrl = friendDoc.getString("profileImageUrl") ?: friendDoc.getString("photoUrl") ?: "",
                                isPremium = friendDoc.getBoolean("isPremium") ?: false,
                                hasCompletedToday = hasCompletedToday,
                                friendshipId = doc.id
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                trySend(friends)
            }
        awaitClose { listener.remove() }
    }

    override fun getPendingRequests(uid: String): Flow<List<Friendship>> = callbackFlow {
        val listener = firestore.collection("friends")
            .whereEqualTo("status", FriendshipStatus.PENDING.name)
            .whereEqualTo("user2", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.map { doc ->
                    val timestamp = doc.getTimestamp("createdAt")
                    Friendship(
                        id = doc.id,
                        user1 = doc.getString("user1") ?: "",
                        user2 = doc.getString("user2") ?: "",
                        status = FriendshipStatus.valueOf(
                            doc.getString("status") ?: FriendshipStatus.PENDING.name
                        ),
                        invitedBy = doc.getString("invitedBy") ?: "",
                        createdAt = timestamp?.toDate()?.time ?: 0L
                    )
                } ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun runCompatibilityTest(
        myUid: String,
        friendUid: String
    ): Result<CompatibilityTestResult> {
        Log.d("FriendsRepo", "runCompatibilityTest START: myUid=$myUid, friendUid=$friendUid")
        return try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            val existingTest = checkExistingTestToday(myUid, friendUid, today)
            if (existingTest != null) {
                Log.d("FriendsRepo", "Existing test found for today: ${existingTest.similarity}%")
                return Result.success(existingTest)
            }

            val mySessionsSnapshot = firestore.collection("sessions")
                .whereEqualTo("uid", myUid)
                .whereEqualTo("date", today)
                .get().await()

            val friendSessionsSnapshot = firestore.collection("sessions")
                .whereEqualTo("uid", friendUid)
                .whereEqualTo("date", today)
                .get().await()

            if (mySessionsSnapshot.isEmpty) {
                Log.w("FriendsRepo", "My sessions empty for today")
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_no_today_session)))
            }

            if (friendSessionsSnapshot.isEmpty) {
                Log.w("FriendsRepo", "Friend sessions empty for today")
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_no_today_friend_session)))
            }

            val mySessionsMap = mySessionsSnapshot.documents.associateBy { it.getString("type") ?: "" }
            val friendSessionsMap = friendSessionsSnapshot.documents.associateBy { it.getString("type") ?: "" }
            val commonTypes = mySessionsMap.keys.intersect(friendSessionsMap.keys).filter { it.isNotEmpty() }
            
            Log.d("FriendsRepo", "Common session types: $commonTypes")

            if (commonTypes.isEmpty()) {
                Log.w("FriendsRepo", "No common types found between users")
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_not_same_session)))
            }

            var totalCommonCount = 0
            var totalPossibleCount = 0
            val commonTagsList = mutableListOf<String>()
            val aggregatedMyTags = mutableListOf<String>()
            val aggregatedFriendTags = mutableListOf<String>()

            for (type in commonTypes) {
                val s1 = mySessionsMap[type]!!
                val s2 = friendSessionsMap[type]!!

                val tags1 = s1.get("tags") as? List<String> ?: emptyList()
                val tags2 = s2.get("tags") as? List<String> ?: emptyList()
                val free1 = s1.get("freeTextAnswers") as? Map<String, String> ?: emptyMap()
                val free2 = s2.get("freeTextAnswers") as? Map<String, String> ?: emptyMap()

                aggregatedMyTags.addAll(tags1)
                aggregatedFriendTags.addAll(tags2)

                val tagLen = minOf(tags1.size, tags2.size)
                for (i in 0 until tagLen) {
                    if (tags1[i] == tags2[i]) {
                        commonTagsList.add(tags1[i])
                        totalCommonCount++
                    }
                }
                totalPossibleCount += maxOf(tags1.size, tags2.size)

                free1.forEach { (qid, ans1) ->
                    val ans2 = free2[qid]
                    if (ans2 != null) {
                        val n1 = ans1.trim().lowercase(Locale.getDefault())
                        val n2 = ans2.trim().lowercase(Locale.getDefault())
                        if (n1 == n2 && n1.isNotEmpty()) {
                            totalCommonCount++
                        }
                    }
                }
                totalPossibleCount += (free1.keys + free2.keys).distinct().size
            }

            val similarity = if (totalPossibleCount > 0) (totalCommonCount * 100 / totalPossibleCount) else 0
            Log.d("FriendsRepo", "Calculated similarity: $similarity% ($totalCommonCount/$totalPossibleCount)")

            val friendDoc = firestore.collection("users").document(friendUid).get().await()
            val friendName = friendDoc.getString("name") ?: "Unknown"
            val friendPhoto = friendDoc.getString("profileImageUrl") ?: friendDoc.getString("photoUrl") ?: ""

            val testRef = firestore.collection("compatibility_tests").document()
            val testData = hashMapOf(
                "user1" to myUid,
                "user2" to friendUid,
                "date" to today,
                "similarity" to similarity,
                "commonSelections" to commonTagsList,
                "totalSelections" to totalPossibleCount,
                "timestamp" to System.currentTimeMillis(),
                "visibleFor" to listOf(myUid, friendUid), // 👈 UID'leri listeye ekle
                "details" to mapOf(
                    "user1Selections" to aggregatedMyTags,
                    "user2Selections" to aggregatedFriendTags
                )
            )

            testRef.set(testData).await()

            return Result.success(CompatibilityTestResult(
                testId = testRef.id,
                friendName = friendName,
                friendPhotoUrl = friendPhoto,
                similarity = similarity,
                commonSelections = commonTagsList,
                totalSelections = totalPossibleCount,
                mySelections = aggregatedMyTags,
                theirSelections = aggregatedFriendTags,
                visibleFor = listOf(myUid, friendUid),
                timestamp = System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error in runCompatibilityTest: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun getCompatibilityHistory(uid: String): Flow<List<CompatibilityTestResult>> = callbackFlow {
        Log.d("FriendsRepo", "getCompatibilityHistory START for uid: $uid")
        
        val listener = firestore.collection("compatibility_tests")
            .whereArrayContains("visibleFor", uid) // 👈 Sadece kendi UID'si olanları getir
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val tests = snapshot.documents.map { doc ->
                    val user1Id = doc.getString("user1") ?: ""
                    val user2Id = doc.getString("user2") ?: ""
                    val friendUid = if (user1Id == uid) user2Id else user1Id

                    val friendDoc = kotlinx.coroutines.runBlocking {
                        try {
                            firestore.collection("users").document(friendUid).get().await()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    CompatibilityTestResult(
                        testId = doc.id,
                        friendName = friendDoc?.getString("name") ?: "Unknown",
                        friendPhotoUrl = friendDoc?.getString("profileImageUrl") ?: friendDoc?.getString("photoUrl") ?: "",
                        similarity = doc.getLong("similarity")?.toInt() ?: 0,
                        commonSelections = doc.get("commonSelections") as? List<String> ?: emptyList(),
                        totalSelections = doc.getLong("totalSelections")?.toInt() ?: 0,
                        visibleFor = doc.get("visibleFor") as? List<String> ?: emptyList(),
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                trySend(tests)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteCompatibilityTest(testId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            val docRef = firestore.collection("compatibility_tests").document(testId)
            val doc = docRef.get().await()
            
            if (!doc.exists()) return Result.success(Unit)

            val visibleFor = doc.get("visibleFor") as? MutableList<String> ?: mutableListOf()
            visibleFor.remove(uid)

            if (visibleFor.isEmpty()) {
                docRef.delete().await()
            } else {
                docRef.update("visibleFor", visibleFor).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrCreateInviteCode(uid: String): String {
        val referralDoc = firestore.collection("referrals").document(uid).get().await()
        if (referralDoc.exists()) {
            val existingCode = referralDoc.getString("referralCode")
            if (!existingCode.isNullOrBlank()) return existingCode
        }

        val newCode = generateRandomCode()
        firestore.collection("referrals").document(uid)
            .set(
                mapOf(
                    "referralCode" to newCode,
                    "referredBy" to null,
                    "referredUsers" to emptyList<String>(),
                    "totalReferrals" to 0,
                    "premiumDaysEarned" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()
        return newCode
    }

    override suspend fun acceptInviteCode(code: String, accepterUid: String): Result<String> {
        return try {
            val referralsSnapshot = firestore.collection("referrals")
                .whereEqualTo("referralCode", code)
                .get()
                .await()

            if (referralsSnapshot.isEmpty) {
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_invalid_invite_code)))
            }

            val referralDoc = referralsSnapshot.documents.first()
            val inviterUid = referralDoc.id

            if (inviterUid == accepterUid) {
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_own_code)))
            }

            val accepterReferralDoc = firestore.collection("referrals").document(accepterUid).get().await()
            if (accepterReferralDoc.exists() && accepterReferralDoc.getString("referredBy") != null) {
                return Result.failure(UiTextException(UiText.StringResource(R.string.error_already_used_code)))
            }

            sendFriendRequest(inviterUid, accepterUid)

            firestore.collection("referrals").document(inviterUid).update(
                mapOf(
                    "referredUsers" to FieldValue.arrayUnion(accepterUid),
                    "totalReferrals" to FieldValue.increment(1),
                    "premiumDaysEarned" to FieldValue.increment(7)
                )
            ).await()

            firestore.collection("referrals").document(accepterUid).set(
                mapOf(
                    "referralCode" to generateRandomCode(),
                    "referredBy" to inviterUid,
                    "referredUsers" to emptyList<String>(),
                    "totalReferrals" to 0,
                    "premiumDaysEarned" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            addPremiumDays(accepterUid, 7)
            addPremiumDays(inviterUid, 7)

            Result.success(inviterUid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startSoulSyncWithFriend(myUid: String, friendUid: String): Result<String> {
        return try {
            val meDoc = firestore.collection("users").document(myUid).get().await()
            val friendDoc = firestore.collection("users").document(friendUid).get().await()
            
            val myName = meDoc.getString("name") ?: "You"
            val myPhoto = meDoc.getString("profileImageUrl") ?: meDoc.getString("photoUrl") ?: ""
            val friendName = friendDoc.getString("name") ?: "Friend"
            val friendPhoto = friendDoc.getString("profileImageUrl") ?: friendDoc.getString("photoUrl") ?: ""

            val friendshipSnapshot = firestore.collection("friends")
                .whereIn("user1", listOf(myUid, friendUid))
                .whereIn("user2", listOf(myUid, friendUid))
                .get()
                .await()

            val friendshipDoc = friendshipSnapshot.documents.firstOrNull() 
                ?: return Result.failure(Exception("Friendship not found"))

            val roomId = friendshipDoc.id
            
            friendshipDoc.reference.update(
                mapOf(
                    "soulSyncRoomId" to roomId,
                    "soulSyncScore" to 0,
                    "soulSyncCompleted" to false
                )
            ).await()

            val databaseUrl = "https://sixth-sense-9647e-default-rtdb.europe-west1.firebasedatabase.app"
            val roomsRef = Firebase.database(databaseUrl).reference.child("soul_sync_rooms")
            
            val roomData = mapOf(
                "matchId" to roomId,
                "players" to mapOf(
                    myUid to mapOf(
                        "uid" to myUid,
                        "name" to myName,
                        "photoUrl" to myPhoto,
                        "status" to "invited", // 👈 Host is invited too, will join via VM
                        "score" to 0
                    ),
                    friendUid to mapOf(
                        "uid" to friendUid,
                        "name" to friendName,
                        "photoUrl" to friendPhoto,
                        "status" to "invited",
                        "score" to 0
                    )
                ),
                "gameState" to "waiting",
                "currentRound" to 1,
                "createdAt" to com.google.firebase.database.ServerValue.TIMESTAMP
            )
            
            roomsRef.child(roomId).setValue(roomData).await()

            Result.success(roomId)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error starting soul sync with friend", e)
            Result.failure(e)
        }
    }

    private suspend fun checkExistingTestToday(
        myUid: String,
        friendUid: String,
        today: String
    ): CompatibilityTestResult? {
        return try {
            val existingTests = firestore.collection("compatibility_tests")
                .whereEqualTo("date", today)
                .get()
                .await()

            val relevantTest = existingTests.documents.firstOrNull { doc ->
                val user1 = doc.getString("user1")
                val user2 = doc.getString("user2")
                (user1 == myUid && user2 == friendUid) || (user1 == friendUid && user2 == myUid)
            }

            if (relevantTest != null) {
                val friendDoc = firestore.collection("users").document(friendUid).get().await()
                return CompatibilityTestResult(
                    testId = relevantTest.id,
                    friendName = friendDoc.getString("name") ?: "Unknown",
                    friendPhotoUrl = friendDoc.getString("profileImageUrl") ?: friendDoc.getString("photoUrl") ?: "",
                    similarity = relevantTest.getLong("similarity")?.toInt() ?: 0,
                    commonSelections = relevantTest.get("commonSelections") as? List<String> ?: emptyList(),
                    totalSelections = relevantTest.getLong("totalSelections")?.toInt() ?: 0,
                    mySelections = emptyList(),
                    theirSelections = emptyList(),
                    visibleFor = relevantTest.get("visibleFor") as? List<String> ?: emptyList(),
                    timestamp = relevantTest.getLong("timestamp") ?: 0L
                )
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun generateRandomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    private suspend fun addPremiumDays(uid: String, days: Int) {
        try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            val currentExpiry = userDoc.getLong("premiumExpiryDate") ?: System.currentTimeMillis()
            val newExpiry = maxOf(currentExpiry, System.currentTimeMillis()) + (days * 24 * 60 * 60 * 1000L)

            firestore.collection("users").document(uid).update(
                mapOf(
                    "isPremium" to true,
                    "premiumExpiryDate" to newExpiry
                )
            ).await()
        } catch (e: Exception) {}
    }

    private suspend fun checkTodaySession(uid: String): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sessions = firestore.collection("sessions")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today)
            .get()
            .await()
        return !sessions.isEmpty
    }
}