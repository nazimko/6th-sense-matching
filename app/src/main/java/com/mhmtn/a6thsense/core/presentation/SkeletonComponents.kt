package com.mhmtn.a6thsense.core.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.mhmtn.a6thsense.R
import androidx.compose.ui.unit.dp

// ==================== BASE SHIMMER ====================

@Composable
fun shimmerBrush(
    isDark: Boolean
): Brush {// Source code removed.}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {// Source code removed.}

// ==================== HOME SKELETON ====================

@Composable
fun HomeScreenSkeleton(
    isDark: Boolean
) {// Source code removed.}

// ==================== PROFILE SKELETON ====================

@Composable
fun ProfileScreenSkeleton(
    isDark: Boolean
) {// Source code removed.}

@Composable
private fun StatCardSkeleton(modifier: Modifier = Modifier, isDark: Boolean) {// Source code removed.}

// ==================== CONVERSATIONS SKELETON ====================

@Composable
fun ConversationsScreenSkeleton(isDark: Boolean){// Source code removed.}

@Composable
private fun ConversationItemSkeleton(
    isDark: Boolean
){// Source code removed.}

// ==================== MESSAGING SKELETON ====================

@Composable
fun MessagingScreenSkeleton(
    isDark: Boolean
) {// Source code removed.}

@Composable
private fun MessageBubbleSkeleton(
    isOwn: Boolean,
    isDark: Boolean,
    width: Dp = 160.dp
) {// Source code removed.}