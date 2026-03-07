package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.ui.theme.MeditationAccent
import com.mhmtn.a6thsense.ui.theme.MeditationGlow
import com.mhmtn.a6thsense.ui.theme.MeditationMist
import com.mhmtn.a6thsense.ui.theme.MeditationSoftLavender
@Composable
fun ChoiceCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isDimmed: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 0.dp,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "elevation"
    )

    val haptic = LocalHapticFeedback.current

    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.6f else 0f,
        animationSpec = tween(500),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        // Glow effect
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MeditationGlow.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .blur(24.dp)
            )
        }

        // Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = MeditationAccent.copy(alpha = 0.4f),
                    spotColor = MeditationAccent.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = when {
                            // 👇 Seçili → Dolu gradient
                            isSelected -> listOf(
                                MeditationSoftLavender.copy(alpha = 0.95f),
                                Color.White.copy(alpha = 0.9f)
                            )
                            // 👇 Diğeri seçili → Çok soluk
                            isDimmed -> listOf(
                                MeditationMist.copy(alpha = 0.15f),
                                MeditationMist.copy(alpha = 0.1f)
                            )
                            // 👇 Hiçbiri seçili değil → Boş (transparan arka plan)
                            else -> listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        }
                    )
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    brush = Brush.linearGradient(
                        colors = if (isSelected) {
                            listOf(MeditationAccent, MeditationGlow)
                        } else {
                            // 👇 Boş kutular için border
                            listOf(
                                MeditationSoftLavender.copy(alpha = 0.4f),
                                MeditationSoftLavender.copy(alpha = 0.2f)
                            )
                        }
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable(
                    enabled = !isSelected,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner ripple effect when selected
            if (isSelected) {
                val infiniteTransition = rememberInfiniteTransition(label = "ripple")
                val rippleScale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = EaseInOutCubic),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "rippleScale"
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = rippleScale
                            scaleY = rippleScale
                            alpha = 0.1f
                        }
                        .background(MeditationAccent, CircleShape)
                )
            }
        }
    }
}