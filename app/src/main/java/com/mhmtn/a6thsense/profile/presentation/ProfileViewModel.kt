package com.mhmtn.a6thsense.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        load()
    }

    private fun load() {
        val firebaseUser = auth.currentUser ?: return

        val user = AuthUser(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "User",
            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
        )

        _state.update { it.copy(user = user) }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val stats = repository.getProfileStats(firebaseUser.uid)
                val isPremium = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                    .getBoolean("isPremium") ?: false

                val badges = repository.getBadges(stats, isPremium)
                val newlyUnlocked = badges.filter { it.isUnlocked }
                if (newlyUnlocked.isNotEmpty()) {
                    _effect.emit(ProfileContract.Effect.TriggerConfetti)
                }

                _state.update {
                    it.copy(
                        stats = stats, badges = badges, isPremium = isPremium, isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onAction(action: ProfileContract.Action) {
        when (action) {
            ProfileContract.Action.Load -> load()
            is ProfileContract.Action.OpenSheet -> {
                // Milestone kontrolü
                if (action.type == ProfileContract.BottomSheetType.BADGES) {
                    val hasNewBadge = _state.value.badges.any { it.isUnlocked }
                    if (hasNewBadge) {
                        viewModelScope.launch {
                            _effect.emit(ProfileContract.Effect.TriggerConfetti)
                        }
                    }
                }
                _state.update { it.copy(activeSheet = action.type) }
            }

            ProfileContract.Action.CloseSheet -> {
                _state.update { it.copy(activeSheet = null) }
            }

            is ProfileContract.Action.TriggerConfetti -> {
                viewModelScope.launch {
                    _effect.emit(ProfileContract.Effect.TriggerConfetti)
                }
            }

            ProfileContract.Action.NavigateToMatchHistory -> {
                viewModelScope.launch {
                    _effect.emit(ProfileContract.Effect.NavigateToMatchHistory)
                }
            }
            ProfileContract.Action.onInviteClick -> {
                viewModelScope.launch {
                    _effect.emit(ProfileContract.Effect.NavigateToInvite)
                }
            }
            ProfileContract.Action.NavigateToFriends -> {
                viewModelScope.launch {
                    _effect.emit(ProfileContract.Effect.NavigateToFriends)
                }
            }
        }
    }
}