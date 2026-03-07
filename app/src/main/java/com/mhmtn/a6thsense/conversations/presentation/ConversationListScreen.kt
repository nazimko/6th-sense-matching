package com.mhmtn.a6thsense.conversations.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.mhmtn.a6thsense.R
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.conversations.presentation.components.ConversationListItem
import com.mhmtn.a6thsense.core.presentation.ConversationsScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView

@Composable
fun ConversationListScreen(
    state: ConversationListContract.State,
    onAction: (ConversationListContract.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0C29),
                        Color(0xFF1A1A2E),
                        Color(0xFF24243E)
                    )
                )
            )
    ) {
        when {
            state.isLoading -> {
                ConversationsScreenSkeleton()
            }

            state.error != null -> {
                NetworkErrorView(
                    message = state.error,
                    onRetry = { onAction(ConversationListContract.Action.Reload) }
                )
            }

            state.conversations.isEmpty() -> {
                // Boş liste
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "💬", fontSize = 48.sp)
                    Text(
                        text = R.string.no_coversation_text.toString(),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                    Text(
                        text = R.string.no_conversation_subtext.toString(),
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Header
                    item {
                        Text(
                            text = R.string.conversations_text.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(
                                start = 24.dp,
                                top = 48.dp,
                                bottom = 24.dp
                            )
                        )
                    }

                    items(
                        items = state.conversations,
                        key = { it.conversationId }
                    ) { item ->
                        ConversationListItem(
                            item = item,
                            onClick = {
                                onAction(
                                    ConversationListContract.Action.OnConversationClick(item)
                                )
                            }
                        )
                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.05f),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}