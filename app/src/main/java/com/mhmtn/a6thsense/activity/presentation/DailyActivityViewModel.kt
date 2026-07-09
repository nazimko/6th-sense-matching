package com.mhmtn.a6thsense.activity.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.activity.data.MatchingRepositoryImpl
import com.mhmtn.a6thsense.activity.domain.QuestionRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DailyActivityViewModel @Inject constructor(
    private val repository: MatchingRepositoryImpl,
    private val questionRepository: QuestionRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {// Source code removed.}