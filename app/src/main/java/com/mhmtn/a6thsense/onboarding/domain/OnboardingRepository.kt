package com.mhmtn.a6thsense.onboarding.domain

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun hasCompletedOnboarding(): Flow<Boolean>
    suspend fun completeOnboarding()
}