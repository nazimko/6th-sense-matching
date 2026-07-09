package com.mhmtn.a6thsense.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.core.data.DataStoreManager
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import com.mhmtn.a6thsense.settings.domain.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SettingsRepository {

    companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val MATCH_NOTIFICATIONS = booleanPreferencesKey("match_notifications")
        val MESSAGE_NOTIFICATIONS = booleanPreferencesKey("message_notifications")
        val MIN_SIMILARITY = intPreferencesKey("min_similarity") // ✅ NEW
    }

    override fun getSettings(): Flow<UserSettings> {
        val uid = auth.currentUser?.uid ?: return DataStoreManager.getDataStore(context).data.map { prefs ->
            UserSettings(
                isDarkTheme = prefs[IS_DARK_THEME] ?: true,
                matchNotificationsEnabled = prefs[MATCH_NOTIFICATIONS] ?: true,
                messageNotificationsEnabled = prefs[MESSAGE_NOTIFICATIONS] ?: true,
                displayName = "",
                photoUrl = "",
                minSimilarity = prefs[MIN_SIMILARITY] ?: 40
            )
        }

        val firestoreFlow = callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null && snapshot.exists()) trySend(snapshot)
                }
            awaitClose { listener.remove() }
        }

        return combine(DataStoreManager.getDataStore(context).data, firestoreFlow) { prefs, userDoc ->
            val name = userDoc.getString("name") ?: auth.currentUser?.displayName ?: ""
            val photoUrl = userDoc.getString("profileImageUrl") ?: auth.currentUser?.photoUrl?.toString() ?: ""

            UserSettings(
                isDarkTheme = prefs[IS_DARK_THEME] ?: true,
                matchNotificationsEnabled = prefs[MATCH_NOTIFICATIONS] ?: true,
                messageNotificationsEnabled = prefs[MESSAGE_NOTIFICATIONS] ?: true,
                displayName = name,
                photoUrl = photoUrl,
                minSimilarity = prefs[MIN_SIMILARITY] ?: 40
            )
        }
    }

    override suspend fun updateTheme(isDark: Boolean) {
        DataStoreManager.getDataStore(context).edit { it[IS_DARK_THEME] = isDark }
    }

    override suspend fun updateMatchNotifications(enabled: Boolean) {
        // Yerel kaydet
        DataStoreManager.getDataStore(context).edit { it[MATCH_NOTIFICATIONS] = enabled }
        // Firestore senkronizasyonu
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).update("matchNotificationsEnabled", enabled)
        }
    }

    override suspend fun updateMessageNotifications(enabled: Boolean) {
        // Yerel kaydet
        DataStoreManager.getDataStore(context).edit { it[MESSAGE_NOTIFICATIONS] = enabled }
        // Firestore senkronizasyonu
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).update("messageNotificationsEnabled", enabled)
        }
    }

    override suspend fun updateDisplayName(name: String) {
        val profileUpdates = userProfileChangeRequest { displayName = name }
        auth.currentUser?.updateProfile(profileUpdates)?.await()
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).update("name", name).await()
        }
    }

    override suspend fun updatePhoto(photoUrl: String) {
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).update("photoUrl", photoUrl).await()
        }
    }

    override suspend fun setShowInDiscover(enabled: Boolean) {
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).update("showInDiscover", enabled).await()
        }
    }

    override suspend fun updateMinSimilarity(value: Int) {
        DataStoreManager.getDataStore(context).edit { it[MIN_SIMILARITY] = value }
    }
}
