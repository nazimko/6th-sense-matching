package com.mhmtn.a6thsense.matchhistory.presentation

import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryItem

object MatchHistoryContract {

    data class State(
        val matches: List<MatchHistoryItem> = emptyList(),
        val isLoading: Boolean = true,
        val loadingConversationId: String? = null,
        val isPremium: Boolean = false,
        val totalCount: Int = 0,
        val hasMoreMatches: Boolean = false,
        val error: String? = null
    )

    sealed class Action {
        data class OnMessageClick(val item: MatchHistoryItem) : Action()
        object Reload : Action()
        object OnUpgradeToPremium : Action()
        data class OnSendFriendRequest(val matchedUserId: String) : Action()
    }

    sealed class Effect {
        data class NavigateToMessaging(
            val conversationId: String,
            val matchedUserName: String,
            val matchedUserPhotoUrl: String,
            val matchedUserId: String
        ) : Effect()
        object NavigateToPaywall : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}