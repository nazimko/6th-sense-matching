package com.mhmtn.a6thsense.profile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.profile.domain.Badge

@Composable
fun BadgeItem(badge: Badge) {
    val scale = remember { Animatable(if (badge.isUnlocked) 0.8f else 1f) }

    LaunchedEffect(badge.isUnlocked) {
        if (badge.isUnlocked) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (badge.isUnlocked) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (badge.isUnlocked) 2.dp else 0.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emoji box
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (badge.isUnlocked)
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF7B5EA7).copy(alpha = 0.15f),
                                Color(0xFF4568DC).copy(alpha = 0.15f)
                            )
                        )
                    else
                        Brush.linearGradient(
                            listOf(
                                Color.Gray.copy(alpha = 0.1f),
                                Color.Gray.copy(alpha = 0.1f)
                            )
                        )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (badge.isUnlocked) badge.emoji else "🔒",
                fontSize = 26.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = if (badge.isUnlocked) 1f else 0.4f
                }
            )
        }

        // Bilgi
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = badge.title.asString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = badge.description.asString(),
                fontSize = 12.sp,
                color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            if (!badge.isUnlocked) {
                // Progress
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { badge.currentValue.toFloat() / badge.requiredValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF7B5EA7),
                    trackColor = Color(0xFF7B5EA7).copy(alpha = 0.15f)
                )
                Text(
                    text = "${badge.currentValue}/${badge.requiredValue}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Kazanıldı işareti
        if (badge.isUnlocked) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}