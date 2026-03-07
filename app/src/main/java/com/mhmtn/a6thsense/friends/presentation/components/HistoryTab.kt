package com.mhmtn.a6thsense.friends.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.mhmtn.a6thsense.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.core.presentation.floating
import com.mhmtn.a6thsense.friends.domain.model.CompatibilityTestResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryTab(
    history: List<CompatibilityTestResult>,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) {
        // Empty state
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📊",
                    fontSize = 80.sp,
                    modifier = Modifier.floating(offsetY = 10f, duration = 2000)
                )

                Text(
                    text = R.string.no_test_history.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = R.string.history_subtext.toString(),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history, key = { it.testId }) { result ->
                HistoryCard(result = result)
            }
        }
    }
}

@Composable
fun HistoryCard(
    result: CompatibilityTestResult,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr")) }

    val gradientColors = when {
        result.similarity >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
        result.similarity >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
        else -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A2A3E).copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(gradientColors.map { it.copy(alpha = 0.5f) }),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = result.friendName.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = result.friendName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = timeFormat.format(Date(result.timestamp)),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${result.commonSelections.size}/${result.totalSelections} ${R.string.common_choices.toString()}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Similarity score
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(gradientColors.map { it.copy(alpha = 0.2f) })
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(gradientColors),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%${result.similarity}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = gradientColors[0]
                    )
                    Text(
                        text = when {
                            result.similarity >= 80 -> "🔥"
                            result.similarity >= 60 -> "✨"
                            else -> "💫"
                        },
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}