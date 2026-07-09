package com.mhmtn.a6thsense.conversations.presentation

import com.mhmtn.a6thsense.conversations.domain.ConversationItem

object ConversationListContract {

    data class State(
        val conversations: List<ConversationItem> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null,
        val conversationToDelete: ConversationItem? = null // Silme onayı için
    )

    sealed class Action {
        data class OnConversationClick(val item: ConversationItem) : Action()
        data class OnDeleteConversation(val item: ConversationItem) : Action() // 👇 Yeni
        object ConfirmDelete : Action() // 👇 Yeni
        object DismissDeleteDialog : Action() // 👇 Yeni
        object Reload : Action()
    }

    sealed class Effect {
        data class NavigateToMessaging(
            val conversationId: String,
            val matchedUserName: String,
            val matchedUserPhotoUrl: String,
            val matchedUserId: String
        ) : Effect()
    }
}
