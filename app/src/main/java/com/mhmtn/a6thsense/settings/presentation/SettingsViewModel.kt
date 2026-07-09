package com.mhmtn.a6thsense.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private var userDataListenerRegistration: ListenerRegistration? = null

    init {
        observeSettings()
        observeUserData()
        analyticsHelper.logSettingsOpened()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getSettings()
                .catch { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { settings ->
                    _state.update {
                        it.copy(
                            settings = settings,
                            nameInput = settings.displayName,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun observeUserData() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            userDataListenerRegistration = firestore.collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    val showInDiscover = snapshot?.getBoolean("showInDiscover") ?: true
                    _state.update {
                        it.copy(showInDiscover = showInDiscover)
                    }
                }
        }
    }

    fun onAction(action: SettingsContract.Action) {
        when (action) {
            is SettingsContract.Action.UpdateTheme -> {
                viewModelScope.launch {
                    repository.updateTheme(action.isDark)
                    analyticsHelper.logThemeChanged(action.isDark)
                }
            }

            is SettingsContract.Action.OnLogoutClick -> {
                _state.update { it.copy(showLogoutDialog = true) }
            }

            is SettingsContract.Action.OnLogoutDismiss -> {
                _state.update { it.copy(showLogoutDialog = false) }
            }

            is SettingsContract.Action.OnLogoutConfirm -> {
                _state.update { it.copy(showLogoutDialog = false, isLoggingOut = true) }
                viewModelScope.launch {
                    try {
                        userDataListenerRegistration?.remove()
                        billingRepository.resetPremiumState()
                        authRepository.signOut()
                        _effect.emit(SettingsContract.Effect.NavigateToLogin)
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoggingOut = false, error = e.message) }
                    }
                }
            }

            SettingsContract.Action.OnContactUsClick -> {
                viewModelScope.launch {
                    _effect.emit(SettingsContract.Effect.NavigateToContactUs)
                }
            }

            is SettingsContract.Action.UpdateMatchNotifications -> {
                viewModelScope.launch {
                    repository.updateMatchNotifications(action.enabled)
                    analyticsHelper.logNotificationToggled("match", action.enabled)
                }
            }

            is SettingsContract.Action.UpdateMessageNotifications -> {
                viewModelScope.launch {
                    repository.updateMessageNotifications(action.enabled)
                }
            }

            SettingsContract.Action.OnShowInDiscoverToggle -> {
                viewModelScope.launch {
                    val newValue = !_state.value.showInDiscover
                    try {
                        repository.setShowInDiscover(newValue)
                        _state.update { it.copy(showInDiscover = newValue) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }

            SettingsContract.Action.StartEditingName -> {
                _state.update { it.copy(isEditingName = true) }
            }

            is SettingsContract.Action.TypeName -> {
                _state.update { it.copy(nameInput = action.name) }
            }

            SettingsContract.Action.SaveName -> {
                val name = _state.value.nameInput.trim()
                if (name.isBlank()) return

                viewModelScope.launch {
                    _state.update { it.copy(isSavingName = true) }
                    try {
                        repository.updateDisplayName(name)
                        _state.update {
                            it.copy(
                                isEditingName = false,
                                isSavingName = false
                            )
                        }
                        _effect.emit(
                            SettingsContract.Effect.ShowMessage(UiText.StringResource(R.string.name_updated))
                        )
                    } catch (e: Exception) {
                        _state.update { it.copy(isSavingName = false) }
                        val message = if (e is UiTextException) e.uiText
                        else UiText.StringResource(R.string.error_occurred)
                        _effect.emit(
                            SettingsContract.Effect.ShowMessage(message)
                        )
                    }
                }
            }

            SettingsContract.Action.CancelEditingName -> {
                _state.update {
                    it.copy(
                        isEditingName = false,
                        nameInput = it.settings.displayName
                    )
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        userDataListenerRegistration?.remove()
    }
}
