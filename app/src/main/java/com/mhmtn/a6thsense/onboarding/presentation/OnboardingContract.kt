package com.mhmtn.a6thsense.onboarding.presentation

import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.UiText

object OnboardingContract {

    sealed class Action {
        object Complete : Action()
    }

    sealed class Effect {
        object NavigateToAuth : Effect()
    }
}

data class OnboardingPage(
    val icon: Int = R.mipmap.ic_launcher,
    val title: UiText,
    val description: UiText,
    val gradientColors: List<Long>,
    val lightGradientColors: List<Long>
)

val onboardingPages = listOf(
    OnboardingPage(
        title = UiText.StringResource( R.string.onboarding_1),
        description = UiText.StringResource(R.string.onboarding_1_desc),
        gradientColors = listOf(0xFF0F0C29, 0xFF302B63, 0xFF24243E),
        lightGradientColors = listOf(0xFFF8F5FF, 0xFFF0EBFF, 0xFFE8DEFF)
    ),
    OnboardingPage(
        title = UiText.StringResource(R.string.onboarding_2),
        description = UiText.StringResource(R.string.onboarding_2_desc),
        gradientColors = listOf(0xFF1A1A2E, 0xFF16213E, 0xFF0F3460),
        lightGradientColors = listOf(0xFFF8F5FF, 0xFFEBF0FF, 0xFFDDE8FF)
    ),
    OnboardingPage(
        title = UiText.StringResource(R.string.onboarding_3),
        description = UiText.StringResource(R.string.onboarding_3_desc),
        gradientColors = listOf(0xFF0F0C29, 0xFF1A1A2E, 0xFF7B5EA7),
        lightGradientColors = listOf(0xFFF8F5FF, 0xFFF0EBFF, 0xFFD4C5FF)
    )
)