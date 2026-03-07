package com.mhmtn.a6thsense.profile.data

import com.mhmtn.a6thsense.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.profile.domain.Badge
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import com.mhmtn.a6thsense.profile.domain.ProfileStats
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    override suspend fun getProfileStats(uid: String): ProfileStats {
        // 👇 createdAt Firebase Auth'dan
        val createdAt = FirebaseAuth.getInstance()
            .currentUser?.metadata?.creationTimestamp
            ?: System.currentTimeMillis()

        val memberSinceDays = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - createdAt
        ).toInt()

        // 👇 uid field'ına göre sorgula
        val sessions = firestore
            .collection("sessions")
            .whereEqualTo("uid", uid)
            .get()
            .await()

        // 👇 participants array'e göre sorgula
        val matches = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()

        val oneDayMs = TimeUnit.DAYS.toMillis(1)

        // 👇 timestamp field'ına göre sırala
        val timestamps = sessions.documents
            .mapNotNull { it.getLong("timestamp") }
            .sortedDescending()

        var streak = 0
        var currentDay = System.currentTimeMillis()

        for (timestamp in timestamps) {
            val diff = currentDay - timestamp
            if (diff <= oneDayMs * 2) {
                streak++
                currentDay = timestamp
            } else break
        }

        val now = System.currentTimeMillis()
        val weeklyActivity = (0..6).map { daysAgo ->
            val dayStart = now - (daysAgo * oneDayMs)
            val dayEnd = dayStart - oneDayMs
            timestamps.any { it in dayEnd..dayStart }
        }.reversed()

        return ProfileStats(
            totalActivities = sessions.size(),
            currentStreak = streak,
            totalMatches = matches.size(),
            memberSinceDays = memberSinceDays,
            activityDates = timestamps,
            weeklyActivity = weeklyActivity
        )
    }

    override suspend fun getBadges(stats: ProfileStats, isPremium: Boolean): List<Badge> {
        return listOf(
            Badge(
                id = "premium",
                emoji = "👑",
                title = R.string.premium_member.toString(),
                description = R.string.premium_member_desc.toString(),
                isUnlocked = isPremium, // 👈
                requiredValue = 1,
                currentValue = if (isPremium) 1 else 0
            ),
            Badge(
                id = "first_activity",
                emoji = "🌟",
                title = R.string.first_activity.toString(),
                description = R.string.first_activity_desc.toString(),
                isUnlocked = stats.totalActivities >= 1,
                requiredValue = 1,
                currentValue = stats.totalActivities
            ),
            Badge(
                id = "streak_3",
                emoji = "🔥",
                title = R.string.streak_3.toString(),
                description = R.string.streak_3_desc.toString(),
                isUnlocked = stats.currentStreak >= 3,
                requiredValue = 3,
                currentValue = stats.currentStreak
            ),
            Badge(
                id = "streak_7",
                emoji = "⚡",
                title = R.string.streak_7.toString(),
                description = R.string.streak_7_desc.toString(),
                isUnlocked = stats.currentStreak >= 7,
                requiredValue = 7,
                currentValue = stats.currentStreak
            ),
            Badge(
                id = "first_match",
                emoji = "💫",
                title = R.string.first_match.toString(),
                description = R.string.first_match_desc.toString(),
                isUnlocked = stats.totalMatches >= 1,
                requiredValue = 1,
                currentValue = stats.totalMatches
            ),
            Badge(
                id = "matches_5",
                emoji = "🔮",
                title = R.string.matches_5.toString(),
                description = R.string.matches_5_desc.toString(),
                isUnlocked = stats.totalMatches >= 5,
                requiredValue = 5,
                currentValue = stats.totalMatches
            ),
            Badge(
                id = "activities_10",
                emoji = "🌙",
                title = R.string.activities_10.toString(),
                description = R.string.activities_10_desc.toString(),
                isUnlocked = stats.totalActivities >= 10,
                requiredValue = 10,
                currentValue = stats.totalActivities
            ),
            Badge(
                id = "veteran",
                emoji = "👑",
                title = R.string.veteran.toString(),
                description = R.string.veteran_desc.toString(),
                isUnlocked = stats.memberSinceDays >= 30,
                requiredValue = 30,
                currentValue = stats.memberSinceDays
            ),
            Badge(
                id = "legend",
                emoji = "✨",
                title = R.string.legend.toString(),
                description = R.string.legend_desc.toString(),
                isUnlocked = stats.totalActivities >= 50,
                requiredValue = 50,
                currentValue = stats.totalActivities
            )
        )
    }
}