package com.mhmtn.a6thsense.soulsync.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.mhmtn.a6thsense.R

@Composable
fun CountdownScreen(
    question: String,
    countdown: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val gradientColors = if (isDark) {
        listOf(Color(0xFF0F0C29), Color(0xFF1A1A2E), Color(0xFF24243E))
    } else {
        listOf(Color(0xFFF8F5FF), Color(0xFFF0EBFF), Color(0xFFE8DEFF))
    }

    val scale by animateFloatAsState(
        targetValue = if (countdown % 2 == 0) 1.2f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "countdown_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            // Soru
            Text(
                text = question,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .background(
                        colorScheme.onBackground.copy(alpha = 0.1f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            )

            // Geri sayım
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow circles
                repeat(3) { index ->
                    val size = 200.dp - (index * 40.dp)
                    Box(
                        modifier = Modifier
                            .size(size)
                            .background(
                                Color(0xFF7B5EA7).copy(alpha = 0.2f - (index * 0.05f)),
                                CircleShape
                            )
                    )
                }

                // Number
                Text(
                    text = countdown.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Black,
                    color = colorScheme.onBackground,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFF7B5EA7),
                            blurRadius = 30f
                        )
                    )
                )
            }

            Text(
                text = stringResource(R.string.ready),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}