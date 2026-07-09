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

    sealed class Action {// Source code removed.}

    sealed class Effect {// Source code removed.}
}