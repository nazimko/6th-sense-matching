package com.mhmtn.a6thsense.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.bounceClick

@Composable
fun UserStatsCard(
    isPremium: Boolean,
    swipesUsed: Int,
    swipeLimit: Int,
    messagesUsed: Int,
    messageLimit: Int,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    // Tema Renkleri
    val shadowColor = if (isPremium) Color(0xFFFFD700) else Color(0xFF7B5EA7)
    val borderColors = if (isPremium) {
        listOf(Color(0xFFFFD700).copy(alpha = 0.5f), Color(0xFFFFA500).copy(alpha = 0.3f))
    } else {
        listOf(Color(0xFF7B5EA7).copy(alpha = 0.5f), Color(0xFF4568DC).copy(alpha = 0.3f))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = shadowColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2D1B69).copy(alpha = 0.3f),
                        Color(0xFF1A1A2E).copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(borderColors),
                shape = RoundedCornerShape(24.dp)
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = if (isPremium) "👑" else "📊", fontSize = 28.sp)
                    Column {
                        Text(
                            text = if (isPremium) R.string.premium_member.toString() else R.string.daily_limits.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (!isExpanded) {
                            Text(
                                text = if (isPremium) R.string.all_active.toString()
                                else "$swipesUsed/$swipeLimit ${R.string.discover_text}, $messagesUsed/$messageLimit ${R.string.messages_text}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Expand/Collapse Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .bounceClick { isExpanded = !isExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = if (isExpanded) 180f else 0f }
                    )
                }
            }

            // Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Swipe Limit Item
                    StatsLimitItem(
                        icon = "🔄",
                        label = "Swipe Limit",
                        used = swipesUsed,
                        limit = swipeLimit,
                        isUnlimited = isPremium
                    )

                    // Message Limit Item
                    StatsLimitItem(
                        icon = "💬",
                        label = "Message Limit",
                        used = messagesUsed,
                        limit = messageLimit,
                        isUnlimited = isPremium
                    )

                    // Upgrade Button (Sadece Premium değilse ve limitlerden biri dolduysa)
                    if (!isPremium && (swipesUsed >= swipeLimit || messagesUsed >= messageLimit)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                    )
                                )
                                .bounceClick(onClick = onUpgradeClick)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "👑", fontSize = 16.sp)
                                Text(
                                    text = R.string.use_limitless.toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsLimitItem(
    icon: String,
    label: String,
    used: Int,
    limit: Int,
    isUnlimited: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = icon, fontSize = 20.sp)

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isUnlimited) "∞" else "$used/$limit",
                    fontSize = 13.sp,
                    color = when {
                        isUnlimited -> Color(0xFFFFD700)
                        used >= limit -> Color(0xFFFF6B6B)
                        used >= limit * 0.8f -> Color(0xFFFFD700)
                        else -> Color(0xFF43E97B)
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                val progress = if (isUnlimited) 1f else (used.toFloat() / limit).coerceIn(0f, 1f)
                val barColors = when {
                    isUnlimited -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                    used >= limit -> listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F))
                    used >= limit * 0.8f -> listOf(Color(0xFFFFD700), Color(0xFFFF9A56))
                    else -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.linearGradient(barColors))
                )
            }
        }
    }
}