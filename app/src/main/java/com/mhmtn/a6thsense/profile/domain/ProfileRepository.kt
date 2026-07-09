package com.mhmtn.a6thsense.profile.domain

import android.net.Uri

interface ProfileRepository {
    suspend fun getProfileStats(uid: String): ProfileStats
    suspend fun getBadges(stats: ProfileStats, isPremium: Boolean): List<Badge>

    suspend fun uploadProfileImage(
        userId: String,
        imageUri: Uri
    ): String

    suspend fun updateProfileImageUrl(
        userId: String,
        imageUrl: String
    )
}