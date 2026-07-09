package com.mhmtn.a6thsense.friends.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.floating
import com.mhmtn.a6thsense.friends.domain.model.CompatibilityTestResult
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTab(
    history: List<CompatibilityTestResult>,
    isLight: Boolean,
    onDeleteTest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTestForDelete by remember { mutableStateOf<CompatibilityTestResult?>(null) }
    var selectedTestForResult by remember { mutableStateOf<CompatibilityTestResult?>(null) }
    val sheetState = rememberModalBottomSheetState()

    if (history.isEmpty()) {
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
                    text = stringResource(R.string.no_test_history),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.history_subtext),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
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
                HistoryCard(
                    result = result,
                    isLight = isLight,
                    isSelected = selectedTestForDelete?.testId == result.testId,
                    onLongClick = { selectedTestForDelete = result },
                    onClick = { selectedTestForResult = result }
                )
            }
        }
    }

    if (selectedTestForDelete != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedTestForDelete = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedTestForDelete?.friendName ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Test Geçmişi Seçenekleri",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                Surface(
                    onClick = {
                        selectedTestForDelete?.let { onDeleteTest(it.testId) }
                        selectedTestForDelete = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    onClick = { selectedTestForDelete = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (selectedTestForResult != null) {
        CompatibilityResultDialog(
            result = selectedTestForResult,
            isLoading = false,
            onDismiss = { selectedTestForResult = null }
        )
    }
}

@Composable
fun HistoryCard(
    result: CompatibilityTestResult,
    isLight: Boolean,
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    var isPressing by remember { mutableStateOf(false) }

    val pressProgress by animateFloatAsState(
        targetValue = if (isPressing || isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = if (isPressing) 600 else 300),
        label = "pressProgress"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.95f else 1f,
        label = "scale"
    )

    val gradientColors = when {
        result.similarity >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
        result.similarity >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
        else -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
    }

    val cardBackground = if (isSelected) {
        if (isLight) Color(0xFFF0F0F0) else Color(0xFF2D2D44)
    } else {
        if (isLight) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val borderAlpha = if (isSelected) 1f else (if (isLight) 0.7f else 0.5f)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isSelected) 0.dp else (if (isLight) 4.dp else 8.dp),
                shape = RoundedCornerShape(20.dp),
                ambientColor = gradientColors[0].copy(alpha = if (isLight) 0.15f else 0.3f),
                spotColor = gradientColors[0].copy(alpha = if (isLight) 0.15f else 0.3f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
            .drawBehind {
                if (pressProgress > 0f) {
                    val color = gradientColors[0].copy(alpha = 0.15f * pressProgress)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color, Color.Transparent),
                            center = center,
                            radius = size.maxDimension * pressProgress * 1.5f
                        ),
                        radius = size.maxDimension * pressProgress * 1.5f,
                        center = center
                    )
                }
            }
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    if (isSelected)
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    else
                        gradientColors.map { it.copy(alpha = borderAlpha) }
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressing = true
                        try {
                            awaitRelease() // Parmak kaldırılana kadar bekle
                        } finally {
                            isPressing = false
                        }
                    },
                    onLongPress = { onLongClick() },
                    onTap = { onClick() }
                )
            }
            .padding(20.dp)
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
                        if (isLight)
                            Brush.linearGradient(gradientColors.map { it.copy(alpha = 0.85f) })
                        else
                            Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (result.friendPhotoUrl.isNotBlank()) {
                    AsyncImage(
                        model = result.friendPhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = result.friendName.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = result.friendName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = timeFormat.format(Date(result.timestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${result.commonSelections.size}/${result.totalSelections} ${stringResource(R.string.common_choices)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            gradientColors.map {
                                it.copy(alpha = if (isLight) 0.15f else 0.2f)
                            }
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            gradientColors.map { it.copy(alpha = if (isLight) 0.9f else 1f) }
                        ),
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
                        color = if (isLight) gradientColors[1] else gradientColors[0]
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

@Preview(showBackground = true)
@Composable
fun HistoryTabPreview() {
    val sampleHistory = listOf(
        CompatibilityTestResult(
            testId = "1",
            friendName = "Mert",
            friendPhotoUrl = "",
            similarity = 92,
            commonSelections = listOf("Option A", "Option B"),
            totalSelections = 5,
            timestamp = System.currentTimeMillis()
        )
    )

    _6thSenseTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HistoryTab(history = sampleHistory, isLight = false, onDeleteTest = {})
        }
    }
}
