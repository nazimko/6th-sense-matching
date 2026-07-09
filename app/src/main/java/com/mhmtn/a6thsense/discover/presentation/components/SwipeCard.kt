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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.discover.domain.DiscoverUser
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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

    val swipeProgress = (offsetX.value / 300f).coerceIn(-1f, 1f)
    val likeAlpha = swipeProgress.coerceAtLeast(0f)
    val nopeAlpha = (-swipeProgress).coerceAtLeast(0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f) // 👇 Kartın dikey uzunluğu biraz kısaltıldı (0.72 -> 0.85)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(28.dp))
            .pointerInput(user.uid) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > 150f -> {
                                    offsetX.animateTo(1000f, tween(300))
                                    onSwipeRight()
                                    offsetX.snapTo(0f); offsetY.snapTo(0f); rotation.snapTo(0f)
                                }
                                offsetX.value < -150f -> {
                                    offsetX.animateTo(-1000f, tween(300))
                                    onSwipeLeft()
                                    offsetX.snapTo(0f); offsetY.snapTo(0f); rotation.snapTo(0f)
                                }
                                else -> {
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
        if (user.photoUrl.isNotBlank()) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = user.name.firstOrNull()?.uppercase() ?: "?", fontSize = 72.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        if (user.isPremium) {
            Box(modifier = Modifier.fillMaxSize().border(3.dp, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))), RoundedCornerShape(28.dp)))
        }

        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.8f)))))

        // Overlayler (Like/Nope)
        if (likeAlpha > 0f) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF43E97B).copy(alpha = likeAlpha * 0.2f)))
        }
        if (nopeAlpha > 0f) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFF6B6B).copy(alpha = nopeAlpha * 0.2f)))
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = user.name, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                if (user.isPremium) {
                    Box(modifier = Modifier.clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))).padding(4.dp)) {
                        Text(text = "👑", fontSize = 12.sp)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(when { user.similarityScore >= 70 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7)); user.similarityScore >= 40 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)); else -> listOf(Color(0xFFFFD700), Color(0xFFFF9A56)) })).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Text(text = "${user.similarityScore} ${stringResource(R.string.harmony)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFF43E97B).copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF43E97B), CircleShape))
                        Text(text = stringResource(R.string.active_today), color = Color(0xFF43E97B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
