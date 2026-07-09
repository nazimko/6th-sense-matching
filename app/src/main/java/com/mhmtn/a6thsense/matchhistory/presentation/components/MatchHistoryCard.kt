package com.mhmtn.a6thsense.matchhistory.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.friends.domain.model.FriendshipStatus
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchHistoryCard(
    item: MatchHistoryItem,
    isLoadingConversation: Boolean,
    onMessageClick: () -> Unit,
    onSendFriendRequest: () -> Unit,
    onLongClick: () -> Unit, // 👇 Yeni parametre
    index: Int = 0
) {// Source code removed.}