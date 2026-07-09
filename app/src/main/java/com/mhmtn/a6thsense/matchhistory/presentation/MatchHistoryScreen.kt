package com.mhmtn.a6thsense.matchhistory.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.matchhistory.presentation.components.MatchHistoryCard
import com.mhmtn.a6thsense.matchhistory.presentation.components.PremiumGateCard

@Composable
fun MatchHistoryScreen(
    state: MatchHistoryContract.State,
    onAction: (MatchHistoryContract.Action) -> Unit,
    onBackClick: () -> Unit
) {

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // 👇 Silme Onay Diyaloğu
        if (state.matchToDelete != null) {
            AlertDialog(
                onDismissRequest = { onAction(MatchHistoryContract.Action.DismissDeleteDialog) },
                title = { Text(text = stringResource(R.string.remove_match)) },
                text = { Text(text = stringResource(R.string.unmatch_confirmation_warning, state.matchToDelete.matchedUserName)) },
                confirmButton = {
                    TextButton(
                        onClick = { onAction(MatchHistoryContract.Action.ConfirmDelete) }
                    ) {
                        Text(text = stringResource(R.string.remove), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onAction(MatchHistoryContract.Action.DismissDeleteDialog) }
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Dekoratif bloblar
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    Color(0xFF7B5EA7).copy(alpha = 0.06f),
                    CircleShape
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "${stringResource(R.string.matches)} 💫",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground
                        )
                        Text(
                            text = pluralStringResource(
                                id = R.plurals.matches_plural,
                                count = state.totalCount,
                                state.totalCount
                            ),
                            fontSize = 13.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            when {
                state.isLoading -> {
                    items(4) {
                        MatchHistoryCardSkeleton()
                    }
                }

                state.error != null -> {
                    item {
                        NetworkErrorView(
                            message = state.error,
                            onRetry = { onAction(MatchHistoryContract.Action.Reload) }
                        )
                    }
                }

                state.matches.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "🔮", fontSize = 64.sp)
                                Text(
                                    text = stringResource(R.string.home_no_matches),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onBackground
                                )
                                Text(
                                    text = stringResource(R.string.home_subtitle),
                                    fontSize = 14.sp,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                else -> {
                    itemsIndexed(
                        items = state.matches,
                        key = { _, item -> item.matchId }
                    ) { index, item ->
                        MatchHistoryCard(
                            item = item,
                            isLoadingConversation = state.loadingConversationId == item.matchId,
                            onMessageClick = {
                                onAction(MatchHistoryContract.Action.OnMessageClick(item))
                            },
                            onSendFriendRequest = {
                                onAction(MatchHistoryContract.Action.OnSendFriendRequest(item.matchedUserId))
                            },
                            onLongClick = {
                                onAction(MatchHistoryContract.Action.OnDeleteMatch(item))
                            },
                            index = index
                        )

                        if (state.hasMoreMatches && !state.isPremium) {
                            PremiumGateCard(
                                text = stringResource(R.string.more_matches),
                                desc = stringResource(R.string.gate_card_subtitle),
                                onUpgradeClick = {
                                    onAction(MatchHistoryContract.Action.OnUpgradeToPremium)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchHistoryCardSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                com.mhmtn.a6thsense.core.presentation.ShimmerBox(
                    modifier = Modifier.size(52.dp),
                    isDark = false,
                    shape = CircleShape
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.mhmtn.a6thsense.core.presentation.ShimmerBox(
                        modifier = Modifier.size(width = 130.dp, height = 18.dp),
                        isDark = false
                    )
                    com.mhmtn.a6thsense.core.presentation.ShimmerBox(
                        modifier = Modifier.size(width = 90.dp, height = 13.dp),
                        isDark = false
                    )
                }
                com.mhmtn.a6thsense.core.presentation.ShimmerBox(
                    modifier = Modifier.size(width = 60.dp, height = 28.dp),
                    isDark = false,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
            com.mhmtn.a6thsense.core.presentation.ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                isDark = false,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
        }
    }
}
