package com.mhmtn.a6thsense.profile.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors
import com.mhmtn.a6thsense.R

@Composable
fun ProfileRoute(
    onNavigateToAuth: () -> Unit,
    onNavigateToMatchHistory: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToInvite: () -> Unit,
    isDark: Boolean,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showConfetti by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }
    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("ProfileScreen")
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateToAuth -> onNavigateToAuth()
                ProfileContract.Effect.TriggerConfetti -> {
                    showConfetti = true
                }

                ProfileContract.Effect.NavigateToMatchHistory -> onNavigateToMatchHistory()
                ProfileContract.Effect.NavigateToInvite -> {
                    onNavigateToInvite()
                }

                ProfileContract.Effect.NavigateToFriends -> {
                    onNavigateToFriends()
                }

                is ProfileContract.Effect.ProfileImageUploaded -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    ProfileScreen(
        state = state,
        showConfetti = showConfetti,
        onConfettiComplete = { showConfetti = false },
        onAction = viewModel::onAction,
        onUploadProfileImage = viewModel::uploadProfileImage,
        isDark = isDark
    )
}