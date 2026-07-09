package com.mhmtn.a6thsense.similarity.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.auth.domain.AuthUser
import kotlinx.coroutines.delay

@Composable
fun SimilarityResultScreen(
    modifier: Modifier,
    similarity: Int,
    currentUser: AuthUser,
    matchedUser: AuthUser,
    roomId: String?,
    isDark: Boolean,
    onNavigateToSoulSync: () -> Unit,
    onAction: (SimilarityContract.Action) -> Unit,
    onContinue: () -> Unit
) {
    val animatedSimilarity = remember { Animatable(0f) }
    val buttonVisible = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val colorScheme = MaterialTheme.colorScheme

    val gradientColors = if (isDark) {
        listOf(Color(0xFF1A1625), Color(0xFF2D1B3D), Color(0xFF1A1625))
    } else {
        listOf(Color(0xFFF0EBFF), Color(0xFFE8DEFF), Color(0xFFF0EBFF))
    }

    LaunchedEffect(Unit) {
        animatedSimilarity.animateTo(
            targetValue = similarity.toFloat(),
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
        delay(400)
        buttonVisible.value = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Üst Kısım - Avatarlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatarWithGlow(
                    user = currentUser,
                    glowColor = Color(0xFF9D4EDD)
                )

                Spacer(modifier = Modifier.width(16.dp))

                UserAvatarWithGlow(
                    user = matchedUser,
                    glowColor = Color(0xFF4568DC)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.spiritual_harmony),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentUser.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = stringResource(R.string.and_text),
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = matchedUser.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            // Orta Kısım - Circle Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF9D4EDD).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                CircularProgressIndicator(
                    progress = { animatedSimilarity.value / 100f },
                    strokeWidth = 10.dp,
                    modifier = Modifier.size(190.dp),
                    color = Color(0xFF9D4EDD),
                    trackColor = colorScheme.surfaceVariant
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${animatedSimilarity.value.toInt()}%",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.similarity_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Butonlar Bölümü
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (roomId != null) {
                    SoulSyncButton(
                        onClick = onNavigateToSoulSync,
                        isDark = isDark,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF7B5EA7),
                                    Color(0xFF4568DC)
                                )
                            )
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onAction(SimilarityContract.Action.OnMessageClick)
                        }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💬", fontSize = 20.sp)
                        Text(
                            text = stringResource(R.string.message_text),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Alt Kısım - Ana Sayfa Butonu
            AnimatedVisibility(
                visible = buttonVisible.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.onBackground.copy(alpha = 0.1f),
                        contentColor = colorScheme.onBackground
                    )
                ) {
                    Text(
                        text = stringResource(R.string.home),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UserAvatarWithGlow(
    user: AuthUser,
    glowColor: Color
) {
    Box(
        modifier = Modifier.size(110.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            glowColor.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(3.dp, glowColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (user.photoUrl?.isNotBlank() == true) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = user.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = user.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = glowColor
                )
            }
        }
    }
}
