package com.mhmtn.a6thsense.home.components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType
import com.mhmtn.a6thsense.core.presentation.bounceClick

@Composable
fun ExpandableSessionSection(
    hasCompletedIntuition: Boolean,
    hasCompletedPreference: Boolean,
    onStartSession: (SessionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(true) }

    val allCompleted = hasCompletedIntuition && hasCompletedPreference
    val progress = when {
        hasCompletedIntuition && hasCompletedPreference -> 2
        hasCompletedIntuition || hasCompletedPreference -> 1
        else -> 0
    }

    Column(
        modifier = modifier.fillMaxWidth(), // 👇 Sabit padding kaldırıldı
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header (always visible)
        SessionSectionHeader(
            isExpanded = isExpanded,
            progress = progress,
            allCompleted = allCompleted,
            onClick = { isExpanded = !isExpanded }
        )

        // Expandable content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                SessionCard(
                    type = SessionType.INTUITION,
                    title = "Soul Sync",
                    description = stringResource( R.string.session_description_intuition),
                    emoji = "🌙",
                    isCompleted = hasCompletedIntuition,
                    gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                    onClick = { onStartSession(SessionType.INTUITION) }
                )

                SessionCard(
                    type = SessionType.PREFERENCE,
                    title = "Vibe Check",
                    description = stringResource(R.string.session_description_preference),
                    emoji = "✨",
                    isCompleted = hasCompletedPreference,
                    gradient = listOf(Color(0xFFf093fb), Color(0xFFf5576c)),
                    onClick = { onStartSession(SessionType.PREFERENCE) }
                )

                if (allCompleted) {
                    CompletionBadge(modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun SessionSectionHeader(
    isExpanded: Boolean,
    progress: Int,
    allCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "arrow_rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (allCompleted)
                    Color(0xFF43E97B).copy(alpha = 0.5f)
                else
                    Color(0xFF7B5EA7).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (allCompleted)
                    Brush.horizontalGradient(listOf(Color(0xFF43E97B), Color(0xFF38F9D7)))
                else
                    Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))
            )
            .border(
                width = 2.dp,
                color = if (allCompleted) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .bounceClick(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (allCompleted) Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.2f)))
                            else Brush.linearGradient(listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (allCompleted) "🎉" else "📅", fontSize = 28.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.todays_activities),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (allCompleted) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(2) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index < progress) {
                                            if (allCompleted) Color.White else MaterialTheme.colorScheme.onSurface
                                        } else {
                                            if (allCompleted) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        }
                                    )
                            )
                        }

                        Text(
                            text = when (progress) {
                                0 -> stringResource(R.string.no_progress)
                                1 -> stringResource(R.string.one_progress)
                                2 -> stringResource(R.string.two_progress)
                                else -> ""
                            },
                            fontSize = 13.sp,
                            color = if (allCompleted) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .rotate(rotation)
                    .clip(CircleShape)
                    .background(if (allCompleted) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (allCompleted) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
