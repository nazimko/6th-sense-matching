package com.mhmtn.a6thsense.invite.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.*
import com.mhmtn.a6thsense.R
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.CallbackManager
import com.mhmtn.a6thsense.invite.data.PlatformShareHelper
import com.mhmtn.a6thsense.invite.presentation.components.RewardDialog

@Composable
fun InviteFriendsRoute(
    isDark: Boolean,
    onBackClick: () -> Unit,
    viewModel: InviteFriendsViewModel = hiltViewModel()
) {// Source code removed.}