package com.mhmtn.a6thsense.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import com.mhmtn.a6thsense.settings.domain.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SettingsRepository {

    companion object {
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val MATCH_NOTIFICATIONS = booleanPreferencesKey("match_notifications")
        val MESSAGE_NOTIFICATIONS = booleanPreferencesKey("message_notifications")
    }

    override fun getSettings(): Flow<UserSettings> {
        return context.dataStore.data.map { prefs ->
            UserSettings(
                isDarkTheme = prefs[IS_DARK_THEME] ?: true,
                matchNotificationsEnabled = prefs[MATCH_NOTIFICATIONS] ?: true,
                messageNotificationsEnabled = prefs[MESSAGE_NOTIFICATIONS] ?: true,
                displayName = auth.currentUser?.displayName ?: "",
                photoUrl = auth.currentUser?.photoUrl?.toString() ?: ""
            )
        }
    }

    override suspend fun updateTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = isDark
        }
    }

    override suspend fun updateMatchNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MATCH_NOTIFICATIONS] = enabled
        }
    }

    override suspend fun updateMessageNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MESSAGE_NOTIFICATIONS] = enabled
        }
    }

    override suspend fun updateDisplayName(name: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        auth.currentUser?.updateProfile(profileUpdates)?.await()

        // Firestore'da da güncelle
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .update("name", name)
                .await()
        }
    }

    override suspend fun updatePhoto(photoUrl: String) {
        // Photo update için Storage entegrasyonu gerekir
        // Şimdilik Firestore'da güncelle
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .update("photoUrl", photoUrl)
                .await()
        }
    }

    override suspend fun setShowInDiscover(enabled: Boolean) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .update("showInDiscover", enabled)
            .await()
    }
}