package com.mhmtn.a6thsense.activity.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme

@Composable
fun SessionCompleteScreen(
    state: SessionCompleteContract.State,
    matchName: String,
    similarity: Int,
    onEvent: (SessionCompleteContract.Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Match icon
        Text(
            text = "🎉",
            fontSize = 80.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Harika Bir Eşleşme!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "$matchName ile %$similarity uyumluluk",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Bu eşleşmeyi ne kadar tutmak istersin?",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Duration seçenekleri - 2x2 Grid (veya yan yana Row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FreezeDurationButton(
                duration = 1,
                label = "1 Gün",
                isSelected = state.selectedDuration == 1,
                onClick = { onEvent(SessionCompleteContract.Event.OnDurationChange(1)) }
            )
            
            FreezeDurationButton(
                duration = 3,
                label = "3 Gün",
                isSelected = state.selectedDuration == 3,
                onClick = { onEvent(SessionCompleteContract.Event.OnDurationChange(3)) }
            )
            
            FreezeDurationButton(
                duration = 7,
                label = "7 Gün",
                isSelected = state.selectedDuration == 7,
                isPremium = !state.userIsPremium, // Premium gated
                onClick = { onEvent(SessionCompleteContract.Event.OnDurationChange(7)) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { onEvent(SessionCompleteContract.Event.OnDurationChange(0)) }
        ) {
            Text(
                text = "Hayır, yarın yeni eşleşme istiyorum",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { onEvent(SessionCompleteContract.Event.OnConfirm) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF667eea), modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (state.selectedDuration == 0) "Ana Sayfaya Dön" else "Kaydet ve Devam Et",
                    color = Color(0xFF667eea),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FreezeDurationButton(
    duration: Int,
    label: String,
    isSelected: Boolean,
    isPremium: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) Color(0xFF667eea) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = duration.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF667eea) else Color.White
                )
                
                if (isPremium) {
                    Text(
                        text = "👑",
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SessionCompleteScreenPreview() {
    _6thSenseTheme(darkTheme = false) {
        SessionCompleteScreen(
            state = SessionCompleteContract.State(
                selectedDuration = 3,
                isLoading = false,
                userIsPremium = false
            ),
            matchName = "Elif",
            similarity = 85,
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SessionCompleteScreenDarkPreview() {
    _6thSenseTheme(darkTheme = true) {
        SessionCompleteScreen(
            state = SessionCompleteContract.State(
                selectedDuration = 7,
                isLoading = false,
                userIsPremium = true
            ),
            matchName = "Mert",
            similarity = 92,
            onEvent = {}
        )
    }
}
