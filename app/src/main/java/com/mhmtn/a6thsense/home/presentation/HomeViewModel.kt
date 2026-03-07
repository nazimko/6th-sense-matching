package com.mhmtn.a6thsense.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.home.domain.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val matchingRepository: MatchingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        checkTodaysSessions()
        loadTodayMatches()
    }

    fun onAction(action: HomeAction) {
        viewModelScope.launch {
            when (action) {

                HomeAction.Load -> loadHome()

                is HomeAction.OnMatchClick -> {
                    viewModelScope.launch {
                        val match = if (action.matchId != null) {
                            // Specific match
                            _state.value.todayMatches.find { it.matchId == action.matchId }
                        } else {
                            // İlk match (backward compatibility)
                            _state.value.todayMatches.firstOrNull()
                        }

                        if (match != null) {
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
                }


                is HomeAction.OnStartSession -> {
                    viewModelScope.launch {
                        Log.d("HomeVM", "Starting session: ${action.type}")
                        _effect.emit(HomeEffect.NavigateToSession(action.type))
                    }
                }

                HomeAction.OnStartDailyClick -> {
                    _effect.emit(HomeEffect.NavigateToDaily)
                }

                HomeAction.OnLogoutClick -> {
                    viewModelScope.launch {
                        authRepository.signOut()
                        _effect.emit(HomeEffect.NavigateToAuth)
                    }
                }

                HomeAction.OnSettingsClick -> {
                    viewModelScope.launch {
                        _effect.emit(HomeEffect.NavigateToSettings)
                    }
                }

                HomeAction.OnUpgradeClick -> {
                    viewModelScope.launch {
                        _effect.emit(HomeEffect.NavigateToPaywall)
                    }
                }
            }
        }
    }

    private fun loadTodayMatches() {
        viewModelScope.launch {
            try {
                val uid = authRepository.currentUser()?.uid ?: return@launch
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                Log.d("HomeVM", "=== loadTodayMatches Started ===")
                Log.d("HomeVM", "uid: $uid, today: $today")

                matchingRepository.getTodayMatches(uid, today).collect { matches ->

                    Log.d("HomeVM", "Received ${matches.size} matches from repository")

                    matches.forEachIndexed { index, match ->
                        Log.d("HomeVM", "Match $index: ${match.userName}, similarity=${match.similarity}%")
                    }

                    _state.update { it.copy(todayMatches = matches) }

                    // Backward compatibility - ilk match'i de set et
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
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Error loading today matches: ${e.message}", e)
            }
        }
    }

    private fun checkTodaysSessions() {
        viewModelScope.launch {
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
                Log.e("HomeVM", "Error checking sessions: ${e.message}", e)
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadHome() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val uid = authRepository.currentUser()?.uid
                if (uid == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = R.string.no_session.toString()
                        )
                    }
                    return@launch
                }

               // val matchedUser = repository.getMatchedUser()
                val similarity = repository.getSimilarity()

                val streak = repository.getCurrentStreak(uid)

                val completedToday = repository.isCompletedToday(uid)

                val isPremium = repository.isPremium(uid)

                _state.update {
                    it.copy(
                        isLoading = false,
                       // matchedUser = matchedUser,
                        similarity = similarity,
                        currentStreak = streak,
                        completedToday = completedToday,
                        isPremium = isPremium,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: R.string.error_occurred.toString()
                    )
                }
            }
        }
    }
}