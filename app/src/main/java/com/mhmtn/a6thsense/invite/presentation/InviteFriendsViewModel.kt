package com.mhmtn.a6thsense.invite.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsEvent
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.invite.domain.InviteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteFriendsViewModel @Inject constructor(
    private val repository: InviteRepository,
    private val auth: FirebaseAuth,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {// Source code removed.}