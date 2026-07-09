package com.mhmtn.a6thsense.activity.domain

import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.core.presentation.UiText

object DailyActivityContract {

    data class State(
        val phase: Phase = Phase.PHASE_1,
        val step: Int = 0,
        val selections: List<String> = emptyList(),
        val type: SessionType = SessionType.INTUITION,
        val freeTextAnswers: Map<String, String> = emptyMap(),
        val currentSelection: Option? = null,
        val currentTextInput: String = "",
        val isPhaseTransition: Boolean = false,
        val questionSet: QuestionSet? = null,
        val isLoadingQuestions: Boolean = true,
        val minSimilarity: Int = 40,
        val selectedOptions: Map<String, String> = emptyMap()
    )

    sealed interface Action {
        data class OnOptionSelected(val questionId: String, val optionId: String) : Action
        data class SelectOption(val option: Option) : Action
        data class TypeText(val text: String) : Action
        data object SubmitTextAnswer : Action
        data object Reset : Action
        object Enter : Action
        object PhaseTransitionShown : Action
        object OnRefreshQuestions : Action
    }
    sealed interface Effect {
        data object NavigateToSimilarity : Effect
        object ShowNoMatch : Effect
        object ShowAlreadyCompleted : Effect
        data class ShowToast(val message: UiText) : Effect
    }

    enum class Phase {
        PHASE_1, // A/B choices (Intuition)
        PHASE_2, // Colors
        PHASE_3, // Animals
        PHASE_4, // Elements
        PHASE_5,  // Dimensions
        PHASE_6  // Free Text (Yorumlar)
    }

    enum class SessionType {
        INTUITION,    // Soul Sync (alt-üst, sağ-sol)
        PREFERENCE    // Daily Choices (firebase dynamic questions)
    }
}