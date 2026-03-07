package com.mhmtn.a6thsense.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.onboarding.domain.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: OnboardingRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    init {
        analyticsHelper.logOnboardingStarted() // 👈
    }
    private val _effect = MutableSharedFlow<OnboardingContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    fun onAction(action: OnboardingContract.Action) {
        when (action) {
            OnboardingContract.Action.Complete -> {
                viewModelScope.launch {
                    repository.completeOnboarding()
                    analyticsHelper.logOnboardingCompleted()
                    _effect.emit(OnboardingContract.Effect.NavigateToAuth)
                }
            }
        }
    }
}