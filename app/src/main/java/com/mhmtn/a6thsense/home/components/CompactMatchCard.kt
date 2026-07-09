package com.mhmtn.a6thsense.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.floating
import com.mhmtn.a6thsense.home.domain.TodayMatch
import kotlinx.coroutines.tasks.await

@Composable
fun CompactMatchCard(
    match: TodayMatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "match_glow")

    val glow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(match.userId) {
        try {
            val userDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(match.userId)
                .get()
                .await()
            
            profileImageUrl = userDoc.getString("profileImageUrl")
                ?: userDoc.getString("photoUrl")
        } catch (e: Exception) {
            profileImageUrl = match.userPhoto
        }
    }

    // Similarity'ye göre gradient seç
    val gradient = when {
        match.similarity >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
        match.similarity >= 60 -> listOf(Color(0xFF667eea), Color(0xFF764ba2))
        match.similarity >= 40 -> listOf(Color(0xFFf093fb), Color(0xFFf5576c))
        else -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
    }

    Box(
        modifier = modifier
            .width(160.dp)
            .height(200.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = gradient[0].copy(alpha = glow)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(gradient),
                shape = RoundedCornerShape(24.dp)
            )
            .bounceClick(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = gradient[0].copy(alpha = 0.6f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(gradient)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val finalPhotoUrl = if (!profileImageUrl.isNullOrBlank()) profileImageUrl else match.userPhoto
                if (!finalPhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = finalPhotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = match.userName.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.floating(offsetY = 4f, duration = 2000)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Name
            Text(
                text = match.userName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            // Similarity badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(gradient.map { it.copy(alpha = 0.2f) })
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(gradient),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            match.similarity >= 80 -> "🔥"
                            match.similarity >= 60 -> "✨"
                            match.similarity >= 40 -> "💫"
                            else -> "🌟"
                        },
                        fontSize = 16.sp
                    )
                    Text(
                        text = "%${match.similarity}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = gradient[0]
                    )
                }
            }

            // Session type indicator
            Text(
                text = when (match.sessionType) {
                    DailyActivityContract.SessionType.INTUITION -> "🌙 Soul Sync"
                    DailyActivityContract.SessionType.PREFERENCE -> "✨ Vibe Check"
                },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
