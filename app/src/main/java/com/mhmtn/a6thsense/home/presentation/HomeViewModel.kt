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
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    private var todayMatchesJob: Job? = null
    private var todaySessionsJob: Job? = null
    private var homeJob: Job? = null


    init {
        observeAuthState()
        observePremiumStatus()
        observeSettings() // ✅ Added
        checkTodaysSessions()
        loadTodayMatches()
    }

    private fun observePremiumStatus() {
        viewModelScope.launch {
            billingRepository.isPremium.collect { isPremium ->
                _state.update { it.copy(isPremium = isPremium) }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _state.update { it.copy(minSimilarity = settings.minSimilarity) }
            }
        }
    }

    fun onAction(action: HomeAction) {
        viewModelScope.launch {
            when (action) {
                HomeAction.Load -> loadHome()
                
                is HomeAction.OnMatchClick -> {
                    val match = if (action.matchId != null) {
                        _state.value.todayMatches.find { it.matchId == action.matchId }
                    } else {
                        _state.value.todayMatches.firstOrNull()
                    }

                    if (match != null) {
                        Log.d("HomeVM", "match clicked: ${match.matchId}")
                        Log.d("HomeVM", "similarity: ${match.similarity}")
                        _effect.emit(
                            HomeEffect.NavigateToSimilarity(
                                matchId = match.matchId,
                                otherUserName = match.userName,
                                otherUserPhoto = match.userPhoto,
                                similarity = match.similarity
                            )
                        )
                    }
                }

                is HomeAction.OnStartSession -> {
                    // ✅ threshold değerini efekt ile gönderiyoruz
                    _effect.emit(HomeEffect.NavigateToSession(action.type, _state.value.minSimilarity))
                }

                HomeAction.OnStartDailyClick -> {
                    _effect.emit(HomeEffect.NavigateToDaily)
                }

                HomeAction.OnSettingsClick -> {
                    _effect.emit(HomeEffect.NavigateToSettings)
                }

                HomeAction.OnUpgradeClick -> {
                    _effect.emit(HomeEffect.NavigateToPaywall)
                }

                is HomeAction.OnThresholdChange -> {
                    // ✅ Update DataStore
                    settingsRepository.updateMinSimilarity(action.value)
                    Log.d("HomeVM", "threshold changed: ${action.value}")
                }
            }
        }
    }

    private fun loadTodayMatches() {
        todayMatchesJob?.cancel()
        todayMatchesJob = viewModelScope.launch {
            try {
                val uid = authRepository.currentUser()?.uid ?: return@launch
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                matchingRepository.getTodayMatches(uid, today).collect { matches ->
                    _state.update { it.copy(todayMatches = matches) }

                    if (matches.isNotEmpty()) {
                        _state.update {
                            it.copy(
                                matchedUser = AuthUser(
                                    uid = matches[0].userId,
                                    name = matches[0].userName,
                                    photoUrl = matches[0].userPhoto
                                ),
                                similarity = matches[0].similarity
                            )
                        }
                    }
                    Log.d("HomeVM", "today matches: ${matches.size}")
                    Log.d("HomeVM", "today matches: ${matches[0].userName}")
                    Log.d("HomeVM", "today matches: ${matches[0].similarity}")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("HomeVM", "Error loading today matches: ${e.message}", e)
            }
        }
    }

    private fun checkTodaysSessions() {
        todaySessionsJob?.cancel()
        todaySessionsJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val uid = authRepository.currentUser()?.uid ?: return@launch
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val sessions = repository.getTodaysSessions(uid, today)

                _state.update {
                    it.copy(
                        hasCompletedIntuitionToday = sessions.any { s -> s.type == DailyActivityContract.SessionType.INTUITION },
                        hasCompletedPreferenceToday = sessions.any { s -> s.type == DailyActivityContract.SessionType.PREFERENCE },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _state.update { it.copy(isLoading = false, error = message) }
            }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authStateChanges().collect { user ->
                if (user == null) {
                    todayMatchesJob?.cancel()
                    todaySessionsJob?.cancel()
                    homeJob?.cancel()
                }
            }
        }
    }

    private fun loadHome() {
        homeJob?.cancel()
        homeJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val uid = authRepository.currentUser()?.uid
                if (uid == null) {
                    _state.update { it.copy(isLoading = false, error = UiText.StringResource(R.string.no_session)) }
                    return@launch
                }

                //val similarity = repository.getSimilarity()
                val streak = repository.getCurrentStreak(uid)
                val completedToday = repository.isCompletedToday(uid)
                val isPremium = repository.isPremium(uid)

                _state.update {
                    it.copy(
                        isLoading = false,
                      //  similarity = similarity,
                        currentStreak = streak,
                        completedToday = completedToday,
                        isPremium = isPremium,
                        error = null
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _state.update { it.copy(isLoading = false, error = message) }
            }
        }
    }
}
