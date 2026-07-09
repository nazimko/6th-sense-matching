package com.mhmtn.a6thsense.friends.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository,
    private val auth: FirebaseAuth,
    private val premiumRepository: PremiumRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel(){// Source code removed.}