package com.mhmtn.a6thsense.contact.presentation

import com.mhmtn.a6thsense.contact.domain.MessageSubject

object ContactUsContract {
    data class State(
        val selectedSubject: MessageSubject = MessageSubject.GENERAL_FEEDBACK,
        val message: String = "",
        val isSending: Boolean = false,
        val error: String? = null
    )

    sealed interface Action {
        data class OnSubjectSelected(val subject: MessageSubject) : Action
        data class OnMessageChanged(val message: String) : Action
        object OnSendClick : Action
        object OnDismissError : Action
    }

    sealed interface Effect {
        object NavigateBack : Effect
        data class ShowToast(val message: String) : Effect
    }
}