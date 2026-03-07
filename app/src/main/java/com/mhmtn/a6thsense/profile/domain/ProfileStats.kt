package com.mhmtn.a6thsense.profile.domain

data class ProfileStats(
    val totalActivities: Int = 0,
    val currentStreak: Int = 0,
    val totalMatches: Int = 0,
    val memberSinceDays: Int = 0,
    val activityDates: List<Long> = emptyList(),
    val weeklyActivity: List<Boolean> = emptyList()
)