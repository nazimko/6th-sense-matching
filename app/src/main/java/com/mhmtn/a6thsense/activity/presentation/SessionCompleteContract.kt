package com.mhmtn.a6thsense.activity.presentation

object SessionCompleteContract {
    
    data class State(
        val selectedDuration: Int = 1, // Default: 1 gün
        val isLoading: Boolean = false,
        val userIsPremium: Boolean = false
    )
    
    sealed interface Event {
        data class OnDurationChange(val duration: Int) : Event
        object OnConfirm : Event
    }
    
    sealed interface Effect {
        object NavigateHome : Effect
        data class ShowError(val message: String) : Effect
    }
}
