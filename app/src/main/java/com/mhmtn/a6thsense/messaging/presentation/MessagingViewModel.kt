package com.mhmtn.a6thsense.messaging.presentation

import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.messaging.domain.MessagingRepository
import com.mhmtn.a6thsense.messaging.domain.model.Message
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val repository: MessagingRepository,
    private val firestore: FirebaseFirestore,
    private val analyticsHelper: AnalyticsHelper,
    private val matchingRepository: MatchingRepository,
    private val premiumRepository: PremiumRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String =
        Uri.decode(savedStateHandle.get<String>("conversationId") ?: "")
    private val matchedUserName: String =
        Uri.decode(savedStateHandle.get<String>("matchedUserName") ?: "")
    private val matchedUserPhotoUrl: String = run {
        val raw = savedStateHandle.get<String>("matchedUserPhotoUrl") ?: ""
        try {
            String(Base64.decode(raw, Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8)
        } catch (e: Exception) {
            Uri.decode(raw)
        }
    }

    private val _state = MutableStateFlow(
        MessagingContract.State(
            matchedUserName = matchedUserName,
            matchedUserPhotoUrl = matchedUserPhotoUrl
        )
    )
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MessagingContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        // 👇 Loglar metot içine taşındı
        Log.d("MessagingVM", "matchedUserName: $matchedUserName")
        Log.d("MessagingVM", "matchedUserPhotoUrl: $matchedUserPhotoUrl")

        if (conversationId.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val conversation = firestore.collection("conversations").document(conversationId).get().await()
                    val participants = conversation.get("participants") as? List<*>
                    val currentUid = auth.currentUser?.uid
                    val otherUid = participants?.firstOrNull { it != currentUid }?.toString() ?: ""
                    _state.update { it.copy(otherUserId = otherUid) }
                } catch (e: Exception) {
                    Log.e("MessagingVM", "Error getting otherUserId: ${e.message}")
                }
            }
            observeMessages()
            markAsRead()
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            var isFirstLoad = true
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getMessages(conversationId)
                .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                .collect { messages ->
                    val currentUserId = auth.currentUser?.uid
                    if (!isFirstLoad && messages.size > _state.value.messages.size) {
                        val newMessage = messages.last()
                        if (newMessage.senderId != currentUserId) {
                            _effect.emit(MessagingContract.Effect.PlayIncomingMessageSound)
                        }
                    }
                    isFirstLoad = false
                    _state.update { it.copy(messages = messages, isLoading = false, error = null) }
                    _effect.emit(MessagingContract.Effect.ScrollToBottom)
                }
        }
    }

    private fun markAsRead() {
        viewModelScope.launch {
            try { repository.markAsRead(conversationId, auth.currentUser?.uid ?: "") } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun onAction(action: MessagingContract.Action) {
        when (action) {
            is MessagingContract.Action.TypeMessage -> _state.update { it.copy(currentInput = action.text) }
            MessagingContract.Action.SendMessage -> {
                val messageText = _state.value.currentInput.trim()
                if (messageText.isBlank()) return
                viewModelScope.launch {
                    try {
                        val currentUid = auth.currentUser?.uid ?: return@launch
                        _state.update { it.copy(isSendingMessage = true) }
                        val premiumStatus = premiumRepository.getPremiumStatus(currentUid).first()
                        if (!premiumStatus.isPremium && premiumStatus.dailyMessagesUsed >= premiumStatus.dailyMessageLimit) {
                            analyticsHelper.logMessageLimitReached()
                            _state.update { it.copy(isSendingMessage = false) }
                            _effect.emit(MessagingContract.Effect.ShowPaywall)
                            return@launch
                        }
                        analyticsHelper.logMessageSent()
                        val recipientId = _state.value.otherUserId
                        if (recipientId.isNotBlank()) {
                            repository.sendMessage(conversationId, currentUid, recipientId, messageText)
                            premiumRepository.incrementMessageCount(currentUid)
                            _state.update { it.copy(currentInput = "", isSendingMessage = false) }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isSendingMessage = false) }
                        _effect.emit(MessagingContract.Effect.ShowError(UiText.StringResource(R.string.message_error)))
                    }
                }
            }
            is MessagingContract.Action.ShowReactionPicker -> _state.update { it.copy(reactionTargetMessageId = action.messageId) }
            MessagingContract.Action.HideReactionPicker -> _state.update { it.copy(reactionTargetMessageId = null) }
            is MessagingContract.Action.AddReaction -> {
                viewModelScope.launch {
                    try {
                        repository.addReaction(conversationId, action.messageId, auth.currentUser?.uid ?: return@launch, action.emoji)
                        _state.update { it.copy(reactionTargetMessageId = null) }
                    } catch (e: Exception) { _state.update { it.copy(error = e.message) } }
                }
            }
            is MessagingContract.Action.RemoveReaction -> {
                viewModelScope.launch {
                    try { repository.removeReaction(conversationId, action.messageId, auth.currentUser?.uid ?: return@launch) } 
                    catch (e: Exception) { _state.update { it.copy(error = e.message) } }
                }
            }
            MessagingContract.Action.OnUnmatchClick -> _state.update { it.copy(showUnmatchDialog = true) }
            MessagingContract.Action.OnConfirmUnmatch -> confirmUnmatch()
            MessagingContract.Action.OnDismissUnmatchDialog -> _state.update { it.copy(showUnmatchDialog = false) }
            MessagingContract.Action.Reload -> observeMessages()
        }
    }

    private fun confirmUnmatch() {
        viewModelScope.launch {
            val myUid = auth.currentUser?.uid ?: return@launch
            _state.update { it.copy(showUnmatchDialog = false) }
            val matchId = repository.getMatchIdFromConversation(conversationId)
            if (matchId == null) {
                _effect.emit(MessagingContract.Effect.ShowToast(UiText.StringResource(R.string.match_not_found)))
                return@launch
            }
            matchingRepository.unmatch(matchId, conversationId, myUid).onSuccess {
                _effect.emit(MessagingContract.Effect.ShowToast(UiText.StringResource(R.string.match_removed)))
                _effect.emit(MessagingContract.Effect.NavigateBack)
            }.onFailure { e ->
                val message = if (e is UiTextException) e.uiText else UiText.StringResource(R.string.error_occurred)
                _effect.emit(MessagingContract.Effect.ShowToast(message))
            }
        }
    }
}
