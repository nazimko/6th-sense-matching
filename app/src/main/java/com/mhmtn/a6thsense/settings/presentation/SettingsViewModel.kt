package com.mhmtn.a6thsense.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel(){// Source code removed.}