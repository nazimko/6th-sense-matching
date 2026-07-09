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
): Brush {
    val shimmerColors = if (isDark) {
        listOf(
            Color.White.copy(alpha = 0.05f),
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f)
        )
    } else {
        listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush(isDark))
    )
}

// ==================== HOME SKELETON ====================

@Composable
fun HomeScreenSkeleton(
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBox(
                    modifier = Modifier.size(width = 160.dp, height = 28.dp),
                    shape = RoundedCornerShape(8.dp),
                    isDark = isDark
                )
                ShimmerBox(
                    modifier = Modifier.size(width = 100.dp, height = 16.dp),
                    shape = RoundedCornerShape(6.dp),
                    isDark = isDark
                )
            }
            ShimmerBox(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                isDark = isDark
            )
        }

        // Match card skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(24.dp),
            isDark = isDark
        )

        // Daily status card skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(24.dp),
            isDark = isDark
        )

        // Start button skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            isDark = isDark
        )
    }
}

// ==================== PROFILE SKELETON ====================

@Composable
fun ProfileScreenSkeleton(
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Avatar skeleton
        ShimmerBox(
            modifier = Modifier.size(96.dp),
            isDark = isDark,
            shape = CircleShape
        )

        // İsim skeleton
        ShimmerBox(
            modifier = Modifier.size(width = 160.dp, height = 28.dp),
            isDark = isDark,
            shape = RoundedCornerShape(8.dp)
        )

        // Email skeleton
        ShimmerBox(
            modifier = Modifier.size(width = 120.dp, height = 16.dp),
            isDark = isDark,
            shape = RoundedCornerShape(6.dp)
        )

        // Badge skeleton
        ShimmerBox(
            modifier = Modifier.size(width = 100.dp, height = 32.dp),
            isDark = isDark,
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats grid skeleton
        Text(
            text = stringResource(R.string.statistics),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCardSkeleton(modifier = Modifier.weight(1f), isDark = isDark)
            StatCardSkeleton(modifier = Modifier.weight(1f), isDark = isDark)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCardSkeleton(modifier = Modifier.weight(1f), isDark = isDark)
            StatCardSkeleton(modifier = Modifier.weight(1f), isDark = isDark)
        }
    }
}

@Composable
private fun StatCardSkeleton(modifier: Modifier = Modifier, isDark: Boolean) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(
                modifier = Modifier.size(44.dp),
                isDark = isDark,
                shape = RoundedCornerShape(12.dp)
            )
            ShimmerBox(
                modifier = Modifier.size(width = 60.dp, height = 28.dp),
                isDark = isDark,
                shape = RoundedCornerShape(6.dp)
            )
            ShimmerBox(
                modifier = Modifier.size(width = 80.dp, height = 14.dp),
                isDark = isDark,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}

// ==================== CONVERSATIONS SKELETON ====================

@Composable
fun ConversationsScreenSkeleton(isDark: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        ShimmerBox(
            modifier = Modifier
                .padding(start = 24.dp, top = 48.dp, bottom = 24.dp)
                .size(width = 160.dp, height = 32.dp),
            shape = RoundedCornerShape(8.dp),
            isDark = isDark
        )

        // Liste itemleri
        repeat(6) {
            ConversationItemSkeleton(isDark = isDark)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 24.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
private fun ConversationItemSkeleton(
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar
        ShimmerBox(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            isDark = isDark
        )

        // İsim + mesaj
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.size(width = 120.dp, height = 16.dp),
                shape = RoundedCornerShape(4.dp),
                isDark = isDark
            )
            ShimmerBox(
                modifier = Modifier.size(width = 200.dp, height = 12.dp),
                shape = RoundedCornerShape(4.dp),
                isDark = isDark
            )
        }

        // Saat
        ShimmerBox(
            modifier = Modifier.size(width = 36.dp, height = 12.dp),
            shape = RoundedCornerShape(4.dp),
            isDark = isDark
        )
    }
}

// ==================== MESSAGING SKELETON ====================

@Composable
fun MessagingScreenSkeleton(
    isDark: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBox(modifier = Modifier.size(40.dp), shape = CircleShape, isDark = isDark)
            ShimmerBox(modifier = Modifier.size(40.dp), shape = CircleShape, isDark = isDark)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerBox(
                    modifier = Modifier.size(width = 120.dp, height = 16.dp),
                    shape = RoundedCornerShape(4.dp),
                    isDark = isDark
                )
                ShimmerBox(
                    modifier = Modifier.size(width = 60.dp, height = 12.dp),
                    shape = RoundedCornerShape(4.dp),
                    isDark = isDark
                )
            }
        }

        // Mesaj balonları
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Karşıdan gelen
            MessageBubbleSkeleton(isOwn = false, isDark = isDark)
            MessageBubbleSkeleton(isOwn = true, isDark = isDark)
            MessageBubbleSkeleton(isOwn = false, width = 200.dp, isDark = isDark)
            MessageBubbleSkeleton(isOwn = true, width = 140.dp, isDark = isDark)
            MessageBubbleSkeleton(isOwn = false, width = 180.dp, isDark = isDark)
            MessageBubbleSkeleton(isOwn = true, width = 220.dp, isDark = isDark)
        }

        // Input skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                isDark = isDark
            )
            ShimmerBox(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                isDark = isDark
            )
        }
    }
}

@Composable
private fun MessageBubbleSkeleton(
    isOwn: Boolean,
    isDark: Boolean,
    width: Dp = 160.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isOwn) 64.dp else 16.dp,
                end = if (isOwn) 16.dp else 64.dp
            ),
        contentAlignment = if (isOwn) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        ShimmerBox(
            modifier = Modifier
                .width(width)
                .height(44.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isOwn) 20.dp else 4.dp,
                bottomEnd = if (isOwn) 4.dp else 20.dp
            ),
            isDark = isDark
        )
    }
}