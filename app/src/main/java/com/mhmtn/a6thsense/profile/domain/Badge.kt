package com.mhmtn.a6thsense.profile.domain

import com.mhmtn.a6thsense.core.presentation.UiText

data class Badge(
    val id: String,
    val emoji: String,
    val title: UiText,
    val description: UiText,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null,
    val requiredValue: Int = 0,
    val currentValue: Int = 0
)
