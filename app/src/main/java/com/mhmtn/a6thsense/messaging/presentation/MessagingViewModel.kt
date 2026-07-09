package com.mhmtn.a6thsense.messaging.presentation

import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.messaging.domain.MessagingRepository
import com.mhmtn.a6thsense.messaging.domain.model.Message
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val repository: MessagingRepository,
    private val firestore: FirebaseFirestore,
    private val analyticsHelper: AnalyticsHelper,
    private val matchingRepository: MatchingRepository,
    private val premiumRepository: PremiumRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel(){// Source code removed.}