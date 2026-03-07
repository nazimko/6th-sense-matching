package com.mhmtn.a6thsense.friends.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.friends.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import com.mhmtn.a6thsense.R
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
    private val firestore: FirebaseFirestore
) : FriendsRepository {

    // 👇 FRIEND REQUEST
    override suspend fun sendFriendRequest(fromUid: String, toUid: String): Result<String> {
        return try {
            if (fromUid == toUid) {
                return Result.failure(Exception(R.string.error_same_user.toString()))
            }

            // Zaten arkadaş mı kontrol et
            val existingFriendship = firestore.collection("friends")
                .whereIn("user1", listOf(fromUid, toUid))
                .whereIn("user2", listOf(fromUid, toUid))
                .get()
                .await()

            if (!existingFriendship.isEmpty) {
                return Result.failure(Exception(R.string.error_already_friend.toString()))
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

            Log.d("FriendsRepo", "Friend request sent: ${friendshipRef.id}")
            Result.success(friendshipRef.id)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error sending friend request: ${e.message}", e)
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

            Log.d("FriendsRepo", "Friend request accepted: $friendshipId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error accepting friend request: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun rejectFriendRequest(friendshipId: String): Result<Unit> {
        return try {
            firestore.collection("friends").document(friendshipId)
                .update("status", FriendshipStatus.REJECTED.name)
                .await()

            Log.d("FriendsRepo", "Friend request rejected: $friendshipId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error rejecting friend request: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(friendshipId: String): Result<Unit> {
        return try {
            firestore.collection("friends").document(friendshipId).delete().await()
            Log.d("FriendsRepo", "Friend removed: $friendshipId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error removing friend: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 👇 GET FRIENDS
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

                // Her friendship için friend bilgilerini al
                val friends = friendships.mapNotNull { doc ->
                    val user1 = doc.getString("user1") ?: return@mapNotNull null
                    val user2 = doc.getString("user2") ?: return@mapNotNull null
                    val friendUid = if (user1 == uid) user2 else user1

                    // Friend bilgisini çek (suspend içinde olduğumuz için runBlocking kullan)
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
                                photoUrl = friendDoc.getString("photoUrl") ?: "",
                                isPremium = friendDoc.getBoolean("isPremium") ?: false,
                                hasCompletedToday = hasCompletedToday,
                                friendshipId = doc.id
                            )
                        } catch (e: Exception) {
                            Log.e("FriendsRepo", "Error fetching friend: ${e.message}", e)
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
            .whereEqualTo("user2", uid) // Sadece bana gelen istekler
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.map { doc ->
                    Friendship(
                        id = doc.id,
                        user1 = doc.getString("user1") ?: "",
                        user2 = doc.getString("user2") ?: "",
                        status = FriendshipStatus.valueOf(
                            doc.getString("status") ?: FriendshipStatus.PENDING.name
                        ),
                        invitedBy = doc.getString("invitedBy") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L
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
        return try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // 1️⃣ Bugün zaten test yapılmış mı kontrol et
            val existingTest = checkExistingTestToday(myUid, friendUid, today)
            if (existingTest != null) {
                Log.d("FriendsRepo", "Using existing test from today")
                return Result.success(existingTest)
            }

            // 2️⃣ İki kullanıcının da bugünkü session'larını al
            val mySessions = firestore.collection("sessions")
                .whereEqualTo("uid", myUid)
                .whereEqualTo("date", today)
                .get()
                .await()

            val friendSessions = firestore.collection("sessions")
                .whereEqualTo("uid", friendUid)
                .whereEqualTo("date", today)
                .get()
                .await()

            if (mySessions.isEmpty) {
                return Result.failure(Exception(R.string.error_no_today_session.toString()))
            }

            if (friendSessions.isEmpty) {
                return Result.failure(Exception(R.string.error_no_today_friend_session.toString()))
            }

            val mySession = mySessions.documents.first()
            val friendSession = friendSessions.documents.first()

            val myTags = mySession.get("tags") as? List<String> ?: emptyList()
            val friendTags = friendSession.get("tags") as? List<String> ?: emptyList()

            // 3️⃣ Similarity hesapla
            val commonTags = myTags.intersect(friendTags.toSet())
            val totalTags = myTags.union(friendTags.toSet())
            val similarity = if (totalTags.isNotEmpty()) {
                (commonTags.size * 100) / totalTags.size
            } else 0

            // 4️⃣ Friend bilgisini al
            val friendDoc = firestore.collection("users")
                .document(friendUid)
                .get()
                .await()

            val friendName = friendDoc.getString("name") ?: "Unknown"
            val friendPhoto = friendDoc.getString("photoUrl") ?: ""

            // 5️⃣ Test sonucunu kaydet (sadece bir kez)
            val testRef = firestore.collection("compatibility_tests").document()
            val testData = hashMapOf(
                "user1" to myUid,
                "user2" to friendUid,
                "date" to today,
                "similarity" to similarity,
                "commonSelections" to commonTags.toList(),
                "totalSelections" to totalTags.size,
                "timestamp" to System.currentTimeMillis(),
                "details" to mapOf(
                    "user1Selections" to myTags,
                    "user2Selections" to friendTags
                )
            )

            testRef.set(testData).await()

            val result = CompatibilityTestResult(
                testId = testRef.id,
                friendName = friendName,
                friendPhotoUrl = friendPhoto,
                similarity = similarity,
                commonSelections = commonTags.toList(),
                totalSelections = totalTags.size,
                mySelections = myTags,
                theirSelections = friendTags,
                timestamp = System.currentTimeMillis()
            )

            Log.d("FriendsRepo", "✅ New compatibility test saved: $similarity% similarity")
            Result.success(result)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error running compatibility test: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 👇 YENİ: Bugünkü mevcut testi kontrol et
    private suspend fun checkExistingTestToday(
        myUid: String,
        friendUid: String,
        today: String
    ): CompatibilityTestResult? {
        return try {
            // Her iki yönü de kontrol et (user1-user2 veya user2-user1)
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
                // Mevcut test bulundu
                val friendDoc = firestore.collection("users")
                    .document(friendUid)
                    .get()
                    .await()

                CompatibilityTestResult(
                    testId = relevantTest.id,
                    friendName = friendDoc.getString("name") ?: "Unknown",
                    friendPhotoUrl = friendDoc.getString("photoUrl") ?: "",
                    similarity = relevantTest.getLong("similarity")?.toInt() ?: 0,
                    commonSelections = relevantTest.get("commonSelections") as? List<String> ?: emptyList(),
                    totalSelections = relevantTest.getLong("totalSelections")?.toInt() ?: 0,
                    mySelections = emptyList(), // Details gerekirse çek
                    theirSelections = emptyList(),
                    timestamp = relevantTest.getLong("timestamp") ?: 0L
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error checking existing test: ${e.message}", e)
            null
        }
    }

    override fun getCompatibilityHistory(uid: String): Flow<List<CompatibilityTestResult>> = callbackFlow {
        val listener = firestore.collection("compatibility_tests")
            .whereEqualTo("user1", uid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tests = snapshot?.documents?.map { doc ->
                    val friendUid = doc.getString("user2") ?: ""

                    // Friend bilgisini çek
                    val friendDoc = kotlinx.coroutines.runBlocking {
                        try {
                            firestore.collection("users")
                                .document(friendUid)
                                .get()
                                .await()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    CompatibilityTestResult(
                        testId = doc.id,
                        friendName = friendDoc?.getString("name") ?: "Unknown",
                        friendPhotoUrl = friendDoc?.getString("photoUrl") ?: "",
                        similarity = doc.getLong("similarity")?.toInt() ?: 0,
                        commonSelections = doc.get("commonSelections") as? List<String> ?: emptyList(),
                        totalSelections = doc.getLong("totalSelections")?.toInt() ?: 0,
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                } ?: emptyList()

                trySend(tests)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getOrCreateInviteCode(uid: String): String {
        try {
            // 1. Referrals'tan mevcut kodu kontrol et
            val referralDoc = firestore.collection("referrals")
                .document(uid)
                .get()
                .await()

            // Mevcut kod varsa döndür
            if (referralDoc.exists()) {
                val existingCode = referralDoc.getString("referralCode")
                if (!existingCode.isNullOrBlank()) {
                    Log.d("FriendsRepo", "Existing referral code found: $existingCode")
                    return existingCode
                }
            }

            // 2. Kod yoksa yeni oluştur (8 haneli)
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
                    SetOptions.merge() // Mevcut data'yı korur
                )
                .await()

            Log.d("FriendsRepo", "New referral code created: $newCode")
            return newCode

        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error getting/creating referral code: ${e.message}", e)
            throw e
        }
    }

    // 👇 DEĞİŞTİ: acceptInviteCode - Hem arkadaş hem referral ekle
    override suspend fun acceptInviteCode(code: String, accepterUid: String): Result<String> {
        return try {
            // Kodu referrals'tan bul
            val referralsSnapshot = firestore.collection("referrals")
                .whereEqualTo("referralCode", code)
                .get()
                .await()

            if (referralsSnapshot.isEmpty) {
                return Result.failure(Exception(R.string.error_invalid_invite_code.toString()))
            }

            val referralDoc = referralsSnapshot.documents.first()
            val inviterUid = referralDoc.id // Document ID = UID

            if (inviterUid == accepterUid) {
                return Result.failure(Exception(R.string.error_own_code.toString()))
            }

            // Zaten kullanılmış mı kontrol et
            val accepterReferralDoc = firestore.collection("referrals")
                .document(accepterUid)
                .get()
                .await()

            if (accepterReferralDoc.exists() && accepterReferralDoc.getString("referredBy") != null) {
                return Result.failure(Exception(R.string.error_already_used_code.toString()))
            }

            // 1️⃣ Arkadaşlık isteği gönder
            sendFriendRequest(inviterUid, accepterUid)

            // 2️⃣ Referral sistemi güncelle (premium reward)
            // Davet eden kullanıcıya reward
            firestore.collection("referrals").document(inviterUid).update(
                mapOf(
                    "referredUsers" to FieldValue.arrayUnion(accepterUid),
                    "totalReferrals" to FieldValue.increment(1),
                    "premiumDaysEarned" to FieldValue.increment(7)
                )
            ).await()

            // Davet edilen kullanıcıya kayıt
            firestore.collection("referrals").document(accepterUid).set(
                mapOf(
                    "referralCode" to generateRandomCode(), // Kendi kodu
                    "referredBy" to inviterUid,
                    "referredUsers" to emptyList<String>(),
                    "totalReferrals" to 0,
                    "premiumDaysEarned" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // 3️⃣ Premium ekle (her ikisine de)
            addPremiumDays(accepterUid, 7)
            addPremiumDays(inviterUid, 7)

            Result.success(inviterUid)
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error accepting invite code: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 👇 8 haneli kod (referrals ile tutarlı)
    private fun generateRandomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    // 👇 Premium ekle helper
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

            Log.d("FriendsRepo", "Added $days premium days to user: $uid")
        } catch (e: Exception) {
            Log.e("FriendsRepo", "Error adding premium: ${e.message}", e)
        }
    }

    // Helper functions
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