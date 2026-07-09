package com.mhmtn.a6thsense.home.components

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import com.mhmtn.a6thsense.R
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.floating

@Composable
fun SessionCard(
    type: DailyActivityContract.SessionType,
    title: String,
    description: String,
    emoji: String,
    isCompleted: Boolean,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "session_glow")

    // Glow animation
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Shimmer effect for active cards
    val shimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Scale animation on tap
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (!isCompleted) 24.dp else 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = if (!isCompleted) gradient[0].copy(alpha = glow) else Color.Gray,
                spotColor = if (!isCompleted) gradient[1].copy(alpha = glow * 0.5f) else Color.Gray
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                if (!isCompleted) {
                    Brush.linearGradient(
                        colors = gradient + listOf(gradient[0].copy(alpha = 0.8f)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                } else {
                    // Completed state - muted green gradient
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                }
            )
            .border(
                width = if (!isCompleted) 2.dp else 1.dp,
                brush = if (!isCompleted) {
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.4f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF43E97B).copy(alpha = 0.5f),
                            Color(0xFF38F9D7).copy(alpha = 0.5f)
                        )
                    )
                },
                shape = RoundedCornerShape(28.dp)
            )
            .pointerInput(isCompleted) {
                detectTapGestures(
                    onPress = {
                        if (!isCompleted) {
                            Log.d("SessionCard", "Card pressed!") // 👈 Debug
                            isPressed = true
                            val released = tryAwaitRelease()
                            isPressed = false

                            if (released) {
                                Log.d("SessionCard", "Card clicked! Type: $type") // 👈 Debug
                                onClick() // 👈 onClick çağır
                            }
                        } else {
                            Log.d("SessionCard", "Card is completed, click disabled")
                        }
                    }
                )
            }
    ) {
        // Shimmer overlay for active cards
        if (!isCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            start = Offset(shimmer * 1000f, shimmer * 1000f),
                            end = Offset((shimmer + 0.3f) * 1000f, (shimmer + 0.3f) * 1000f)
                        )
                    )
            )
        }

        // Decorative circles
        if (!isCompleted) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                gradient[1].copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = 40.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                gradient[0].copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (isCompleted) {
                // Completed state overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(12.dp))
                )

                // Success badge
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(90.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF43E97B).copy(alpha = 0.6f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF43E97B).copy(alpha = 0.3f),
                                    Color(0xFF38F9D7).copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF43E97B)
                        )
                        Text(
                            text = stringResource(R.string.completed),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF43E97B)
                        )
                    }
                }
            }

            // Main content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Emoji with glow
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isCompleted) {
                            // Glow behind emoji
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.15f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        Text(
                            text = emoji,
                            fontSize = 52.sp,
                            modifier = if (!isCompleted) {
                                Modifier
                                    .floating(offsetY = 6f, duration = 2000)
                                    .graphicsLayer {
                                        shadowElevation = 8.dp.toPx()
                                    }
                            } else {
                                Modifier.alpha(0.5f)
                            }
                        )
                    }

                    // Title
                    Text(
                        text = title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface else Color.White,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.alpha(if (isCompleted) 0.6f else 1f)
                    )

                    // Description
                    Text(
                        text = if (isCompleted) "${stringResource(R.string.completed)} ✓" else description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.3.sp
                    )
                }

                // Action button (only for uncompleted)
                if (!isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Color.White.copy(alpha = 0.3f)
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.25f),
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.4f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Start",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun CompletionBadge(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "badge")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0xFF43E97B).copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                )
            )
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 20.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🎉", fontSize = 32.sp)
            Text(
                text = stringResource(R.string.daily_done),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.daily_done_desc),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}