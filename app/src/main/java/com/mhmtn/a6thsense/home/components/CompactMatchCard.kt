package com.mhmtn.a6thsense.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.floating
import com.mhmtn.a6thsense.home.domain.TodayMatch
import kotlinx.coroutines.tasks.await

@Composable
fun CompactMatchCard(
    match: TodayMatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}