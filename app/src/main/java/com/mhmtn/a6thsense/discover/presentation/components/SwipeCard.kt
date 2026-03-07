package com.mhmtn.a6thsense.discover.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.discover.domain.DiscoverUser
import kotlinx.coroutines.launch

@Composable
fun SwipeCard(
    user: DiscoverUser,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    // Sürükleme yönüne göre overlay rengi
    val swipeProgress = (offsetX.value / 300f).coerceIn(-1f, 1f)
    val likeAlpha = swipeProgress.coerceAtLeast(0f)
    val nopeAlpha = (-swipeProgress).coerceAtLeast(0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(28.dp))
            .pointerInput(user.uid) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > 150f -> {
                                    // Sağa swipe - mesajlaş
                                    offsetX.animateTo(
                                        targetValue = 1000f,
                                        animationSpec = tween(300)
                                    )
                                    onSwipeRight()
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                    rotation.snapTo(0f)
                                }
                                offsetX.value < -150f -> {
                                    // Sola swipe - geç
                                    offsetX.animateTo(
                                        targetValue = -1000f,
                                        animationSpec = tween(300)
                                    )
                                    onSwipeLeft()
                                    offsetX.snapTo(0f)
                                    offsetY.snapTo(0f)
                                    rotation.snapTo(0f)
                                }
                                else -> {
                                    // Geri dön
                                    launch { offsetX.animateTo(0f, spring(0.5f, 300f)) }
                                    launch { offsetY.animateTo(0f, spring(0.5f, 300f)) }
                                    launch { rotation.animateTo(0f, spring(0.5f, 300f)) }
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y * 0.3f)
                            rotation.snapTo(offsetX.value / 20f)
                        }
                    }
                )
            }
    ) {
        // Arka plan fotoğraf
        if (user.photoUrl.isNotBlank()) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        if (user.isPremium) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA500)),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            )
        }
        // Alt gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // LIKE overlay (sağa swipe)
        if (likeAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF43E97B).copy(alpha = likeAlpha * 0.3f))
            )
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopStart)
                    .border(
                        width = 4.dp,
                        color = Color(0xFF43E97B).copy(alpha = likeAlpha),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "💬",
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer { alpha = likeAlpha }
                )
            }
        }

        // NOPE overlay (sola swipe)
        if (nopeAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF6B6B).copy(alpha = nopeAlpha * 0.3f))
            )
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.TopEnd)
                    .border(
                        width = 4.dp,
                        color = Color(0xFFFF6B6B).copy(alpha = nopeAlpha),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "👋",
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer { alpha = nopeAlpha }
                )
            }
        }

        // Kullanıcı bilgileri (alt kısım)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 👇 Premium badge + isim
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = user.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                if (user.isPremium) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                )
                            )
                            .padding(4.dp)
                    ) {
                        Text(text = "👑", fontSize = 14.sp)
                    }
                }
            }

            // Uyum skoru
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Skor badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                when {
                                    user.similarityScore >= 70 ->
                                        listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                                    user.similarityScore >= 40 ->
                                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                                    else ->
                                        listOf(Color(0xFFFFD700), Color(0xFFFF9A56))
                                }
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${user.similarityScore} ${R.string.harmony.toString()}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Match durumu
                if (user.isMatched) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = R.string.matched.toString(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Aktif badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF43E97B).copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF43E97B), CircleShape)
                        )
                        Text(
                            text = R.string.active_today.toString(),
                            color = Color(0xFF43E97B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}