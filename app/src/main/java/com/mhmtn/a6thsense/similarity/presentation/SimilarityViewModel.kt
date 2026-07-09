package com.mhmtn.a6thsense.similarity.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.similarity.domain.SimilarityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SimilarityViewModel @Inject constructor(
    private val repository: SimilarityRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: String = savedStateHandle["matchId"] ?: ""
    private val initialSimilarity: Int = savedStateHandle.get<String>("similarity")?.toIntOrNull() ?: 0

    private val _state = MutableStateFlow(SimilarityContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SimilarityContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        loadMatchDetails()
        fetchRoomId()
    }

    private fun loadMatchDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                if (matchId.isBlank()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.StringResource(R.string.error_occurred)
                        )
                    }
                    return@launch
                }

                // Current user
                val currentUser = repository.getCurrentUser()
                // ✅ Artık matchId ile nokta atışı yapıyoruz
                val matchedUser = repository.getMatchedUser(matchId)

                // Similarity - parametreden al veya repository'den (ID ile)
                val similarity = if (initialSimilarity > 0) {
                    initialSimilarity
                } else {
                    repository.getSimilarity(matchId) ?: 0
                }

                Log.d("SimilarityVM", "Loaded: user=${matchedUser?.name}, similarity=$similarity")

                _state.update {
                    it.copy(
                        isLoading = false,
                        currentUser = currentUser,
                        matchedUser = matchedUser,
                        similarity = similarity
                    )
                }

                analyticsHelper.logMatchViewed(similarity)

            } catch (e: Exception) {
                Log.e("SimilarityVM", "Error loading match: ${e.message}", e)
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = message
                    )
                }
            }
        }
    }

    private fun fetchRoomId() {
        viewModelScope.launch {
            try {
                if (matchId.isBlank()) return@launch

                // ✅ Match ID ile direkt dökümanı alıyoruz
                val matchDoc = repository.getMatchDocument(matchId)
                val roomId = matchDoc?.getString("soulSyncRoomId")

                Log.d("SimilarityVM", "Match ID: $matchId, Room ID: $roomId")

                _state.update { it.copy(roomId = roomId) }

            } catch (e: Exception) {
                Log.e("SimilarityVM", "Error fetching room ID: ${e.message}", e)
            }
        }
    }

    fun onAction(action: SimilarityContract.Action) {
        when (action) {
            SimilarityContract.Action.Continue -> {
                viewModelScope.launch {
                    _effect.emit(SimilarityContract.Effect.NavigateHome)
                }
            }

            SimilarityContract.Action.OnMessageClick -> {
                val currentState = _state.value
                viewModelScope.launch {

                    var conversationId = currentState.conversationId

                    if (conversationId.isBlank()) {
                        conversationId = repository.createConversation(
                            currentUserId = currentState.currentUser?.uid ?: return@launch,
                            matchedUserId = currentState.matchedUser?.uid ?: return@launch
                        )

                        _state.update { it.copy(conversationId = conversationId) }
                    }

                    _effect.emit(
                        SimilarityContract.Effect.NavigateToMessaging(
                            conversationId = conversationId,
                            matchedUserName = _state.value.matchedUser?.name ?: "",
                            matchedUserPhotoUrl = _state.value.matchedUser?.photoUrl ?: "none",
                            matchedUserId = _state.value.matchedUser?.uid ?: ""
                        )
                    )
                }
            }
        }
    }
}
