package com.mhmtn.a6thsense.friends.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository,
    private val auth: FirebaseAuth,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(FriendsContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FriendsContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        loadFriends()
        loadPendingRequests()
        loadCompatibilityHistory()
        loadInviteCode()
    }

    fun onAction(action: FriendsContract.Action) {
        when (action) {
            is FriendsContract.Action.OnTabSelected -> {
                _state.update { it.copy(selectedTab = action.tab) }
            }

            is FriendsContract.Action.OnFriendClick -> {
                // Navigate to friend profile or test
            }

            is FriendsContract.Action.OnAcceptRequest -> {
                acceptRequest(action.friendshipId)
            }

            is FriendsContract.Action.OnRejectRequest -> {
                rejectRequest(action.friendshipId)
            }

            is FriendsContract.Action.OnRemoveFriend -> {
                val friend = _state.value.friends.find { it.friendshipId == action.friendshipId }
                _state.update {
                    it.copy(
                        friendToRemove = friend,
                        showRemoveFriendDialog = true
                    )
                }
            }

            FriendsContract.Action.OnConfirmRemoveFriend -> { // 👈 YENİ
                confirmRemoveFriend()
            }

            FriendsContract.Action.OnDismissRemoveFriendDialog -> { // 👈 YENİ
                _state.update {
                    it.copy(
                        friendToRemove = null,
                        showRemoveFriendDialog = false
                    )
                }
            }

            is FriendsContract.Action.OnRunCompatibilityTest -> {
                runCompatibilityTest(action.friendUid)
            }

            FriendsContract.Action.OnGenerateInviteCode -> {
                _state.update {
                    it.copy(showInviteDialog = true)
                }
            }

            is FriendsContract.Action.OnAcceptInviteCode -> {
                acceptInviteCode(action.code)
            }

            FriendsContract.Action.OnDismissInviteDialog -> {
                _state.update { it.copy(showInviteDialog = false) }
            }

            FriendsContract.Action.OnDismissTestResultDialog -> {
                _state.update { it.copy(showTestResultDialog = false, testResult = null) }
            }
        }
    }

    private fun loadFriends() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            repository.getFriends(uid).collect { friends ->
                _state.update { it.copy(friends = friends, isLoading = false) }
            }
        }
    }

    private fun loadPendingRequests() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            repository.getPendingRequests(uid).collect { requests ->
                _state.update { it.copy(pendingRequests = requests) }
            }
        }
    }

    private fun loadCompatibilityHistory() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            repository.getCompatibilityHistory(uid).collect { history ->
                _state.update { it.copy(compatibilityHistory = history) }
            }
        }
    }

    private fun loadInviteCode() {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val code = repository.getOrCreateInviteCode(uid)

                _state.update { it.copy(inviteCode = code) }
                Log.d("FriendsVM", "Invite code loaded: $code")
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error loading invite code: ${e.message}", e)
            }
        }
    }

    private fun acceptRequest(friendshipId: String) {
        viewModelScope.launch {
            repository.acceptFriendRequest(friendshipId).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(R.string.accepted_friend.toString()))
            }.onFailure { e ->
                _effect.emit(FriendsContract.Effect.ShowToast(e.message ?: R.string.error_occurred.toString()))
            }
        }
    }

    private fun rejectRequest(friendshipId: String) {
        viewModelScope.launch {
            repository.rejectFriendRequest(friendshipId).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(R.string.rejected_friend.toString()))
            }.onFailure { e ->
                _effect.emit(FriendsContract.Effect.ShowToast(e.message ?: R.string.error_occurred.toString()))
            }
        }
    }

    private fun confirmRemoveFriend() {
        viewModelScope.launch {
            val friendshipId = _state.value.friendToRemove?.friendshipId ?: return@launch

            _state.update {
                it.copy(
                    friendToRemove = null,
                    showRemoveFriendDialog = false
                )
            }

            repository.removeFriend(friendshipId).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(R.string.removed_friend.toString()))
            }.onFailure { e ->
                _effect.emit(FriendsContract.Effect.ShowToast(e.message ?: R.string.error_occurred.toString()))
            }
        }
    }

    private fun runCompatibilityTest(friendUid: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.runCompatibilityTest(
                auth.currentUser?.uid ?: return@launch,
                friendUid
            ).onSuccess { result ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        showTestResultDialog = true,
                        testResult = result
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false) }
                _effect.emit(FriendsContract.Effect.ShowToast(e.message ?: R.string.error_occurred.toString()))
            }
        }
    }


    private fun acceptInviteCode(code: String) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            repository.acceptInviteCode(code, uid).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(R.string.send_friend_request.toString()))
                _state.update { it.copy(showInviteDialog = false) }
            }.onFailure { e ->
                _effect.emit(FriendsContract.Effect.ShowToast(e.message ?: R.string.error_invalid_invite_code.toString()))
            }
        }
    }
}