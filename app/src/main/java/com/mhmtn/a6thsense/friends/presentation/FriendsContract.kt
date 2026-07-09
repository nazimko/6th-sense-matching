package com.mhmtn.a6thsense.friends.presentation

import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.friends.domain.model.CompatibilityTestResult
import com.mhmtn.a6thsense.friends.domain.model.Friend
import com.mhmtn.a6thsense.friends.domain.model.Friendship
import com.mhmtn.a6thsense.premium.domain.PremiumStatus


object FriendsContract {
    data class State(
        val isLoading: Boolean = true,
        val friends: List<Friend> = emptyList(),
        val pendingRequests: List<Friendship> = emptyList(),
        val compatibilityHistory: List<CompatibilityTestResult> = emptyList(),
        val selectedTab: Tab = Tab.FRIENDS,
        val inviteCode: String? = null,
        val showInviteDialog: Boolean = false,
        val showTestResultDialog: Boolean = false,
        val testResult: CompatibilityTestResult? = null,
        val friendToRemove: Friend? = null,
        val showRemoveFriendDialog: Boolean = false,
        val premiumStatus: PremiumStatus = PremiumStatus(),
        val error: String? = null
    )

    enum class Tab {
        FRIENDS,
        REQUESTS,
        HISTORY,
        SOUL_SYNC
    }

    sealed class Action {
        data class OnTabSelected(val tab: Tab) : Action()
        data class OnFriendClick(val friend: Friend) : Action()
        data class OnAcceptRequest(val friendshipId: String) : Action()
        data class OnRejectRequest(val friendshipId: String) : Action()
        data class OnRemoveFriend(val friendshipId: String) : Action()
        object OnConfirmRemoveFriend : Action()
        object OnDismissRemoveFriendDialog : Action()
        data class OnRunCompatibilityTest(val friendUid: String) : Action()
        object OnGenerateInviteCode : Action()
        data class OnAcceptInviteCode(val code: String) : Action()
        object OnDismissInviteDialog : Action()
        object OnDismissTestResultDialog : Action()
        data class OnStartSoulSync(val friend: Friend) : Action()
        data class OnDeleteTest(val testId: String) : Action() // 👈 YENİ
    }

    sealed class Effect {
        data class ShowToast(val message: UiText) : Effect()
        data class ShowTestResult(val result: CompatibilityTestResult) : Effect()
        data class NavigateToSoulSync(val roomId: String) : Effect()
        data object ShowPremiumGate : Effect()
    }
}