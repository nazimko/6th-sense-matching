package com.mhmtn.a6thsense.conversations.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val repository: ConversationRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationListContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ConversationListContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            analyticsHelper.logConversationOpened()
            repository.getConversations(uid)
                .catch { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { conversations ->
                    _state.update {
                        it.copy(conversations = conversations, isLoading = false, error = null)
                    }
                }
        }
    }

    fun onAction(action: ConversationListContract.Action) {
        when (action) {
            is ConversationListContract.Action.OnConversationClick -> {
                viewModelScope.launch {
                    _effect.emit(
                        ConversationListContract.Effect.NavigateToMessaging(
                            conversationId = action.item.conversationId,
                            matchedUserName = action.item.otherUserName,
                            matchedUserPhotoUrl = action.item.otherUserPhotoUrl,
                            matchedUserId = action.item.otherUserId
                        )
                    )
                }
            }
            is ConversationListContract.Action.OnDeleteConversation -> {
                _state.update { it.copy(conversationToDelete = action.item) }
            }
            ConversationListContract.Action.ConfirmDelete -> {
                val conversation = _state.value.conversationToDelete ?: return
                val uid = auth.currentUser?.uid ?: return
                
                viewModelScope.launch {
                    repository.deleteConversation(uid, conversation.conversationId)
                    _state.update { it.copy(conversationToDelete = null) }
                }
            }
            ConversationListContract.Action.DismissDeleteDialog -> {
                _state.update { it.copy(conversationToDelete = null) }
            }
            ConversationListContract.Action.Reload -> loadConversations()
        }
    }
}
