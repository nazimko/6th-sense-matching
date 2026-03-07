package com.mhmtn.a6thsense.profile.domain

interface ProfileRepository {
    suspend fun getProfileStats(uid: String): ProfileStats
    suspend fun getBadges(stats: ProfileStats, isPremium: Boolean): List<Badge>
}