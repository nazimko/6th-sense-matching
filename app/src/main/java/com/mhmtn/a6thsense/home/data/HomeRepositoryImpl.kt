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
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : HomeRepository {

    override suspend fun getMatchedUser(): AuthUser? {
        val uid = auth.currentUser?.uid ?: return null

        // 👇 participants array'e göre ara
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

        return AuthUser(
            uid = otherUid,
            name = matchDoc.getString("matchedUserName_$uid") ?: matchedDoc.getString("name") ?: "",
            photoUrl = matchDoc.getString("matchedUserPhoto_$uid") ?: matchedDoc.getString("photoUrl")
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

        // 👇 participants array'e göre ara
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
        val sessions = firestore
            .collection("sessions")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .await()

        if (sessions.isEmpty) return 0

        var streak = 0
        var previousDate: String? = null

        for (doc in sessions.documents) {
            val date = doc.getString("date") ?: continue

            if (previousDate == null) {
                streak = 1
                previousDate = date
            } else {
                val dayDiff = calculateDayDifference(previousDate, date)
                if (dayDiff <= 2) { // 2 gün tolerans
                    streak++
                    previousDate = date
                } else {
                    break
                }
            }
        }

        return streak
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
        val userDoc = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        return userDoc.getBoolean("isPremium") ?: false
    }

    private fun calculateDayDifference(date1: String, date2: String): Int {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d1 = format.parse(date1)?.time ?: 0L
        val d2 = format.parse(date2)?.time ?: 0L
        return ((d1 - d2) / (1000 * 60 * 60 * 24)).toInt()
    }
}