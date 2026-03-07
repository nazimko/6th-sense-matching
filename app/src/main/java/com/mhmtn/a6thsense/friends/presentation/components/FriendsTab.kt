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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.core.presentation.floating

@Composable
fun FriendsTab(
    friends: List<Friend>,
    onFriendClick: (Friend) -> Unit,
    onFriendLongPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (friends.isEmpty()) {
        // Empty state
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "👥",
                    fontSize = 80.sp,
                    modifier = Modifier.floating(offsetY = 10f, duration = 2000)
                )

                Text(
                    text = R.string.no_friends.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = R.string.friends_subtext.toString(),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(friends, key = { it.uid }) { friend ->
                FriendCard(
                    friend = friend,
                    onClick = { onFriendClick(friend) },
                    onLongPress = { onFriendLongPress(friend.friendshipId) }
                )
            }
        }
    }
}

@Composable
fun FriendCard(
    friend: Friend,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2D1B69),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar with status
            Box {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (friend.isPremium)
                                    listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                else
                                    listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = if (friend.hasCompletedToday) Color(0xFF43E97B) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.name.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Status indicator
                if (friend.hasCompletedToday) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                            }
                            .clip(CircleShape)
                            .background(Color(0xFF43E97B))
                            .border(
                                width = 2.dp,
                                color = Color(0xFF1A1A2E),
                                shape = CircleShape
                            )
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = friend.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (friend.isPremium) {
                        Text(text = "👑", fontSize = 14.sp)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (friend.hasCompletedToday) Color(0xFF43E97B)
                                else Color(0xFF666666)
                            )
                    )

                    Text(
                        text = if (friend.hasCompletedToday) R.string.active.toString() else R.string.passive.toString(),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = if (friend.hasCompletedToday) R.string.friend_completed_today.toString()
                    else R.string.error_no_today_friend_session.toString(),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // Test button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (friend.hasCompletedToday)
                            Brush.linearGradient(
                                listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                            )
                        else
                            Brush.linearGradient(
                                listOf(Color(0xFF4A4A5E), Color(0xFF3A3A4E))
                            )
                    )
                    .shadow(
                        elevation = if (friend.hasCompletedToday) 12.dp else 0.dp,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔮",
                    fontSize = 28.sp,
                    modifier = if (friend.hasCompletedToday) {
                        Modifier.floating(offsetY = 4f, duration = 1500)
                    } else Modifier
                )
            }
        }
    }
}