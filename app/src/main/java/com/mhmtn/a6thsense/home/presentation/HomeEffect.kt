package com.mhmtn.a6thsense.home.presentation

import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType

sealed interface HomeEffect {
    data class NavigateToSimilarity(
        val matchId: String,
        val otherUserName: String,
        val otherUserPhoto: String,
        val similarity: Int
    ) : HomeEffect

    data object NavigateToDaily : HomeEffect
    data object NavigateToAuth : HomeEffect
    data object NavigateToSettings : HomeEffect
    object NavigateToPaywall : HomeEffect
    data class NavigateToSession(val type: SessionType, val threshold: Int) : HomeEffect // ✅ threshold eklendi
}
