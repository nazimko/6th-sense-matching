package com.mhmtn.a6thsense.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.onboarding.domain.OnboardingEntryPoint
import com.mhmtn.a6thsense.onboarding.domain.OnboardingRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

@Composable
fun AppStartRoute(
    navController: NavHostController,
    authRepository: AuthRepository = EntryPointAccessors
        .fromApplication(
            LocalContext.current.applicationContext,
            AuthEntryPoint::class.java
        )
        .authRepository(),
    onboardingRepository: OnboardingRepository = EntryPointAccessors
        .fromApplication(
            LocalContext.current.applicationContext,
            OnboardingEntryPoint::class.java
        )
        .onboardingRepository()
) {// Source code removed.}
