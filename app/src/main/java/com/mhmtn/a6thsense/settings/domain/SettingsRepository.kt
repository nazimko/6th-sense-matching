package com.mhmtn.a6thsense.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun updateTheme(isDark: Boolean)
    suspend fun updateMatchNotifications(enabled: Boolean)
    suspend fun updateMessageNotifications(enabled: Boolean)
    suspend fun updateDisplayName(name: String)
    suspend fun updatePhoto(photoUrl: String)
    suspend fun setShowInDiscover(enabled: Boolean)
}