package com.mhmtn.a6thsense.core.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mhmtn.a6thsense.R
import androidx.compose.ui.unit.dp

// ==================== BASE SHIMMER ====================

@Composable
fun shimmerBrush(
    isDark: Boolean = true // dark ekranlar için açık, light için koyu
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
    isDark: Boolean = true,
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
fun HomeScreenSkeleton() {
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
                    shape = RoundedCornerShape(8.dp)
                )
                ShimmerBox(
                    modifier = Modifier.size(width = 100.dp, height = 16.dp),
                    shape = RoundedCornerShape(6.dp)
                )
            }
            ShimmerBox(
                modifier = Modifier.size(40.dp),
                shape = CircleShape
            )
        }

        // Match card skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(24.dp)
        )

        // Daily status card skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(24.dp)
        )

        // Start button skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ==================== PROFILE SKELETON ====================

@Composable
fun ProfileScreenSkeleton() {
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
            isDark = false,
            shape = CircleShape
        )

        // İsim skeleton
        ShimmerBox(
            modifier = Modifier.size(width = 160.dp, height = 28.dp),
            isDark = false,
            shape = RoundedCornerShape(8.dp)
        )

        // Email skeleton
        ShimmerBox(
            modifier = Modifier.size(width = 120.dp, height = 16.dp),
            isDark = false,
            shape = RoundedCornerShape(6.dp)
        )

        // Badge skeleton
        ShimmerBox(
            modifier = Modifier.size(width = 100.dp, height = 32.dp),
            isDark = false,
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats grid skeleton
        Text(
            text = R.string.statistics.toString(),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCardSkeleton(modifier = Modifier.weight(1f))
            StatCardSkeleton(modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCardSkeleton(modifier = Modifier.weight(1f))
            StatCardSkeleton(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(
                modifier = Modifier.size(44.dp),
                isDark = false,
                shape = RoundedCornerShape(12.dp)
            )
            ShimmerBox(
                modifier = Modifier.size(width = 60.dp, height = 28.dp),
                isDark = false,
                shape = RoundedCornerShape(6.dp)
            )
            ShimmerBox(
                modifier = Modifier.size(width = 80.dp, height = 14.dp),
                isDark = false,
                shape = RoundedCornerShape(4.dp)
            )
        }
    }
}

// ==================== CONVERSATIONS SKELETON ====================

@Composable
fun ConversationsScreenSkeleton() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        ShimmerBox(
            modifier = Modifier
                .padding(start = 24.dp, top = 48.dp, bottom = 24.dp)
                .size(width = 160.dp, height = 32.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Liste itemleri
        repeat(6) {
            ConversationItemSkeleton()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 24.dp)
                    .background(Color.White.copy(alpha = 0.05f))
            )
        }
    }
}

@Composable
private fun ConversationItemSkeleton() {
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
            shape = CircleShape
        )

        // İsim + mesaj
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.size(width = 120.dp, height = 16.dp),
                shape = RoundedCornerShape(4.dp)
            )
            ShimmerBox(
                modifier = Modifier.size(width = 200.dp, height = 12.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }

        // Saat
        ShimmerBox(
            modifier = Modifier.size(width = 36.dp, height = 12.dp),
            shape = RoundedCornerShape(4.dp)
        )
    }
}

// ==================== MESSAGING SKELETON ====================

@Composable
fun MessagingScreenSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBox(modifier = Modifier.size(40.dp), shape = CircleShape)
            ShimmerBox(modifier = Modifier.size(40.dp), shape = CircleShape)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerBox(
                    modifier = Modifier.size(width = 120.dp, height = 16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                ShimmerBox(
                    modifier = Modifier.size(width = 60.dp, height = 12.dp),
                    shape = RoundedCornerShape(4.dp)
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
            MessageBubbleSkeleton(isOwn = false)
            MessageBubbleSkeleton(isOwn = true)
            MessageBubbleSkeleton(isOwn = false, width = 200.dp)
            MessageBubbleSkeleton(isOwn = true, width = 140.dp)
            MessageBubbleSkeleton(isOwn = false, width = 180.dp)
            MessageBubbleSkeleton(isOwn = true, width = 220.dp)
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
                shape = RoundedCornerShape(24.dp)
            )
            ShimmerBox(
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            )
        }
    }
}

@Composable
private fun MessageBubbleSkeleton(
    isOwn: Boolean,
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
            )
        )
    }
}