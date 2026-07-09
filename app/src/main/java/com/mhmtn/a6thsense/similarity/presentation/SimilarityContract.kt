package com.mhmtn.a6thsense.similarity.presentation

import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.UiText

object SimilarityContract {

    data class State(
        val isLoading: Boolean = true,
        val similarity: Int = 0,
        val currentUser: AuthUser? = null,
        val matchedUser: AuthUser? = null,
        val roomId: String? = null,
        val conversationId: String = "",
        val hasMatched: Boolean = false,
        val error: UiText? = null
    )

    sealed interface Action {
        object Continue : Action
        object OnMessageClick : Action
    }

    sealed interface Effect {
        object NavigateHome : Effect
        data class NavigateToMessaging(
            val conversationId: String,
            val matchedUserName: String,
            val matchedUserPhotoUrl: String,
            val matchedUserId: String
        ) : Effect
    }
}
