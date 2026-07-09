package com.mhmtn.a6thsense.conversations.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mhmtn.a6thsense.conversations.domain.ConversationItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationListItem(
    isDark: Boolean,
    item: ConversationItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 👇 Avatar Alanı (Genişlik 66.dp olarak sabitlendi, böylece hizalama bozulmaz)
        Box(
            modifier = Modifier.size(66.dp),
            contentAlignment = Alignment.Center
        ) {
            if (item.isPremium) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
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
                        width = if (item.isPremium) 2.dp else 0.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!item.otherUserPhotoUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.otherUserPhotoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.otherUserName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        error = {
                            // 👇 Fotoğraf yüklenemezse harf göster
                            LetterAvatar(name = item.otherUserName)
                        }
                    )
                } else {
                    LetterAvatar(name = item.otherUserName)
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.otherUserName,
                    fontSize = 16.sp,
                    fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (item.isPremium) {
                    Text(text = "👑", fontSize = 12.sp)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.similarity > 0) {
                    SimilarityBadge(
                        isDark = isDark,
                        similarity = item.similarity,
                        size = SimilarityBadgeContract.SimilarityBadgeSize.SMALL
                    )
                }
                Text(
                    text = item.lastMessage.ifBlank { stringResource(R.string.no_message_text) },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (item.unreadCount > 0) 0.9f else 0.5f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (item.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (item.lastMessageTimestamp > 0) {
                Text(
                    text = timeFormat.format(Date(item.lastMessageTimestamp)),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            if (item.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF7B5EA7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.unreadCount > 9) "9+" else item.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterAvatar(name: String) {
    Text(
        text = name.firstOrNull()?.uppercase() ?: "?",
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}
