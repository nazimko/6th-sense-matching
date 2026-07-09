package com.mhmtn.a6thsense.contact.presentation

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.contact.domain.ContactMessage
import com.mhmtn.a6thsense.contact.domain.ContactRepository
import com.mhmtn.a6thsense.contact.domain.MessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ContactUsContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ContactUsContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onAction(action: ContactUsContract.Action) {
        when (action) {
            is ContactUsContract.Action.OnSubjectSelected -> {
                _state.update { it.copy(selectedSubject = action.subject) }
            }

            is ContactUsContract.Action.OnMessageChanged -> {
                _state.update { it.copy(message = action.message, error = null) }
            }

            ContactUsContract.Action.OnSendClick -> {
                sendMessage()
            }

            ContactUsContract.Action.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun sendMessage() {
        viewModelScope.launch {
            val currentState = _state.value

            // Validation
            if (currentState.message.trim().length < 10) {
                _state.update {
                    it.copy(error = "Mesaj en az 10 karakter olmalıdır")
                }
                return@launch
            }

            _state.update { it.copy(isSending = true) }

            try {
                val user = auth.currentUser
                val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE}"

                val message = ContactMessage(
                    uid = user?.uid ?: "anonymous",
                    userName = user?.displayName ?: "Anonymous",
                    userEmail = user?.email ?: "",
                    subject = currentState.selectedSubject,
                    message = currentState.message.trim(),
                    timestamp = System.currentTimeMillis(),
                    status = MessageStatus.NEW,
                    platform = "android",
                    deviceInfo = deviceInfo
                )

                contactRepository.sendMessage(message).onSuccess {
                    _effect.emit(ContactUsContract.Effect.ShowToast("Mesajınız gönderildi! Teşekkürler 💚"))
                    _effect.emit(ContactUsContract.Effect.NavigateBack)
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isSending = false,
                            error = "Gönderim başarısız: ${error.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSending = false,
                        error = "Bir hata oluştu: ${e.message}"
                    )
                }
            }
        }
    }
}