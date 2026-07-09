package com.mhmtn.a6thsense.friends.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.mhmtn.a6thsense.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.friends.domain.model.Friend
import com.mhmtn.a6thsense.friends.presentation.components.*
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme

@Composable
fun FriendsScreen(
    state: FriendsContract.State,
    isDark: Boolean,
    onAction: (FriendsContract.Action) -> Unit,
    onBackClick: () -> Unit,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
){// Source code removed.}