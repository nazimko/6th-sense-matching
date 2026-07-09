package com.mhmtn.a6thsense.messaging.presentation.components

import android.media.SoundPool
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.mhmtn.a6thsense.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick

@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    value: String,
    isLimitReached: Boolean = false,
    isSending: Boolean = false,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = remember {
        soundPool.load(context, R.raw.sent, 1) // res/raw/send_sound.mp3
    }

    // Composable dispose olunca kaynağı serbest bırak
    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    val playAndSend = {
        soundPool.play(soundId, 0.3f, 0.3f, 0, 0, 1f)
        onSend()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.surface)  // Color(0xFF1A1A2E) → surface
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = colorScheme.surfaceVariant,  // Color(0xFF2A2A3E) → surfaceVariant
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.3f),  // Color.White.copy(0.1f) → outline
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            enabled = !isSending,
            textStyle = TextStyle(
                color = colorScheme.onSurface,  // Color.White → onSurface
                fontSize = 15.sp
            ),
            cursorBrush = SolidColor(colorScheme.onSurface),  // Color.White → onSurface
            singleLine = false,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { playAndSend() }),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.type_message_here),
                        color = colorScheme.onSurface.copy(alpha = 0.4f),  // Color.White.copy(0.4f)
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (value.isNotBlank() && !isLimitReached)
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))  // marka rengi, sabit
                        )
                    else
                        Brush.linearGradient(
                            listOf(
                                colorScheme.onSurface.copy(alpha = 0.3f),  // Color.Gray → token
                                colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )
                )
                .then(
                    if (value.isNotBlank()) {
                        Modifier.bounceClick(
                            onClick = playAndSend,
                            enabled = value.trim().isNotEmpty() && !isSending
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFF43E97B),  // Color.Green → daha tutarlı marka yeşili
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (value.trim().isNotEmpty())
                        Color.White   // gradient üzerinde, sabit
                    else
                        Color.White.copy(alpha = 0.3f),  // disabled gradient üzerinde, sabit
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}