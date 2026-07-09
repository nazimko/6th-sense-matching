package com.mhmtn.a6thsense.settings.presentation

import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.settings.domain.UserSettings

object SettingsContract {

    data class State(
        val settings: UserSettings = UserSettings(),
        val isLoading: Boolean = false,
        val isEditingName: Boolean = false,
        val nameInput: String = "",
        val showInDiscover: Boolean = true,
        val isSavingName: Boolean = false,
        val showLogoutDialog: Boolean = false,
        val isLoggingOut: Boolean = false,
        val error: String? = null
    )

    sealed class Action {
        data class UpdateTheme(val isDark: Boolean) : Action()
        data class UpdateMatchNotifications(val enabled: Boolean) : Action()
        data class UpdateMessageNotifications(val enabled: Boolean) : Action()
        object StartEditingName : Action()
        object OnContactUsClick : Action()
        data class TypeName(val name: String) : Action()
        object SaveName : Action()
        object CancelEditingName : Action()
        object OnShowInDiscoverToggle : Action()
        object OnLogoutClick : Action()
        object OnLogoutConfirm : Action()
        object OnLogoutDismiss : Action()
    }

    sealed class Effect {
        data class ShowMessage(val message: UiText) : Effect()
        object NavigateToContactUs : Effect()
        object NavigateToLogin : Effect()

    }
}