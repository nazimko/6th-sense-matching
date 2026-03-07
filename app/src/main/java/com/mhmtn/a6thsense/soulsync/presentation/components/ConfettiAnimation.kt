package com.mhmtn.a6thsense.soulsync.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConfettiAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val particles = remember {
        List(100) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -0.1f,
                color = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFFA500),
                    Color(0xFF7B5EA7),
                    Color(0xFF4568DC),
                    Color(0xFF43E97B)
                ).random(),
                size = Random.nextFloat() * 10f + 5f,
                rotation = Random.nextFloat() * 360f,
                speedX = (Random.nextFloat() - 0.5f) * 2f,
                speedY = Random.nextFloat() * 2f + 1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = particle.y + (time * particle.speedY)
            if (currentY > 1.1f) return@forEach // Ekrandan çıktı

            val currentX = particle.x + (sin(time * 10f) * particle.speedX * 0.05f)
            val currentRotation = particle.rotation + (time * 360f)

            rotate(currentRotation, Offset(size.width * currentX, size.height * currentY)) {
                drawRect(
                    color = particle.color,
                    topLeft = Offset(
                        size.width * currentX - particle.size / 2,
                        size.height * currentY - particle.size / 2
                    ),
                    size = androidx.compose.ui.geometry.Size(particle.size, particle.size)
                )
            }
        }
    }
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val speedX: Float,
    val speedY: Float
)