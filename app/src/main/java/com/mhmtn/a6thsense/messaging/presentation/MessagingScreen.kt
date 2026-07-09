package com.mhmtn.a6thsense.messaging.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.MessagingScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.core.presentation.PremiumLimitSnackbar
import com.mhmtn.a6thsense.messaging.presentation.components.*
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.flowOf

@Composable
fun MessagingScreen(
    matchedUserName: String,
    matchedUserPhotoUrl: String,
    state: MessagingContract.State,
    listState: LazyListState,
    isDark: Boolean,
    showPremiumSnackbar: Boolean,
    onDismissSnackbar: () -> Unit,
    onUpgradeClick: () -> Unit,
    onAction: (MessagingContract.Action) -> Unit,
    onBackClick: () -> Unit
) {// Source code removed.}