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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.bounceClick

@Composable
fun UserStatsCard(
    isPremium: Boolean,
    isDark: Boolean,
    swipesUsed: Int,
    swipeLimit: Int,
    soulSyncUsed: Int,
    soulSyncLimit: Int,
    messagesUsed: Int,
    messageLimit: Int,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(true) }

    // Tema Renkleri
    val shadowColor = if (isPremium) Color(0xFFFFD700) else Color(0xFF7B5EA7)
    
    // Arka plan gradyanı - Dark mode için daha temiz, light mode için daha yumuşak
    val cardBackground = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF24243E).copy(alpha = 0.85f),
                Color(0xFF1A1A2E).copy(alpha = 0.95f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0xFFF8F5FF).copy(alpha = 0.9f)
            )
        )
    }

    val borderColors = if (isPremium) {
        listOf(Color(0xFFFFD700).copy(alpha = 0.5f), Color(0xFFFFA500).copy(alpha = 0.3f))
    } else {
        if (isDark) {
            listOf(Color(0xFF7B5EA7).copy(alpha = 0.4f), Color(0xFF4568DC).copy(alpha = 0.2f))
        } else {
            listOf(Color(0xFF7B5EA7).copy(alpha = 0.2f), Color(0xFF4568DC).copy(alpha = 0.1f))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPremium) 12.dp else 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = shadowColor.copy(alpha = 0.1f),
                spotColor = shadowColor.copy(alpha = if (isPremium) 0.3f else 0.1f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackground)
            .border(
                width = 1.dp,
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
                Text(text = if (isPremium) "👑" else "📊", fontSize = 28.sp)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = if (isPremium) stringResource(R.string.premium_member) else stringResource(R.string.daily_limits),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!isExpanded) {
                        Text(
                            text = if (isPremium) stringResource(R.string.all_active)
                            else "$swipesUsed/$swipeLimit ${stringResource(R.string.discover_text)}, $soulSyncUsed/$soulSyncLimit ${
                                stringResource(R.string.soul_sync_text)
                            }, $messagesUsed/$messageLimit ${stringResource(R.string.messages_text)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Expand/Collapse Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        .bounceClick { isExpanded = !isExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
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
                        label = stringResource(R.string.swipe_limit),
                        used = swipesUsed,
                        limit = swipeLimit,
                        isUnlimited = isPremium,
                        isDark = isDark
                    )

                    StatsLimitItem(
                        icon = "🔮",
                        label = stringResource(R.string.soul_sync_limit),
                        used = soulSyncUsed,
                        limit = soulSyncLimit,
                        isUnlimited = isPremium,
                        isDark = isDark
                    )

                    // Message Limit Item
                    StatsLimitItem(
                        icon = "💬",
                        label = stringResource(R.string.message_limit),
                        used = messagesUsed,
                        limit = messageLimit,
                        isUnlimited = isPremium,
                        isDark = isDark
                    )

                    // Upgrade Button
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
                                    text = stringResource(R.string.use_limitless),
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
    isUnlimited: Boolean,
    isDark: Boolean
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isUnlimited) "∞" else "$used/$limit",
                    fontSize = 13.sp,
                    color = when {
                        isUnlimited -> Color(0xFFFFD700)
                        used >= limit -> Color(0xFFFF6B6B)
                        used >= limit * 0.8f -> Color(0xFFFFD700)
                        else -> if (isDark) Color(0xFF43E97B) else Color(0xFF2EBD59)
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
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = if (isDark) 0.15f else 0.1f))
            ) {
                val progress = if (isUnlimited) 1f else (used.toFloat() / limit).coerceIn(0f, 1f)
                val barColors = when {
                    isUnlimited -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                    used >= limit -> listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F))
                    used >= limit * 0.8f -> listOf(Color(0xFFFFD700), Color(0xFFFF9A56))
                    else -> if (isDark) listOf(Color(0xFF43E97B), Color(0xFF38F9D7)) else listOf(Color(0xFF2EBD59), Color(0xFF24A64D))
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

@Preview(showBackground = true, name = "Regular Light")
@Composable
fun UserStatsCardPreview_RegularLight() {
    MaterialTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFF8F5FF)
        ) {
            UserStatsCard(
                isPremium = true,
                isDark = false,
                swipesUsed = 15,
                swipeLimit = 20,
                soulSyncUsed = 15,
                soulSyncLimit = 20,
                messagesUsed = 8,
                messageLimit = 10,
                onUpgradeClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Regular Dark")
@Composable
fun UserStatsCardPreview_RegularDark() {
    MaterialTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF1A1A2E)
        ) {
            UserStatsCard(
                isPremium = false,
                isDark = true,
                swipesUsed = 15,
                swipeLimit = 20,
                soulSyncUsed = 15,
                soulSyncLimit = 20,
                messagesUsed = 8,
                messageLimit = 10,
                onUpgradeClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Premium Dark")
@Composable
fun UserStatsCardPreview_PremiumDark() {
    MaterialTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF1A1A2E)
        ) {
            UserStatsCard(
                isPremium = true,
                isDark = true,
                swipesUsed = 100,
                swipeLimit = 20,
                soulSyncUsed = 100,
                soulSyncLimit = 20,
                messagesUsed = 50,
                messageLimit = 10,
                onUpgradeClick = {}
            )
        }
    }
}
