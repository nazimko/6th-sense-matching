package com.mhmtn.a6thsense.auth.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.ui.theme.MeditationSoftLavender

@Composable
fun AnimatedBackgroundParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    (0..12).forEach { index ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = (index * 80).toFloat(),
            targetValue = (index * 80 + 1000).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 10000 + index * 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particleY$index"
        )

        val offsetX by infiniteTransition.animateFloat(
            initialValue = (index * 30).toFloat(),
            targetValue = (index * 30 + 40).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000 + index * 600,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particleX$index"
        )

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size((3 + index % 4).dp)
                .background(
                    MeditationSoftLavender.copy(alpha = 0.4f),
                    CircleShape
                )
        )
    }
}