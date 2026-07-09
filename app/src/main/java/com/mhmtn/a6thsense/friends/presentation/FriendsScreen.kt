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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.friends.domain.model.Friend
import com.mhmtn.a6thsense.friends.presentation.components.*
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme

@Composable
fun FriendsScreen(
    state: FriendsContract.State,
    isDark: Boolean,
    onAction: (FriendsContract.Action) -> Unit,
    onBackClick: () -> Unit,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            FriendsHeader(
                onBackClick = onBackClick,
                onInviteClick = onInviteClick
            )

            // Tab selector
            TabSelector(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(FriendsContract.Action.OnTabSelected(it)) },
                requestCount = state.pendingRequests.size,
                isLight = !isDark
            )

            // Content
            AnimatedContent(
                targetState = state.selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "tab_content",
                modifier = Modifier.weight(1f)
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
                            history = state.compatibilityHistory,
                            isLight = !isDark,
                            onDeleteTest = { onAction(FriendsContract.Action.OnDeleteTest(it)) }
                        )
                    }

                    FriendsContract.Tab.SOUL_SYNC -> {
                        SoulSyncTab(
                            friends = state.friends,
                            onStartSoulSync = { onAction(FriendsContract.Action.OnStartSoulSync(it)) }
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
                isLoading = state.isLoading,
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

        // Loading Overlay
        if (state.isLoading && !state.showTestResultDialog) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
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
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = stringResource(R.string.friends),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
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
                    text = stringResource(R.string.invite),
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
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton(
                text = stringResource(R.string.friends),
                isSelected = selectedTab == FriendsContract.Tab.FRIENDS,
                onClick = { onTabSelected(FriendsContract.Tab.FRIENDS) },
                isLight = isLight,
                modifier = Modifier.weight(1f)
            )

            TabButton(
                text = stringResource(R.string.requests),
                badge = if (requestCount > 0) requestCount else null,
                isSelected = selectedTab == FriendsContract.Tab.REQUESTS,
                onClick = { onTabSelected(FriendsContract.Tab.REQUESTS) },
                isLight = isLight,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton(
                text = stringResource(R.string.history),
                isSelected = selectedTab == FriendsContract.Tab.HISTORY,
                onClick = { onTabSelected(FriendsContract.Tab.HISTORY) },
                isLight = isLight,
                modifier = Modifier.weight(1f)
            )

            TabButton(
                text = "Deep Sync",
                isSelected = selectedTab == FriendsContract.Tab.SOUL_SYNC,
                onClick = { onTabSelected(FriendsContract.Tab.SOUL_SYNC) },
                isLight = isLight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    badge: Int? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    isLight: Boolean,
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
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF7B5EA7),
                            Color(0xFF4568DC)
                        )
                    )
                else if (isLight)
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 0.7f)
                        )
                    )
                else
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.3f) 
                        else if (isLight) Color(0xFF7B5EA7).copy(alpha = 0.15f)
                        else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
            
            badge?.let {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color(0xFFE91E63)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it.toString(),
                        color = if (isSelected) Color(0xFF7B5EA7) else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
