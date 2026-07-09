package com.mhmtn.a6thsense.premium.domain

data class PremiumStatus(
    val isPremium: Boolean = false,
    val dailySwipesUsed: Int = 0,
    val dailySwipeLimit: Int = 4, // Ücretsiz günlük swipe limiti
    val dailyMessagesUsed: Int = 0,
    val dailyMessageLimit: Int = 7,
    val dailySoulSyncUsed: Int = 0,   // YENİ
    val dailySoulSyncLimit: Int = 2,   // YENİ
    val canViewMatchHistory: Boolean = false // İlk 3 ücretsiz
)
