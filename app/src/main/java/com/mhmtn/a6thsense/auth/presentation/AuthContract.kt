package com.mhmtn.a6thsense.auth.presentation

import com.mhmtn.a6thsense.auth.domain.AuthUser

object AuthContract {

    data class State(
        val isLoading: Boolean = false,
        val user: AuthUser? = null
    )

    sealed interface Action {
        data class GoogleSignIn(val idToken: String) : Action
    }

    sealed interface Effect {
        object NavigateHome : Effect
    }
}