package com.mhmtn.a6thsense.home.presentation

import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

sealed interface HomeAction {
    data object Load : HomeAction
    data class OnMatchClick(val matchId: String? = null) : HomeAction
    data object OnStartDailyClick : HomeAction
    data object OnLogoutClick : HomeAction
    data object OnSettingsClick : HomeAction
    object OnUpgradeClick : HomeAction
    data class OnStartSession(val type: DailyActivityContract.SessionType) : HomeAction
}
