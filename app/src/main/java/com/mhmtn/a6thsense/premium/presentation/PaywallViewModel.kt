package com.mhmtn.a6thsense.premium.presentation

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.billing.data.BillingRepositoryImpl
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val analyticsHelper: AnalyticsHelper,
    private val billingRepository: BillingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {// Source code removed.}