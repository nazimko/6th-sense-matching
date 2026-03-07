package com.mhmtn.a6thsense.matchhistory.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryRepository
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchHistoryViewModel @Inject constructor(
    private val repository: MatchHistoryRepository,
    private val friendsRepository: FriendsRepository,
    private val premiumRepository: PremiumRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(MatchHistoryContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MatchHistoryContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val uid = auth.currentUser?.uid ?: return@launch

                // 👇 Premium durumunu al
                val isPremium = premiumRepository.getPremiumStatus(uid).first().isPremium

                repository.getMatchHistory(uid, isPremium).collect { (matches, totalCount) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            matches = matches,
                            isPremium = isPremium,
                            totalCount = totalCount,
                            hasMoreMatches = !isPremium && totalCount > 1
                        )
                    }

                    Log.d("MatchHistoryVM", "Total: $totalCount, Showing: ${matches.size}, hasMoreMatches: ${!isPremium && totalCount > 1}")
                }
            } catch (e: Exception) {
                Log.e("MatchHistoryVM", "Error loading matches: ${e.message}", e)
                _state.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun onAction(action: MatchHistoryContract.Action) {
        when (action) {
            is MatchHistoryContract.Action.OnMessageClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(loadingConversationId = action.item.matchId)
                    }
                    try {
                        val conversationId = if (action.item.conversationId.isNotBlank()) {
                            action.item.conversationId
                        } else {
                            repository.getOrCreateConversation(
                                currentUserId = auth.currentUser?.uid ?: return@launch,
                                matchedUserId = action.item.matchedUserId
                            )
                        }

                        _effect.emit(
                            MatchHistoryContract.Effect.NavigateToMessaging(
                                conversationId = conversationId,
                                matchedUserName = action.item.matchedUserName,
                                matchedUserPhotoUrl = action.item.matchedUserPhotoUrl,
                                matchedUserId = action.item.matchedUserId
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        _state.update { it.copy(loadingConversationId = null) }
                    }
                }
            }

            MatchHistoryContract.Action.Reload -> loadHistory()

            MatchHistoryContract.Action.OnUpgradeToPremium -> { // 👈 Yeni action
                viewModelScope.launch {
                    _effect.emit(MatchHistoryContract.Effect.NavigateToPaywall)
                }
            }
            is MatchHistoryContract.Action.OnSendFriendRequest -> { // 👈 YENİ
                sendFriendRequest(action.matchedUserId)
            }
        }
    }

    private fun sendFriendRequest(toUid: String) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            friendsRepository.sendFriendRequest(uid, toUid).onSuccess {
                _effect.emit(MatchHistoryContract.Effect.ShowToast( R.string.send_friend_request.toString()))
            }.onFailure { e ->
                _effect.emit(MatchHistoryContract.Effect.ShowToast(e.message ?: R.string.error_occurred.toString()))
            }
        }
    }
}