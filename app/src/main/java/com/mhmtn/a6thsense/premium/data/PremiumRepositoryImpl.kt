package com.mhmtn.a6thsense.premium.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private val Context.premiumDataStore by preferencesDataStore("premium")

class PremiumRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : PremiumRepository {

    companion object {
        val SWIPE_COUNT = intPreferencesKey("swipe_count")
        val SWIPE_DATE = stringPreferencesKey("swipe_date")
        val MESSAGE_COUNT = intPreferencesKey("message_count")
        val MESSAGE_DATE = stringPreferencesKey("message_date")
        val SOUL_SYNC_COUNT = intPreferencesKey("soul_sync_count")   // YENİ
        val SOUL_SYNC_DATE = stringPreferencesKey("soul_sync_date")  // YENİ
    }

    override fun getPremiumStatus(uid: String): Flow<PremiumStatus> {
        return context.premiumDataStore.data.map { prefs ->
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val savedDate = prefs[SWIPE_DATE] ?: ""
            // Yeni gün başladıysa sıfırla
            val swipesUsed = if (savedDate == today) prefs[SWIPE_COUNT] ?: 0 else 0

            val savedMessageDate = prefs[MESSAGE_DATE] ?: ""
            val messagesUsed = if (savedMessageDate == today) prefs[MESSAGE_COUNT] ?: 0 else 0

            val savedSoulSyncDate = prefs[SOUL_SYNC_DATE] ?: ""
            val soulSyncUsed = if (savedSoulSyncDate == today) prefs[SOUL_SYNC_COUNT] ?: 0 else 0

            // Firestore'dan premium durumu kontrol et
            val isPremium = try {
                val userDoc = firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()
                userDoc.getBoolean("isPremium") ?: false
            } catch (e: Exception) {
                false
            }

            PremiumStatus(
                isPremium = isPremium,
                dailySwipesUsed = swipesUsed,
                dailySwipeLimit = 3,
                dailyMessagesUsed = messagesUsed,
                dailyMessageLimit = 7,
                dailySoulSyncUsed = soulSyncUsed,   // YENİ
                dailySoulSyncLimit = 2,
                canViewMatchHistory = isPremium
            )
        }
    }

    override suspend fun incrementSoulSyncCount(uid: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.premiumDataStore.edit { prefs ->
            val savedDate = prefs[SOUL_SYNC_DATE] ?: ""
            val current = if (savedDate == today) prefs[SOUL_SYNC_COUNT] ?: 0 else 0
            prefs[SOUL_SYNC_COUNT] = current + 1
            prefs[SOUL_SYNC_DATE] = today
        }
    }

    override suspend fun incrementSwipeCount(uid: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.premiumDataStore.edit { prefs ->
            val savedDate = prefs[SWIPE_DATE] ?: ""
            val current = if (savedDate == today) prefs[SWIPE_COUNT] ?: 0 else 0
            prefs[SWIPE_COUNT] = current + 1
            prefs[SWIPE_DATE] = today
        }
    }

    override suspend fun incrementMessageCount(uid: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.premiumDataStore.edit { prefs ->
            val savedDate = prefs[MESSAGE_DATE] ?: ""
            val current = if (savedDate == today) prefs[MESSAGE_COUNT] ?: 0 else 0
            prefs[MESSAGE_COUNT] = current + 1
            prefs[MESSAGE_DATE] = today
        }
    }

    override suspend fun activatePremium(uid: String) {
        firestore.collection("users")
            .document(uid)
            .update("isPremium", true)
            .await()
    }
}