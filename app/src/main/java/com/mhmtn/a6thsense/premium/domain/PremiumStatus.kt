package com.mhmtn.a6thsense.premium.domain

data class PremiumStatus(
    val isPremium: Boolean = false,
    val dailySwipesUsed: Int = 0,
    val dailySwipeLimit: Int = 2, // Ücretsiz günlük swipe limiti
    val dailyMessagesUsed: Int = 0,
    val dailyMessageLimit: Int = 3,
    val canViewMatchHistory: Boolean = false // İlk 3 ücretsiz
)
