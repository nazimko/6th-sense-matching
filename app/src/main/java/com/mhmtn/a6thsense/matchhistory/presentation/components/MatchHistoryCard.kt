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
) {
    // Staggered reveal animasyonu
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 80L)
        visible = true
    }

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "card_reveal"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label = "card_alpha"
    )

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("tr")) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = offsetY
                this.alpha = alpha
            }
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.2f),
                spotColor = Color(0xFF7B5EA7).copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            // 👇 Uzun basma desteği eklendi
            .combinedClickable(
                onClick = {}, // Kartın geneline tıklama eylemi vermiyoruz, butonlara veriyoruz
                onLongClick = onLongClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            )
            .then(
                if (item.isPremium) {
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                } else Modifier.border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7B5EA7).copy(alpha = 0.2f),
                            Color(0xFF4568DC).copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Üst kısım: Avatar + İsim + Tarih
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar + Glow
                Box(contentAlignment = Alignment.Center) {

                    if (item.isPremium) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700).copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (item.isPremium)
                                        listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                    else
                                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                                )
                            )
                            .border(
                                width = if (item.isPremium) 3.dp else 2.dp,
                                brush = Brush.linearGradient(
                                    if (item.isPremium)
                                        listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                    else
                                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.matchedUserPhotoUrl.isNotBlank()) {
                            AsyncImage(
                                model = item.matchedUserPhotoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = item.matchedUserName
                                    .firstOrNull()?.uppercase() ?: "?",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // İsim + Tarih
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.matchedUserName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (item.isPremium) {
                            Text(text = "👑", fontSize = 14.sp)
                        }
                    }

                    Text(
                        text = dateFormat.format(Date(item.timestamp)),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Uyum skoru
                SimilarityBadge(score = item.similarityScore)
            }

            when (item.friendshipStatus) {
                FriendshipStatus.ACCEPTED -> {
                    // Already friends - show badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF43E97B).copy(alpha = 0.15f))
                            .border(
                                width = 1.dp,
                                color = Color(0xFF43E97B).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "✓", fontSize = 14.sp, color = Color(0xFF43E97B))
                            Text(
                                text = stringResource( R.string.you_are_friends),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF43E97B)
                            )
                        }
                    }
                }

                FriendshipStatus.PENDING -> {
                    // Request pending
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFFFD700).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⏳", fontSize = 14.sp)
                            Text(
                                text = stringResource(R.string.send_friend_request),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }

                else -> {
                    // Send request button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                                )
                            )
                            .bounceClick(onClick = onSendFriendRequest)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "➕", fontSize = 14.sp)
                            Text(
                                text = stringResource(R.string.add_friend),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

            }

            // Alt kısım: Mesajlaş butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF7B5EA7),
                                Color(0xFF4568DC)
                            )
                        )
                    )
                    .bounceClick(onClick = onMessageClick)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoadingConversation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💬", fontSize = 16.sp)
                        Text(
                            text = stringResource(R.string.message_text),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SimilarityBadge(score: Int) {
    val color = when {
        score >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
        score >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
        score >= 40 -> listOf(Color(0xFFFFD700), Color(0xFFFF9A56))
        else -> listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(color))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "$score%",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
