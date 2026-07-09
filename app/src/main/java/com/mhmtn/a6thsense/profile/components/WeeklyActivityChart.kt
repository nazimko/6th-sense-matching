package com.mhmtn.a6thsense.profile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import kotlinx.coroutines.delay

@Composable
fun WeeklyActivityChart(weeklyActivity: List<Boolean>) {

    val days = stringArrayResource(R.array.days_of_week_short).toList()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weeklyActivity.forEachIndexed { index, isActive ->
            val animatedHeight = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                delay(index * 100L)
                animatedHeight.animateTo(
                    targetValue = if (isActive) 1f else 0.2f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedHeight.value)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isActive)
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                                    )
                                else
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF7B5EA7).copy(alpha = 0.2f),
                                            Color(0xFF4568DC).copy(alpha = 0.2f)
                                        )
                                    )
                            )
                    )
                }

                Text(
                    text = days.getOrElse(index) { "" },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}