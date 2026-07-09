package com.mhmtn.a6thsense.home.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.home.domain.HomeRepository
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : HomeRepository {

    override suspend fun getMatchedUser(): AuthUser? {
        val uid = auth.currentUser?.uid ?: return null

        val matchDoc = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return null

        val otherUid = (matchDoc.get("participants") as? List<*>)
            ?.firstOrNull { it != uid }?.toString() ?: return null

        val matchedDoc = firestore
            .collection("users")
            .document(otherUid)
            .get()
            .await()

        if (!matchedDoc.exists()) return null

        // 👇 Fotoğraf önceliği: profileImageUrl > matchedUserPhoto_$uid > photoUrl
        val photo = matchedDoc.getString("profileImageUrl")
            ?: matchDoc.getString("matchedUserPhoto_$uid")
            ?: matchedDoc.getString("photoUrl")

        return AuthUser(
            uid = otherUid,
            name = matchDoc.getString("matchedUserName_$uid") ?: matchedDoc.getString("name") ?: "",
            photoUrl = photo
        )
    }

    override suspend fun getTodaysSessions(uid: String, date: String): List<DailyActivityContract.State> {
        return try {
            val snapshot = firestore.collection("sessions")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", date)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(DailyActivityContract.State::class.java)
            }
        } catch (e: Exception) {
            Log.e("MatchingRepo", "Error getting today's sessions: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getSimilarity(): Int? {
        val uid = auth.currentUser?.uid ?: return null

        val matchDoc = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()
            .documents
            .firstOrNull() ?: return null

        return matchDoc.getLong("similarity")?.toInt()
    }

    override suspend fun getCurrentStreak(uid: String): Int {
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            
            val activityHistory = userDoc.get("activityHistory") as? List<String> ?: emptyList()
            
            if (activityHistory.isEmpty()) return 0

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val now = System.currentTimeMillis()
            val oneDayMs = TimeUnit.DAYS.toMillis(1)
            
            val uniqueSortedDates = activityHistory.distinct().sortedDescending()
            
            var streak = 0
            val today = dateFormat.format(Date(now))
            val yesterday = dateFormat.format(Date(now - oneDayMs))
            
            if (uniqueSortedDates.first() == today || uniqueSortedDates.first() == yesterday) {
                streak = 1
                for (i in 0 until uniqueSortedDates.size - 1) {
                    val d1 = dateFormat.parse(uniqueSortedDates[i])?.time ?: 0L
                    val d2 = dateFormat.parse(uniqueSortedDates[i+1])?.time ?: 0L
                    val diff = (d1 - d2) / oneDayMs
                    
                    if (diff == 1L) {
                        streak++
                    } else break
                }
            }
            streak
        } catch (e: Exception) {
            Log.e("HomeRepo", "Error calculating streak: ${e.message}")
            0
        }
    }

    override suspend fun isCompletedToday(uid: String): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val session = firestore
            .collection("sessions")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", today)
            .get()
            .await()

        return !session.isEmpty
    }

    override suspend fun isPremium(uid: String): Boolean {
        val userDoc = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        return userDoc.getBoolean("isPremium") ?: false
    }
}
