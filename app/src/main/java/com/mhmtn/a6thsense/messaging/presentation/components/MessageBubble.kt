package com.mhmtn.a6thsense.messaging.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import com.mhmtn.a6thsense.R
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.messaging.domain.model.Message
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isOwnMessage: Boolean,
    currentUserId: String,
    onLongPress: (String) -> Unit,
    onReactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    /* ---------------- ANIMATION STATES ---------------- */
    val colorScheme = MaterialTheme.colorScheme

    var isPressed by remember { mutableStateOf(false) }
    var showReactionPulse by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bubble_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    /* ---------------- UI VALUES ---------------- */

    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isOwnMessage) 20.dp else 4.dp,
        bottomEnd = if (isOwnMessage) 4.dp else 20.dp
    )

    val bubbleGradient = if (isOwnMessage) {
        Brush.linearGradient(
            listOf(Color(0xFF7B5EA7), Color(0xFF5E4A7E))
        )
    } else {
        Brush.linearGradient(
            listOf(colorScheme.surface, colorScheme.surface)
        )
    }

    val time = message.timestamp?.let {
        SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(it))
    } ?: stringResource(R.string.sending)


    /* ---------------- LAYOUT ---------------- */

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isOwnMessage)
                Arrangement.End
            else
                Arrangement.Start
        ) {

            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)   // 👈 maksimum genişlik
                    .wrapContentWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .shadow(
                        elevation = if (isOwnMessage) 8.dp else 4.dp,
                        shape = bubbleShape,
                        ambientColor = if (isOwnMessage)
                            Color(0xFF7B5EA7).copy(alpha = 0.3f)
                        else
                            Color.Transparent
                    )
                    .clip(bubbleShape)
                    .background(bubbleGradient)
                    .border(
                        width = if (isOwnMessage) 1.dp else 0.5.dp,
                        color = if (isOwnMessage)
                            Color.White.copy(alpha = 0.15f)
                        else
                            colorScheme.outline.copy(alpha = 0.05f),
                        shape = bubbleShape
                    )
                    .pointerInput(message.id) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onLongPress = {
                                showReactionPulse = true
                                onLongPress(message.id)
                            }
                        )
                    }
            ) {

                /* -------- SHIMMER OVERLAY -------- */

                if (isOwnMessage) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.08f),
                                        Color.Transparent
                                    ),
                                    start = Offset(shimmer * 500f, shimmer * 500f),
                                    end = Offset(
                                        (shimmer + 0.5f) * 500f,
                                        (shimmer + 0.5f) * 500f
                                    )
                                )
                            )
                    )
                }

                /* -------- CONTENT -------- */

                Column(
                    modifier = Modifier
                        //  .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {

                    Text(
                        text = message.text,
                        color = if (isOwnMessage) Color.White else colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        // modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        //modifier = Modifier.fillMaxWidth(),
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //  Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = time,
                            fontSize = 11.sp,
                            color = if (isOwnMessage) Color.White.copy(alpha = 0.6f) else colorScheme.onSurface.copy(alpha = 0.6f),
                        )

                        if (isOwnMessage) {
                            Spacer(modifier = Modifier.width(4.dp))

                            //schedule-doneall
                            Icon(
                                imageVector = when {
                                    message.timestamp == null -> Icons.Default.Send
                                    message.reactions.isNotEmpty() -> Icons.Default.Done
                                    else -> Icons.Default.Done
                                },
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (message.reactions.isNotEmpty())
                                    Color(0xFF43E97B)
                                else
                                    colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        /* ---------------- REACTIONS ---------------- */

        if (message.reactions.isNotEmpty()) {

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isOwnMessage)
                    Arrangement.End
                else
                    Arrangement.Start
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    message.reactions.values
                        .groupBy { it }
                        .forEach { (emoji, users) ->

                            val isSelected =
                                message.reactions[currentUserId] == emoji

                            val reactionScale by animateFloatAsState(
                                targetValue =
                                    if (showReactionPulse && isSelected) 1.2f
                                    else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "reaction_scale"
                            )

                            LaunchedEffect(showReactionPulse) {
                                if (showReactionPulse) {
                                    delay(300)
                                    showReactionPulse = false
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = reactionScale
                                        scaleY = reactionScale
                                    }
                                    .shadow(
                                        elevation =
                                            if (isSelected) 6.dp else 2.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor =
                                            if (isSelected)
                                                Color(0xFF7B5EA7)
                                                    .copy(alpha = 0.4f)
                                            else Color.Transparent
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected)
                                            Color(0xFF7B5EA7)
                                                .copy(alpha = 0.4f)
                                        else
                                            colorScheme.surfaceVariant
                                    )
                                    .bounceClick {
                                        onReactionClick(emoji)
                                    }
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                            ) {
                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 16.sp
                                    )

                                    if (users.size > 1) {
                                        Spacer(
                                            modifier =
                                                Modifier.width(4.dp)
                                        )
                                        Text(
                                            text =
                                                users.size.toString(),
                                            fontSize = 12.sp,
                                            color = colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

@Preview
@Composable
fun MessageBubblePreview() {
    _6thSenseTheme(
        darkTheme = false
    ) {
        MessageBubble(
            message = Message(
                id = "123",
                text = "Hell?",
                senderId = "user123",
                timestamp = System.currentTimeMillis(),
                reactions = mutableMapOf()
            ),
            isOwnMessage = true,
            currentUserId = "454541615",
            onLongPress = { },
            onReactionClick = {},
            modifier = Modifier,
        )
    }
}
