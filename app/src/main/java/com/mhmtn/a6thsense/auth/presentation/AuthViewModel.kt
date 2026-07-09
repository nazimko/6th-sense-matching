package com.mhmtn.a6thsense.auth.presentation

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val billingRepository: BillingRepository,
    private val analyticsHelper: AnalyticsHelper,
    val googleSignInClient: GoogleSignInClient
) : ViewModel() {// Source code removed.}