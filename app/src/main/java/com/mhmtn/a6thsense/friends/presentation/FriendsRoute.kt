package com.mhmtn.a6thsense.friends.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.matchhistory.presentation.components.PremiumGateCard

@Composable
fun FriendsRoute(
    onBackClick: () -> Unit,
    onNavigateToSoulSync: (String) -> Unit,
    onNavigateToInvite: () -> Unit,
    isDark: Boolean,
    onNavigateToPremium: () -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {// Source code removed.}
