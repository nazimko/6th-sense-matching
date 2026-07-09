package com.mhmtn.a6thsense.home.presentation

import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.home.domain.TodayMatch

data class HomeUiState(
    val isLoading: Boolean = false,
    val hasCompletedIntuitionToday: Boolean = false,
    val hasCompletedPreferenceToday: Boolean = false,
    val matchedUser: AuthUser? = null,
    val similarity: Int? = null,
    val todayMatches: List<TodayMatch> = emptyList(),
    val conversationId: String = "",
    val currentStreak: Int = 0,
    val completedToday: Boolean = false,
    val isPremium: Boolean = false,
    val error: UiText? = null,
    val minSimilarity: Int = 40 // ✅ NEW
)
