package com.mhmtn.a6thsense.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.mhmtn.a6thsense.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.profile.domain.Badge
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import com.mhmtn.a6thsense.profile.domain.ProfileStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    ) : ProfileRepository {

    override suspend fun getProfileStats(uid: String): ProfileStats {
        val createdAt = auth.currentUser?.metadata?.creationTimestamp ?: System.currentTimeMillis()
        val memberSinceDays = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - createdAt).toInt()

        val userDoc = firestore.collection("users").document(uid).get().await()
        
        // 👇 Artık veriyi kalıcı geçmiş listesinden alıyoruz
        val activityHistory = userDoc.get("activityHistory") as? List<String> ?: emptyList()

        val matches = firestore
            .collection("matches")
            .whereArrayContains("participants", uid)
            .get()
            .await()

        val oneDayMs = TimeUnit.DAYS.toMillis(1)
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 1️⃣ Haftalık aktivite grafiğini activityHistory üzerinden hesapla
        val weeklyActivity = (0..6).map { daysAgo ->
            val dateStr = dateFormat.format(Date(now - (daysAgo * oneDayMs)))
            activityHistory.contains(dateStr)
        }.reversed()

        // 2️⃣ Streak (seri) hesaplaması
        val uniqueSortedDates = activityHistory.distinct().sortedDescending()
        var streak = 0
        if (uniqueSortedDates.isNotEmpty()) {
            val today = dateFormat.format(Date(now))
            val yesterday = dateFormat.format(Date(now - oneDayMs))
            
            // Eğer en son aktivite bugün veya dünse streak başlar
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
        }

        return ProfileStats(
            totalActivities = activityHistory.size,
            currentStreak = streak,
            totalMatches = matches.size(),
            memberSinceDays = memberSinceDays,
            activityDates = emptyList(), // Timestamp listesine artık gerek kalmadı
            weeklyActivity = weeklyActivity
        )
    }

    override suspend fun getBadges(stats: ProfileStats, isPremium: Boolean): List<Badge> {
        return listOf(
            Badge(
                id = "premium",
                emoji = "👑",
                title = UiText.StringResource(R.string.premium_member),
                description = UiText.StringResource(R.string.premium_member_desc),
                isUnlocked = isPremium,
                requiredValue = 1,
                currentValue = if (isPremium) 1 else 0
            ),
            Badge(
                id = "first_activity",
                emoji = "🌟",
                title = UiText.StringResource(R.string.first_activity),
                description = UiText.StringResource(R.string.first_activity_desc),
                isUnlocked = stats.totalActivities >= 1,
                requiredValue = 1,
                currentValue = stats.totalActivities
            ),
            Badge(
                id = "activities_10",
                emoji = "🌙",
                title = UiText.StringResource(R.string.activities_10),
                description = UiText.StringResource(R.string.activities_10_desc),
                isUnlocked = stats.totalActivities >= 10,
                requiredValue = 10,
                currentValue = stats.totalActivities
            ),
            Badge(
                id = "activities_100",
                emoji = "🧘",
                title = UiText.StringResource(R.string.activities_100),
                description = UiText.StringResource(R.string.activities_100_desc),
                isUnlocked = stats.totalActivities >= 100,
                requiredValue = 100,
                currentValue = stats.totalActivities
            ),
            Badge(
                id = "streak_3",
                emoji = "🔥",
                title = UiText.StringResource(R.string.streak_3),
                description = UiText.StringResource(R.string.streak_3_desc),
                isUnlocked = stats.currentStreak >= 3,
                requiredValue = 3,
                currentValue = stats.currentStreak
            ),
            Badge(
                id = "streak_7",
                emoji = "⚡",
                title = UiText.StringResource(R.string.streak_7),
                description = UiText.StringResource(R.string.streak_7_desc),
                isUnlocked = stats.currentStreak >= 7,
                requiredValue = 7,
                currentValue = stats.currentStreak
            ),
            Badge(
                id = "streak_30",
                emoji = "☄️",
                title = UiText.StringResource(R.string.streak_30),
                description = UiText.StringResource(R.string.streak_30_desc),
                isUnlocked = stats.currentStreak >= 30,
                requiredValue = 30,
                currentValue = stats.currentStreak
            ),
            Badge(
                id = "first_match",
                emoji = "💫",
                title = UiText.StringResource(R.string.first_match),
                description = UiText.StringResource(R.string.first_match_desc),
                isUnlocked = stats.totalMatches >= 1,
                requiredValue = 1,
                currentValue = stats.totalMatches
            ),
            Badge(
                id = "matches_5",
                emoji = "🔮",
                title = UiText.StringResource(R.string.matches_5),
                description = UiText.StringResource(R.string.matches_5_desc),
                isUnlocked = stats.totalMatches >= 5,
                requiredValue = 5,
                currentValue = stats.totalMatches
            ),
            Badge(
                id = "matches_20",
                emoji = "🌌",
                title = UiText.StringResource(R.string.matches_20),
                description = UiText.StringResource(R.string.matches_20_desc),
                isUnlocked = stats.totalMatches >= 20,
                requiredValue = 20,
                currentValue = stats.totalMatches
            ),
            Badge(
                id = "veteran",
                emoji = "🏛️",
                title = UiText.StringResource(R.string.veteran),
                description = UiText.StringResource(R.string.veteran_desc),
                isUnlocked = stats.memberSinceDays >= 30,
                requiredValue = 30,
                currentValue = stats.memberSinceDays
            ),
            Badge(
                id = "legend",
                emoji = "✨",
                title = UiText.StringResource(R.string.legend),
                description = UiText.StringResource(R.string.legend_desc),
                isUnlocked = stats.totalActivities >= 50 && stats.totalMatches >= 30,
                requiredValue = 80, // Toplam hedef olarak görselleştirilebilir
                currentValue = stats.totalActivities + stats.totalMatches
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun uploadProfileImage(
        userId: String,
        imageUri: Uri
    ): String {
        val bitmap = withContext(Dispatchers.IO) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }

        val maxSize = 512
        val scaledBitmap = scaleBitmap(bitmap, maxSize)

        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val compressedData = baos.toByteArray()

        val fileName = "$userId.jpg"
        val storageRef = storage.reference.child("profile_images/$userId/$fileName")
        storageRef.putBytes(compressedData).await()

        return storageRef.downloadUrl.await().toString()
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val ratio: Float = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    override suspend fun updateProfileImageUrl(
        userId: String,
        imageUrl: String
    ) {
        firestore.collection("users")
            .document(userId)
            .update("profileImageUrl", imageUrl)
            .await()
    }
}
