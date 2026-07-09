package com.mhmtn.a6thsense.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.home.domain.HomeRepository
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val matchingRepository: MatchingRepository,
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository // ✅ Added
) : ViewModel() {// Source code removed.}