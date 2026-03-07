package com.mhmtn.a6thsense.friends.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.mhmtn.a6thsense.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.friends.domain.model.Friend
import com.mhmtn.a6thsense.friends.presentation.components.CompatibilityResultDialog
import com.mhmtn.a6thsense.friends.presentation.components.FriendsTab
import com.mhmtn.a6thsense.friends.presentation.components.HistoryTab
import com.mhmtn.a6thsense.friends.presentation.components.InviteCodeDialog
import com.mhmtn.a6thsense.friends.presentation.components.RemoveFriendDialog
import com.mhmtn.a6thsense.friends.presentation.components.RequestsTab
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme

@Composable
fun FriendsScreen(
    state: FriendsContract.State,
    onAction: (FriendsContract.Action) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            FriendsHeader(
                onBackClick = onBackClick,
                onInviteClick = { onAction(FriendsContract.Action.OnGenerateInviteCode) }
            )

            // Tab selector
            TabSelector(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(FriendsContract.Action.OnTabSelected(it)) },
                requestCount = state.pendingRequests.size
            )

            // Content
            AnimatedContent(
                targetState = state.selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    FriendsContract.Tab.FRIENDS -> {
                        FriendsTab(
                            friends = state.friends,
                            onFriendClick = { friend ->
                                onAction(FriendsContract.Action.OnRunCompatibilityTest(friend.uid))
                            },
                            onFriendLongPress = { friendshipId ->
                                onAction(FriendsContract.Action.OnRemoveFriend(friendshipId))
                            }
                        )
                    }

                    FriendsContract.Tab.REQUESTS -> {
                        RequestsTab(
                            requests = state.pendingRequests,
                            onAccept = { onAction(FriendsContract.Action.OnAcceptRequest(it)) },
                            onReject = { onAction(FriendsContract.Action.OnRejectRequest(it)) }
                        )
                    }

                    FriendsContract.Tab.HISTORY -> {
                        HistoryTab(
                            history = state.compatibilityHistory
                        )
                    }
                }
            }
        }

        // Invite Dialog
        if (state.showInviteDialog) {
            InviteCodeDialog(
                code = state.inviteCode,
                onDismiss = { onAction(FriendsContract.Action.OnDismissInviteDialog) },
                onAcceptCode = { code ->
                    onAction(FriendsContract.Action.OnAcceptInviteCode(code))
                }
            )
        }

        // Test Result Dialog
        if (state.showTestResultDialog && state.testResult != null) {
            CompatibilityResultDialog(
                result = state.testResult,
                onDismiss = { onAction(FriendsContract.Action.OnDismissTestResultDialog) }
            )
        }

        if (state.showRemoveFriendDialog && state.friendToRemove != null) {
            RemoveFriendDialog(
                friendName = state.friendToRemove.name,
                onConfirm = { onAction(FriendsContract.Action.OnConfirmRemoveFriend) },
                onDismiss = { onAction(FriendsContract.Action.OnDismissRemoveFriendDialog) }
            )
        }

    }
}

@Composable
fun FriendsHeader(
    onBackClick: () -> Unit,
    onInviteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = R.string.friends.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Invite button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                    )
                )
                .bounceClick(onClick = onInviteClick)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "➕", fontSize = 16.sp)
                Text(
                    text = R.string.invite.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TabSelector(
    selectedTab: FriendsContract.Tab,
    onTabSelected: (FriendsContract.Tab) -> Unit,
    requestCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabButton(
            text = R.string.friends.toString(),
            isSelected = selectedTab == FriendsContract.Tab.FRIENDS,
            onClick = { onTabSelected(FriendsContract.Tab.FRIENDS) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = R.string.requests.toString(),
            badge = if (requestCount > 0) requestCount else null,
            isSelected = selectedTab == FriendsContract.Tab.REQUESTS,
            onClick = { onTabSelected(FriendsContract.Tab.REQUESTS) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = R.string.history.toString(),
            isSelected = selectedTab == FriendsContract.Tab.HISTORY,
            onClick = { onTabSelected(FriendsContract.Tab.HISTORY) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    badge: Int? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tab_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF7B5EA7),
                            Color(0xFF4568DC)
                        )
                    )
                else
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF2E2445),
                            Color(0xFF1E2A4F)
                        )
                    )
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color.White
            )

            if (badge != null && badge > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6B6B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badge > 9) "9+" else badge.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun FriendsScreenPreview() {
    _6thSenseTheme {
        FriendsScreen(
            state = FriendsContract.State(
                selectedTab = FriendsContract.Tab.FRIENDS,
                friends = listOf(
                    Friend(
                        uid = "123",
                        name = "Ahmet",
                        isPremium = true,
                        hasCompletedToday = true
                    )
                ),
                pendingRequests = listOf(),
                compatibilityHistory = listOf(),
            ),
            onAction = {},
            onBackClick = {}
        )
    }
}