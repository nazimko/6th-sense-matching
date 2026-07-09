package com.mhmtn.a6thsense.discover.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.core.presentation.PremiumLimitSnackbar
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.ShimmerBox
import com.mhmtn.a6thsense.discover.presentation.components.SwipeButtons
import com.mhmtn.a6thsense.discover.presentation.components.SwipeCard
import com.mhmtn.a6thsense.R

@Composable
fun DiscoverScreen(
    state: DiscoverContract.State,
    isDark: Boolean,
    showPremiumSnackbar: Boolean, // 👈 Parametre
    onDismissSnackbar: () -> Unit,
    onUpgradeClick: () -> Unit,
    onAction: (DiscoverContract.Action) -> Unit
) {// Source code removed.}