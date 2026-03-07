package com.mhmtn.a6thsense.auth.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.ui.theme.MeditationAccent
import com.mhmtn.a6thsense.ui.theme.MeditationGlow

@Composable
fun FloatingOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    // Large orb
    val orb1Y by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1Y"
    )

    Box(
        modifier = Modifier
            .offset(x = 50.dp, y = orb1Y.dp)
            .size(150.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MeditationGlow.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
            .blur(40.dp)
    )

    // Small orb
    val orb2Y by infiniteTransition.animateFloat(
        initialValue = 400f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2Y"
    )

    Box(
        modifier = Modifier
            .offset(x = 280.dp, y = orb2Y.dp)
            .size(100.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MeditationAccent.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
            .blur(30.dp)
    )
}
