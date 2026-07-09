package com.mhmtn.a6thsense.friends.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.friends.domain.model.Friend
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.core.presentation.floating
import kotlinx.coroutines.tasks.await

@Composable
fun FriendsTab(
    friends: List<Friend>,
    onFriendClick: (Friend) -> Unit,
    onFriendLongPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}