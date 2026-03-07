package com.mhmtn.a6thsense.invite.presentation

import com.mhmtn.a6thsense.invite.domain.ReferralInfo

object InviteFriendsContract {
    data class State(
        val isLoading: Boolean = true,
        val referralInfo: ReferralInfo? = null,
        val showCodeInput: Boolean = false,
        val codeInput: String = "",
        val error: String? = null
    )

    sealed class Action {
        object OnShareClick : Action()
        data class OnPlatformClick(val platform: SharePlatform) : Action()
        object OnCopyCodeClick : Action()
        object OnEnterCodeClick : Action()
        data class OnCodeInputChange(val code: String) : Action()
        object OnApplyCode : Action()
        object OnDismissCodeInput : Action()
    }

    sealed class Effect {
        data class ShareLink(val link: String, val message: String) : Effect()
        data class CopyToClipboard(val text: String) : Effect()
        data class ShowToast(val message: String) : Effect()
        data class ShowReward(val premiumDays: Int) : Effect()
        data class ShareToPlatform(val platform: SharePlatform) : Effect()
    }
}

enum class SharePlatform {
    WHATSAPP, INSTAGRAM, TWITTER, MESSAGE, EMAIL, FACEBOOK, OTHER
}