package com.mhmtn.a6thsense.similarity.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
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
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: String = savedStateHandle["matchId"] ?: ""
    private val otherUserName: String = savedStateHandle["userName"] ?: ""
    private val otherUserPhoto: String = savedStateHandle["userPhoto"] ?: ""
    private val initialSimilarity: Int = savedStateHandle.get<String>("similarity")?.toIntOrNull() ?: 0

    private val _state = MutableStateFlow(SimilarityContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SimilarityContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        loadMatchDetails()
        //loadUsers()
        fetchRoomId()
        analyticsHelper.logMatchViewed(_state.value.similarity)
    }

    private fun loadMatchDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentUid = auth.currentUser?.uid ?: return@launch

                // Current user
                val currentUser = repository.getCurrentUser()
                val matchedUser = repository.getMatchedUser()

                // Match document'ten detayları al
                val matchDoc = firestore.collection("matches")
                    .whereArrayContains("participants", currentUid)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                matchDoc?.exists()?.let {
                    if (!it) {
                        Log.e("SimilarityVM", "Match not found: $matchId")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = R.string.home_no_matches.toString()
                            )
                        }
                        return@launch
                    }
                }

                val participants = matchDoc?.get("participants") as? List<String> ?: emptyList()
                val otherUid = participants.firstOrNull { it != currentUid } ?: ""

                // Similarity - parametreden al veya firestore'dan
                val similarity = if (initialSimilarity > 0) {
                    initialSimilarity
                } else {
                    matchDoc?.getLong("similarity")?.toInt() ?: 0
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
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    /*
    private fun loadUsers() {
        viewModelScope.launch {

            val currentUser = repository.getCurrentUser()
            val matchedUser = repository.getMatchedUser()

            val uid = auth.currentUser?.uid ?: return@launch

            val matchSnapshot = firestore
                .collection("matches")
                .whereArrayContains("participants", uid)
                .get()
                .await()

            //val similarity = repository.getSimilarity()

            val matchDoc = matchSnapshot.documents.firstOrNull()
            val similarity = matchDoc?.getLong("similarity")?.toInt() ?: 0

            /*
            val selectionSimilarity = matchDoc?.getLong("selectionSimilarity")?.toInt() ?: 0
            val soulSyncScore = matchDoc?.getLong("soulSyncScore")?.toInt() ?: 0
            val soulSyncCompleted = matchDoc?.getBoolean("soulSyncCompleted") ?: false
             */
            Log.d("SimilarityViewModel", "Current User: ${currentUser.name}")
            Log.d("SimilarityViewModel", "Matched User: ${matchedUser?.name}")


            _state.update{
                it.copy(
                    isLoading = false,
                    currentUser = currentUser,
                    matchedUser = matchedUser,
                    similarity = similarity // 👈 Final benzerlik
                   // selectionSimilarity = selectionSimilarity, // 👈 Opsiyonel: göstermek için
                   // soulSyncScore = soulSyncScore, // 👈 Opsiyonel
                    //soulSyncCompleted = soulSyncCompleted // 👈 Opsiyonel
                )
            }
        }
    }

     */

    private fun fetchRoomId() {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch

                Log.d("SimilarityVM", "Fetching room for uid: $uid")

                // Match document'ten direkt roomId al
                val matchDoc = firestore
                    .collection("matches")
                    .whereArrayContains("participants", uid)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                val matchId = matchDoc?.id
                val roomId = matchDoc?.getString("soulSyncRoomId") // 👈 Cloud Function ekliyor

                Log.d("SimilarityVM", "Match ID: $matchId")
                Log.d("SimilarityVM", "Room ID from Firestore: $roomId")

                _state.update { it.copy(roomId = roomId) }

            } catch (e: Exception) {
                Log.e("SimilarityVM", "Error fetching room ID: ${e.message}", e)
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val result = repository.getSimilarityResult()

            if (result != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        similarity = result.similarity,
                        currentUser = result.currentUser,
                        matchedUser = result.matchedUser
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
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
