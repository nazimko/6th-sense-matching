package com.mhmtn.a6thsense.friends.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository,
    private val auth: FirebaseAuth,
    private val premiumRepository: PremiumRepository,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(FriendsContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FriendsContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        loadPremiumStatus()
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

            FriendsContract.Action.OnConfirmRemoveFriend -> {
                confirmRemoveFriend()
            }

            FriendsContract.Action.OnDismissRemoveFriendDialog -> {
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

            is FriendsContract.Action.OnStartSoulSync -> {
                startSoulSync(action.friend.uid)
            }

            is FriendsContract.Action.OnDeleteTest -> {
                deleteTest(action.testId)
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
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.StringResource(R.string.accepted_friend)))
            }.onFailure { e ->
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _effect.emit(FriendsContract.Effect.ShowToast(message))
            }
        }
    }

    private fun rejectRequest(friendshipId: String) {
        viewModelScope.launch {
            repository.rejectFriendRequest(friendshipId).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.StringResource(R.string.rejected_friend)))
            }.onFailure { e ->
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _effect.emit(FriendsContract.Effect.ShowToast(message))
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
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.StringResource(R.string.removed_friend)))
            }.onFailure { e ->
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _effect.emit(FriendsContract.Effect.ShowToast(message))
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
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _effect.emit(FriendsContract.Effect.ShowToast(message))
                _state.update { it.copy(isLoading = false) }
            }
        }
    }


    private fun acceptInviteCode(code: String) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            repository.acceptInviteCode(code, uid).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.StringResource(R.string.send_friend_request)))
                _state.update { it.copy(showInviteDialog = false) }
            }.onFailure { e ->
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_invalid_invite_code)
                _effect.emit(FriendsContract.Effect.ShowToast(message))
            }
        }
    }

    private fun startSoulSync(friendUid: String) {
        viewModelScope.launch {
            val myUid = auth.currentUser?.uid ?: return@launch
            val premiumStatus = _state.value.premiumStatus

            // Limit kontrolü — premium değilse ve hakkı bittiyse gate göster
            if (!premiumStatus.isPremium &&
                premiumStatus.dailySoulSyncUsed >= premiumStatus.dailySoulSyncLimit
            ) {
                _effect.emit(FriendsContract.Effect.ShowPremiumGate)
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            // Premium değilse kullanımı say
            if (!premiumStatus.isPremium) {
                premiumRepository.incrementSoulSyncCount(myUid)
            }

            repository.startSoulSyncWithFriend(myUid, friendUid).onSuccess { roomId ->
                _state.update { it.copy(isLoading = false) }
                _effect.emit(FriendsContract.Effect.NavigateToSoulSync(roomId))
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false) }
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.DynamicString(e.message ?: "Error")))
            }
        }
    }

    private fun deleteTest(testId: String) {
        viewModelScope.launch {
            repository.deleteCompatibilityTest(testId).onSuccess {
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.StringResource(R.string.deleted_successfully)))
            }.onFailure { e ->
                _effect.emit(FriendsContract.Effect.ShowToast(UiText.StringResource(R.string.error_occurred)))
            }
        }
    }

    private fun loadPremiumStatus() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            premiumRepository.getPremiumStatus(uid).collect { status ->
                _state.update { it.copy(premiumStatus = status) }
            }
        }
    }
}