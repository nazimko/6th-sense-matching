package com.mhmtn.a6thsense.profile.presentation

import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.profile.domain.Badge
import com.mhmtn.a6thsense.profile.domain.ProfileStats

object ProfileContract {

    enum class BottomSheetType {
        MATCHES, ACTIVITY_STATS, BADGES
    }

    data class State(
        val user: AuthUser? = null,
        val stats: ProfileStats = ProfileStats(),
        val isLoading: Boolean = true,
        val error: String? = null,
        val activeSheet: BottomSheetType? = null,
        val showConfetti: Boolean = false,
        val isPremium: Boolean = false,
        val badges: List<Badge> = emptyList()
    )

    sealed class Action {
        object Load : Action()
        data class OpenSheet(val type: BottomSheetType) : Action()
        object CloseSheet : Action()
        object TriggerConfetti : Action()
        object NavigateToMatchHistory : Action()
        object onInviteClick : Action()
        object NavigateToFriends : Action()
        data class Error (val message: UiText): Action()
    }

    sealed class Effect {
        object NavigateToAuth : Effect()
        object TriggerConfetti : Effect()
        object NavigateToMatchHistory : Effect()
        object NavigateToInvite : Effect()
        object NavigateToFriends : Effect()
        data class ProfileImageUploaded (val message: UiText): Effect()
    }
}