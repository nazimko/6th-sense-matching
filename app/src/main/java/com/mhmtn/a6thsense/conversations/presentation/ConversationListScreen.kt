package com.mhmtn.a6thsense.conversations.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.mhmtn.a6thsense.R
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.conversations.presentation.components.ConversationListItem
import com.mhmtn.a6thsense.core.presentation.ConversationsScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView

@Composable
fun ConversationListScreen(
    state: ConversationListContract.State,
    isDark: Boolean,
    onAction: (ConversationListContract.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        Color(0xFF0F0C29), Color(0xFF1A1A2E), Color(0xFF24243E)
                    ) else listOf(
                        Color(0xFFF8F5FF), Color(0xFFF0EBFF), Color(0xFFE8DEFF)
                    )
                )
            )
    ) {
        // 👇 Silme Onay Diyaloğu
        if (state.conversationToDelete != null) {
            AlertDialog(
                onDismissRequest = { onAction(ConversationListContract.Action.DismissDeleteDialog) },
                title = { Text(text = stringResource(R.string.delete_conversation_title)) },
                text = { Text(text = stringResource(R.string.delete_conversation_message, state.conversationToDelete.otherUserName)) },
                confirmButton = {
                    TextButton(
                        onClick = { onAction(ConversationListContract.Action.ConfirmDelete) }
                    ) {
                        Text(text = stringResource(R.string.delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onAction(ConversationListContract.Action.DismissDeleteDialog) }
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        when {
            state.isLoading -> {
                ConversationsScreenSkeleton(isDark = isDark)
            }

            state.error != null -> {
                NetworkErrorView(
                    message = state.error,
                    onRetry = { onAction(ConversationListContract.Action.Reload) }
                )
            }

            state.conversations.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "💬", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_coversation_text),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                    Text(
                        text = stringResource(R.string.no_conversation_subtext),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = stringResource(R.string.conversations_text),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
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
                            isDark = isDark,
                            item = item,
                            onClick = {
                                onAction(
                                    ConversationListContract.Action.OnConversationClick(item)
                                )
                            },
                            // 👇 Basılı tutma eylemi eklendi
                            onLongClick = {
                                onAction(
                                    ConversationListContract.Action.OnDeleteConversation(item)
                                )
                            }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
