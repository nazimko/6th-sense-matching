package com.mhmtn.a6thsense.conversations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val repository: ConversationRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val auth: FirebaseAuth
) : ViewModel() {// Source code removed.}
