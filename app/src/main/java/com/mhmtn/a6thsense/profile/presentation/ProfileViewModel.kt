package com.mhmtn.a6thsense.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    val state = _state
        .onStart { load() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000),
            ProfileContract.State()
        )

    private val _effect = MutableSharedFlow<ProfileContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    // Oturum boyunca bir rozet artışı için sadece bir kez konfeti göstermek için
    private var hasShownConfettiForCurrentLoad = false

    init {
        viewModelScope.launch {
            auth.addAuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser != null) {
                    load()
                }
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val imageUrl = repository.uploadProfileImage(userId = userId, imageUri = uri)
                repository.updateProfileImageUrl(userId = userId, imageUrl = imageUrl)
                val updatedUser = _state.value.user?.copy(photoUrl = imageUrl)
                _state.update { it.copy(user = updatedUser, isLoading = false) }
                _effect.emit(ProfileContract.Effect.ProfileImageUploaded(UiText.StringResource(R.string.profile_image_uploaded)))
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun load() {
        val firebaseUser = auth.currentUser ?: return
        
        _state.update { it.copy(isLoading = _state.value.user == null) }

        viewModelScope.launch {
            try {
                val stats = repository.getProfileStats(firebaseUser.uid)
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get(Source.SERVER).await()

                val isPremium = userDoc.getBoolean("isPremium") ?: false
                val customPhotoUrl = userDoc.getString("profileImageUrl")
                val resolvedPhotoUrl = customPhotoUrl ?: firebaseUser.photoUrl?.toString() ?: ""

                val badges = repository.getBadges(stats, isPremium)
                
                // 🔥 Yeni Konfeti Mantığı: 
                // Firestore'da saklanan son görülen rozet sayısını kontrol et.
                val unlockedCount = badges.count { it.isUnlocked }
                val lastSeenBadgeCount = userDoc.getLong("lastSeenBadgeCount") ?: 0L
                
                if (unlockedCount > lastSeenBadgeCount && !hasShownConfettiForCurrentLoad) {
                    hasShownConfettiForCurrentLoad = true
                    _effect.emit(ProfileContract.Effect.TriggerConfetti)
                    
                    // Firestore'u hemen güncelle ki bir sonraki girişte (başka rozet açılana kadar) patlamasın.
                    firestore.collection("users").document(firebaseUser.uid)
                        .update("lastSeenBadgeCount", unlockedCount)
                }

                _state.update {
                    it.copy(
                        user = AuthUser(firebaseUser.uid, userDoc.getString("name") ?: firebaseUser.displayName ?: "", resolvedPhotoUrl),
                        stats = stats,
                        badges = badges,
                        isPremium = isPremium,
                        isLoading = false,
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
                _state.update { it.copy(activeSheet = action.type) }
            }
            ProfileContract.Action.CloseSheet -> _state.update { it.copy(activeSheet = null) }
            is ProfileContract.Action.TriggerConfetti -> {
                viewModelScope.launch { _effect.emit(ProfileContract.Effect.TriggerConfetti) }
            }
            is ProfileContract.Action.NavigateToMatchHistory -> {
                viewModelScope.launch { _effect.emit(ProfileContract.Effect.NavigateToMatchHistory) }
            }
            is ProfileContract.Action.onInviteClick -> {
                viewModelScope.launch { _effect.emit(ProfileContract.Effect.NavigateToInvite) }
            }
            is ProfileContract.Action.NavigateToFriends -> {
                viewModelScope.launch { _effect.emit(ProfileContract.Effect.NavigateToFriends) }
            }
            is ProfileContract.Action.Error -> {
                viewModelScope.launch { _effect.emit(ProfileContract.Effect.ProfileImageUploaded(action.message)) }
            }
        }
    }
}
