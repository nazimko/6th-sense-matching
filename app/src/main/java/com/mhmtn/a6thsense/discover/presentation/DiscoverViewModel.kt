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
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DiscoverContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        load()
    }

    private fun load() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val users = repository.getActiveUsers(uid)
                _state.update {
                    it.copy(
                        users = users,
                        currentIndex = 0,
                        isLoading = false,
                        isEmpty = users.isEmpty(),
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Unknown error", isLoading = false)
                }
            }
        }
    }

    fun onAction(action: DiscoverContract.Action) {
        when (action) {
            DiscoverContract.Action.SwipeLeft -> {
                viewModelScope.launch {
                    val uid = auth.currentUser?.uid ?: return@launch

                    val premiumStatus = premiumRepository
                        .getPremiumStatus(uid)
                        .first()

                    if (!premiumStatus.isPremium &&
                        premiumStatus.dailySwipesUsed >= premiumStatus.dailySwipeLimit
                    ) {
                        analyticsHelper.logDiscoverLimitReached()
                        _effect.emit(DiscoverContract.Effect.ShowPaywall)
                        return@launch
                    }

                    val currentUser = _state.value.users
                        .getOrNull(_state.value.currentIndex)

                    currentUser?.let { repository.recordSwipe(uid, it.uid) }

                    premiumRepository.incrementSwipeCount(uid)
                    _state.update { it.copy(currentIndex = it.currentIndex + 1) }
                    analyticsHelper.logDiscoverSwipeLeft()
                }
            }

            DiscoverContract.Action.SwipeRight -> {
                viewModelScope.launch {
                    val uid = auth.currentUser?.uid ?: return@launch

                    val premiumStatus = premiumRepository
                        .getPremiumStatus(uid)
                        .first()

                    if (!premiumStatus.isPremium &&
                        premiumStatus.dailySwipesUsed >= premiumStatus.dailySwipeLimit
                    ) {
                        analyticsHelper.logDiscoverLimitReached()
                        _effect.emit(DiscoverContract.Effect.ShowPaywall)
                        return@launch
                    }

                    val currentUser = _state.value.users
                        .getOrNull(_state.value.currentIndex) ?: return@launch

                    _state.update { it.copy(isLoadingConversation = true) }
                    try {
                        // 👇 Swipe'ı kaydet
                        repository.recordSwipe(uid, currentUser.uid)
                        premiumRepository.incrementSwipeCount(uid)

                        val conversationId = repository.getOrCreateConversation(
                            currentUserId = uid,
                            matchedUserId = currentUser.uid
                        )

                        _state.update {
                            it.copy(
                                isLoadingConversation = false,
                                currentIndex = it.currentIndex + 1
                            )
                        }

                        _effect.emit(
                            DiscoverContract.Effect.NavigateToMessaging(
                                conversationId = conversationId,
                                matchedUserName = currentUser.name,
                                matchedUserPhotoUrl = currentUser.photoUrl,
                                matchedUserId = currentUser.uid
                            )
                        )
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoadingConversation = false) }
                    }
                    analyticsHelper.logDiscoverSwipeRight()
                }
            }
            DiscoverContract.Action.Reload -> load()
        }
    }
}