package com.mhmtn.a6thsense.profile.domain

data class Badge(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null,
    val requiredValue: Int = 0,
    val currentValue: Int = 0
)
