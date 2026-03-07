package com.mhmtn.a6thsense.messaging.presentation

import com.mhmtn.a6thsense.messaging.domain.model.Message

object MessagingContract {

    data class State(
        val messages: List<Message> = emptyList(),
        val currentInput: String = "",
        val isLoading: Boolean = true,
        val reactionTargetMessageId: String? = null, // Reaksiyon seçici açık mı
        val matchedUserName: String = "",
        val matchedUserPhotoUrl: String = "",
        val otherUserId: String = "",
        val showUnmatchDialog: Boolean = false,
        val error: String? = null
    )

    sealed class Action {
        data class TypeMessage(val text: String) : Action()
        object SendMessage : Action()
        data class ShowReactionPicker(val messageId: String) : Action()
        object HideReactionPicker : Action()
        data class AddReaction(val messageId: String, val emoji: String) : Action()
        data class RemoveReaction(val messageId: String) : Action()
        object Reload : Action()
        object OnUnmatchClick : Action()
        object OnConfirmUnmatch : Action()
        object OnDismissUnmatchDialog : Action()
    }

    sealed class Effect {
        object ScrollToBottom : Effect()
        object ShowPaywall : Effect()
        object NavigateBack : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}