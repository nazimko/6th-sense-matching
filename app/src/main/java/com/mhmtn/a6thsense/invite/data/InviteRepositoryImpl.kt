package com.mhmtn.a6thsense.invite.data

import com.mhmtn.a6thsense.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.invite.domain.InviteRepository
import com.mhmtn.a6thsense.invite.domain.ReferralInfo
import com.mhmtn.a6thsense.invite.domain.ReferralReward
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class InviteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : InviteRepository {

    override suspend fun generateReferralCode(uid: String): String {
        // Mevcut kodu kontrol et
        val existingDoc = firestore.collection("referrals").document(uid).get().await()

        if (existingDoc.exists()) {
            return existingDoc.getString("referralCode") ?: generateNewCode()
        }

        val code = generateNewCode()

        firestore.collection("referrals").document(uid).set(
            mapOf(
                "referralCode" to code,
                "referredBy" to null,
                "referredUsers" to emptyList<String>(),
                "totalReferrals" to 0,
                "premiumDaysEarned" to 0,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()

        return code
    }

    override suspend fun getReferralInfo(uid: String): Flow<ReferralInfo> = callbackFlow {
        val listener = firestore.collection("referrals")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {
                    val info = ReferralInfo(
                        referralCode = snapshot.getString("referralCode") ?: "",
                        referredBy = snapshot.getString("referredBy"),
                        referredUsers = snapshot.get("referredUsers") as? List<String> ?: emptyList(),
                        totalReferrals = snapshot.getLong("totalReferrals")?.toInt() ?: 0,
                        premiumDaysEarned = snapshot.getLong("premiumDaysEarned")?.toInt() ?: 0
                    )
                    trySend(info)
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun applyReferralCode(uid: String, code: String): Result<ReferralReward> {
        return try {
            // Referral code sahibini bul
            val referrerSnapshot = firestore.collection("referrals")
                .whereEqualTo("referralCode", code)
                .get()
                .await()

            if (referrerSnapshot.isEmpty) {
                return Result.failure(Exception(R.string.error_invalid_invite_code.toString()))
            }

            val referrerDoc = referrerSnapshot.documents.first()
            val referrerId = referrerDoc.id

            if (referrerId == uid) {
                return Result.failure(Exception(R.string.error_own_code.toString()))
            }

            // Zaten kullanılmış mı kontrol et
            val myReferralDoc = firestore.collection("referrals").document(uid).get().await()
            if (myReferralDoc.exists() && myReferralDoc.getString("referredBy") != null) {
                return Result.failure(Exception(R.string.error_already_used_code.toString()))
            }

            // Reward ver
            val reward = ReferralReward(
                premiumDays = 7,
                extraSwipes = 5,
                badgeUnlocked = true
            )

            // Davet eden kullanıcıya reward
            firestore.collection("referrals").document(referrerId).update(
                mapOf(
                    "referredUsers" to FieldValue.arrayUnion(uid),
                    "totalReferrals" to FieldValue.increment(1),
                    "premiumDaysEarned" to FieldValue.increment(7)
                )
            ).await()

            // Davet edilen kullanıcıya kayıt
            firestore.collection("referrals").document(uid).set(
                mapOf(
                    "referralCode" to generateNewCode(),
                    "referredBy" to referrerId,
                    "referredUsers" to emptyList<String>(),
                    "totalReferrals" to 0,
                    "premiumDaysEarned" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // Premium ekle (her ikisine de)
            addPremiumDays(uid, 7)
            addPremiumDays(referrerId, 7)

            Result.success(reward)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun trackShare(uid: String, platform: String) {
        firestore.collection("referral_shares").add(
            mapOf(
                "uid" to uid,
                "platform" to platform,
                "timestamp" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    override fun getShareLink(referralCode: String): String {
        // Deep link oluştur
        return "https://vibetrybe.app/invite?code=$referralCode"
    }

    private fun generateNewCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    private suspend fun addPremiumDays(uid: String, days: Int) {
        // Premium expiry date'i güncelle
        val userDoc = firestore.collection("users").document(uid).get().await()
        val currentExpiry = userDoc.getLong("premiumExpiryDate") ?: System.currentTimeMillis()
        val newExpiry = maxOf(currentExpiry, System.currentTimeMillis()) + (days * 24 * 60 * 60 * 1000L)

        firestore.collection("users").document(uid).update(
            mapOf(
                "isPremium" to true,
                "premiumExpiryDate" to newExpiry
            )
        ).await()
    }
}