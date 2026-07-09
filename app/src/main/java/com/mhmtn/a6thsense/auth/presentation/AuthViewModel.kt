package com.mhmtn.a6thsense.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.messaging.FirebaseMessaging
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val billingRepository: BillingRepository,
    private val analyticsHelper: AnalyticsHelper,
    val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    private val _state = MutableStateFlow(AuthContract.State())
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<AuthContract.Effect>()
    val effect = _effect.asSharedFlow()


    fun onAction(action: AuthContract.Action) {
        when (action) {
            is AuthContract.Action.GoogleSignIn -> signIn(action.idToken)
        }
    }

    private fun signIn(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val user = repository.signInWithGoogle(idToken)

            _state.update {
                it.copy(
                    isLoading = false,
                    user = user
                )
            }
            billingRepository.checkSubscriptionStatus()

            analyticsHelper.setUserId(user.uid)
            analyticsHelper.logSignUp("google")
            _effect.emit(AuthContract.Effect.NavigateHome)
        }
    }
}