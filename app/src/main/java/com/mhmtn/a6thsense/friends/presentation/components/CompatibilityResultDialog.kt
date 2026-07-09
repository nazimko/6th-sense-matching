package com.mhmtn.a6thsense.friends.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.friends.domain.model.CompatibilityTestResult
import kotlinx.coroutines.delay

@Composable
fun CompatibilityResultDialog(
    result: CompatibilityTestResult?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = if (!isLoading) onDismiss else ({})
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                LoadingState()
            } else if (result != null) {
                ResultState(result, onDismiss, modifier)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer { rotationZ = rotation }
                .border(4.dp, Brush.sweepGradient(listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔮", fontSize = 50.sp)
        }

        Text(
            text = stringResource(R.string.checking),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ResultState(
    result: CompatibilityTestResult,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val gradientColors = when {
        result.similarity >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
        result.similarity >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
        else -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
    }

    AnimatedVisibility(
        visible = true,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                )
                .shadow(
                    elevation = 40.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = gradientColors[0].copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(gradientColors),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                // Celebration icon
                Text(
                    text = when {
                        result.similarity >= 90 -> "🔥"
                        result.similarity >= 80 -> "✨"
                        result.similarity >= 60 -> "💫"
                        result.similarity >= 40 -> "🌟"
                        else -> "💭"
                    },
                    fontSize = 80.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )

                // Title
                Text(
                    text = when {
                        result.similarity >= 90 -> stringResource(R.string.comp_legendary_title)
                        result.similarity >= 80 -> stringResource(R.string.comp_perfect_title)
                        result.similarity >= 60 -> stringResource(R.string.comp_strong_title)
                        result.similarity >= 40 -> stringResource(R.string.comp_good_title)
                        else -> stringResource(R.string.comp_different_title)
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = when {
                        result.similarity >= 90 -> stringResource(R.string.comp_legendary_msg)
                        result.similarity >= 80 -> stringResource(R.string.comp_perfect_msg)
                        result.similarity >= 60 -> stringResource(R.string.comp_strong_msg)
                        result.similarity >= 40 -> stringResource(R.string.comp_good_msg)
                        else -> stringResource(R.string.comp_different_msg)
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                // Similarity circle
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            ambientColor = gradientColors[0].copy(alpha = 0.5f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = gradientColors.map { it.copy(alpha = 0.2f) }
                            )
                        )
                        .border(
                            width = 6.dp,
                            brush = Brush.linearGradient(gradientColors),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "%${result.similarity}",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black,
                            color = gradientColors[0]
                        )
                        Text(
                            text = stringResource(R.string.similarity_text),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // 1. Filtreleme işlemini sadece result değiştiğinde bir kez yap
                val filteredChoices = remember(result.commonSelections) {
                    result.commonSelections.filter { choice ->
                        choice.length > 1 // "A", "B" gibi tek karakterlileri burada eliyoruz
                    }
                }

                // 2. Eğer filtrelenmiş liste boşsa, başlığı ve alanı hiç gösterme
                if (filteredChoices.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                gradientColors[0].copy(alpha = 0.5f)
                                            )
                                        )
                                    )
                            )
                            Text(
                                text = stringResource(R.string.common_selections_title),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                letterSpacing = 1.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                gradientColors[0].copy(alpha = 0.5f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            filteredChoices.forEachIndexed { index, selection ->
                                CommonSelectionChip(
                                    text = selection,
                                    gradientColors = gradientColors,
                                    // Animasyon gecikmesini sınırla (çok fazla chip varsa donma yapar)
                                    animationDelay = (index * 50).coerceAtMost(1000)
                                )
                            }
                        }
                    }
                }

                // Close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(gradientColors))
                        .bounceClick(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommonSelectionChip(
    text: String,
    gradientColors: List<Color>,
    animationDelay: Int
) {
    // visible durumunu animasyon başladığında true yapacak şekilde optimize et
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(text) { // Sadece text değişirse veya ilk girişte çalışır
        delay(animationDelay.toLong())
        visible = true
    }

    // 👇 Option ismini yerelleştir
    val displayRes = remember(text) {
        try {
            Option.valueOf(text).displayNameRes
        } catch (e: Exception) {
            null
        }
    }
    val displayText = if (displayRes != null) stringResource(displayRes) else text

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.6f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.linearGradient(
                        gradientColors.map { it.copy(alpha = 0.12f) }
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        gradientColors.map { it.copy(alpha = 0.55f) }
                    ),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = displayText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = gradientColors[0]
            )
        }
    }
}
