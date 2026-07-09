package com.mhmtn.a6thsense.invite.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsEvent
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.invite.domain.InviteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteFriendsViewModel @Inject constructor(
    private val repository: InviteRepository,
    private val auth: FirebaseAuth,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(InviteFriendsContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<InviteFriendsContract.Effect>()
    val effect = _effect.asSharedFlow()

    init {
        loadReferralInfo()
    }

    fun onAction(action: InviteFriendsContract.Action) {
        when (action) {
            InviteFriendsContract.Action.OnShareClick -> handleShare()
            is InviteFriendsContract.Action.OnPlatformClick -> handlePlatformShare(action.platform)
            InviteFriendsContract.Action.OnCopyCodeClick -> handleCopyCode()
            InviteFriendsContract.Action.OnEnterCodeClick -> {
                _state.update { it.copy(showCodeInput = true) }
            }
            is InviteFriendsContract.Action.OnCodeInputChange -> {
                _state.update { it.copy(codeInput = action.code.uppercase()) }
            }
            InviteFriendsContract.Action.OnApplyCode -> handleApplyCode()
            InviteFriendsContract.Action.OnDismissCodeInput -> {
                _state.update { it.copy(showCodeInput = false, codeInput = "", error = null) }
            }
        }
    }

    private fun loadReferralInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val uid = auth.currentUser?.uid ?: return@launch

                // Referral code oluştur (yoksa)
                val code = repository.generateReferralCode(uid)
                Log.d("InviteVM", "Referral code: $code")

                // Referral info'yu dinle
                repository.getReferralInfo(uid).collect { info ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            referralInfo = info
                        )
                    }
                }
            } catch (e: Exception) {
                val message = if (e is UiTextException) e.uiText
                else UiText.StringResource(R.string.error_occurred)
                Log.e("InviteVM", "Error loading referral info: ${e.message}", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = message
                    )
                }
            }
        }
    }

    private fun handleShare() {
        viewModelScope.launch {
            val code = _state.value.referralInfo?.referralCode ?: return@launch
            val link = repository.getShareLink(code)


            _effect.emit(InviteFriendsContract.Effect.ShareLink(
                link,
                UiText.StringResource(R.string.share_invite_message, code,link)
            ))

            analyticsHelper.logEvent(
                AnalyticsEvent.ReferralShared("general")
            )
        }
    }

    private fun handlePlatformShare(platform: SharePlatform) {
        viewModelScope.launch {
            _effect.emit(InviteFriendsContract.Effect.ShareToPlatform(platform)) // 👈 Sadece platform gönder

            val uid = auth.currentUser?.uid ?: return@launch
            repository.trackShare(uid, platform.name)

            analyticsHelper.logEvent(
                AnalyticsEvent.ReferralShared(platform.name)
            )
        }
    }

    private fun handleCopyCode() {
        viewModelScope.launch {
            val code = _state.value.referralInfo?.referralCode ?: return@launch
            _effect.emit(InviteFriendsContract.Effect.CopyToClipboard(code))
            _effect.emit(InviteFriendsContract.Effect.ShowToast(UiText.StringResource(R.string.copied)))
        }
    }

    private fun handleApplyCode() {
        viewModelScope.launch {
            val code = _state.value.codeInput.trim()

            if (code.isBlank()) {
                _state.update { it.copy(error = UiText.StringResource(R.string.enter_invite_code)) }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            val uid = auth.currentUser?.uid ?: return@launch
            val result = repository.applyReferralCode(uid, code)

            result.onSuccess { reward ->
                _effect.emit(InviteFriendsContract.Effect.ShowReward(reward.premiumDays))
                _state.update {
                    it.copy(
                        isLoading = false,
                        showCodeInput = false,
                        codeInput = ""
                    )
                }

                analyticsHelper.logEvent(
                    AnalyticsEvent.ReferralApplied(code)
                )
            }.onFailure { error ->
                val message = if (error is UiTextException) error.uiText
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
}