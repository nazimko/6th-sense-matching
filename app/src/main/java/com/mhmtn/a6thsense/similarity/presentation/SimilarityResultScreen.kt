package com.mhmtn.a6thsense.similarity.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToSoulSync: () -> Unit,
    onAction: (SimilarityContract.Action) -> Unit,
    onContinue: () -> Unit
) {
    val animatedSimilarity = remember { Animatable(0f) }
    val buttonVisible = remember { mutableStateOf(false) }

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
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A1625),
                        Color(0xFF2D1B3D),
                        Color(0xFF1A1625)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Üst Kısım - Avatar ve Metinler
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + scaleIn()
                ) {
                    // İki kullanıcının fotoğraflarını yan yana veya üst üste göstermek için
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current User Avatar
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer glow effect
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFF9D4EDD).copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            AsyncImage(
                                model = currentUser.photoUrl,
                                contentDescription = "Current User",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color(0xFF9D4EDD), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Matched User Avatar
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer glow effect
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFF9D4EDD).copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            AsyncImage(
                                model = matchedUser.photoUrl,
                                contentDescription = "Matched User",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color(0xFF9D4EDD), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = R.string.spiritual_harmony.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB8A4C9),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = matchedUser.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )

            }

            // Orta Kısım - Circle Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                // Outer circle glow
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
                    trackColor = Color(0xFF3D2B4D).copy(alpha = 0.4f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${animatedSimilarity.value.toInt()}%",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = R.string.similarity_text.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB8A4C9),
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (roomId != null) { // roomId varsa göster
                SoulSyncButton(
                    onClick = onNavigateToSoulSync,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
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
                    Text(
                        text = "💬",
                        fontSize = 20.sp
                    )
                    Text(
                        text = R.string.message_text.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Alt Kısım - Button
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
                        containerColor = Color(0xFF9D4EDD),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = R.string.home.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}