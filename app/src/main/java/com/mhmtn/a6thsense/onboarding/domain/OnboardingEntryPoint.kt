package com.mhmtn.a6thsense.onboarding.domain

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface OnboardingEntryPoint {
    fun onboardingRepository(): OnboardingRepository
}