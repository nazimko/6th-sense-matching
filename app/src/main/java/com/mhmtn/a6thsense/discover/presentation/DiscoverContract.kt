package com.mhmtn.a6thsense.discover.presentation

import com.mhmtn.a6thsense.discover.domain.DiscoverUser

object DiscoverContract {

    data class State(
        val users: List<DiscoverUser> = emptyList(),
        val currentIndex: Int = 0,
        val isLoading: Boolean = true,
        val isLoadingConversation: Boolean = false,
        val error: String? = null,
        val isEmpty: Boolean = false
    )

    sealed class Action {
        object SwipeLeft : Action()   // Geç
        object SwipeRight : Action()  // Mesajlaş
        object Reload : Action()
    }

    sealed class Effect {
        data class NavigateToMessaging(
            val conversationId: String,
            val matchedUserName: String,
            val matchedUserPhotoUrl: String,
            val matchedUserId: String
        ) : Effect()
        data class ShowToast(val message: String) : Effect()
        data object ShowPaywall : Effect()
    }
}