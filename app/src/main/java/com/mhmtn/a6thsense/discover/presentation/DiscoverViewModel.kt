package com.mhmtn.a6thsense.discover.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.discover.domain.DiscoverRepository
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: DiscoverRepository,
    private val premiumRepository: PremiumRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val auth: FirebaseAuth
) : ViewModel(){// Source code removed.}