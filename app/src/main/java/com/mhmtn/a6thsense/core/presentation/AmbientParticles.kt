package com.mhmtn.a6thsense.core.presentation

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
fun AmbientParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    (0..8).forEach { index ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = (index * 100).toFloat(),
            targetValue = (index * 100 + 800).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 8000 + index * 1000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particleY$index"
        )

        val offsetX by infiniteTransition.animateFloat(
            initialValue = (index * 40).toFloat(),
            targetValue = (index * 40 + 30).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 500,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particleX$index"
        )

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size((4 + index % 3).dp)
                .background(
                    MeditationSoftLavender.copy(alpha = 0.3f),
                    CircleShape
                )
        )
    }
}