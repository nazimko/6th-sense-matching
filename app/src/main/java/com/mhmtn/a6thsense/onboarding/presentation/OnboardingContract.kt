package com.mhmtn.a6thsense.onboarding.presentation

import com.mhmtn.a6thsense.R

object OnboardingContract {

    sealed class Action {
        object Complete : Action()
    }

    sealed class Effect {
        object NavigateToAuth : Effect()
    }
}

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String,
    val gradientColors: List<Long>
)

val onboardingPages = listOf(
    OnboardingPage(
        emoji = "🔮",
        title = R.string.onboarding_1.toString(),
        description = R.string.onboarding_1_desc.toString(),
        gradientColors = listOf(0xFF0F0C29, 0xFF302B63, 0xFF24243E)
    ),
    OnboardingPage(
        emoji = "✨",
        title = R.string.onboarding_2.toString(),
        description = R.string.onboarding_2_desc.toString(),
        gradientColors = listOf(0xFF1A1A2E, 0xFF16213E, 0xFF0F3460)
    ),
    OnboardingPage(
        emoji = "💫",
        title = R.string.onboarding_3.toString(),
        description = R.string.onboarding_3_desc.toString(),
        gradientColors = listOf(0xFF0F0C29, 0xFF1A1A2E, 0xFF7B5EA7)
    )
)