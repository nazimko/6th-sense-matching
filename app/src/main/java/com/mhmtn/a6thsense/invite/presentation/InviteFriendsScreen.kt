package com.mhmtn.a6thsense.invite.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.floating
import com.mhmtn.a6thsense.invite.domain.ReferralInfo
import com.mhmtn.a6thsense.invite.presentation.components.CodeInputDialog

@Composable
fun InviteFriendsScreen(
    state: InviteFriendsContract.State,
    onAction: (InviteFriendsContract.Action) -> Unit,
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
            InviteHeader(onBackClick = onBackClick)

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF7B5EA7))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Reward card
                    RewardCard(referralInfo = state.referralInfo)

                    // Share section
                    ShareSection(
                        onPlatformClick = { platform ->
                            onAction(InviteFriendsContract.Action.OnPlatformClick(platform))
                        }
                    )

                    // Referral code card
                    ReferralCodeCard(
                        code = state.referralInfo?.referralCode ?: "",
                        onCopyClick = { onAction(InviteFriendsContract.Action.OnCopyCodeClick) }
                    )

                    // Enter code button
                    if (state.referralInfo?.referredBy == null) {
                        EnterCodeButton(
                            onClick = { onAction(InviteFriendsContract.Action.OnEnterCodeClick) }
                        )
                    }

                    // Stats card
                    StatsCard(referralInfo = state.referralInfo)

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Code input dialog
        if (state.showCodeInput) {
            CodeInputDialog(
                codeInput = state.codeInput,
                error = state.error,
                onCodeChange = { onAction(InviteFriendsContract.Action.OnCodeInputChange(it)) },
                onApply = { onAction(InviteFriendsContract.Action.OnApplyCode) },
                onDismiss = { onAction(InviteFriendsContract.Action.OnDismissCodeInput) }
            )
        }
    }
}

@Composable
fun InviteHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            text = R.string.invite.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun RewardCard(
    referralInfo: ReferralInfo?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reward_shine")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFFFFD700).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2D1B69),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.6f),
                        Color(0xFFFFA500).copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        // Shimmer overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = shimmer * size.width * 2
                }
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Text(
                text = "🎁",
                fontSize = 48.sp,
                modifier = Modifier.floating(offsetY = 8f, duration = 2000)
            )

            Text(
                text = R.string.earn_premium.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // Rewards
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RewardItem(emoji = "👑", text = R.string.reward_item1.toString())
                RewardItem(emoji = "🔄", text = R.string.reward_item2.toString())
                RewardItem(emoji = "✨", text = R.string.reward_item3.toString())
            }
        }
    }
}

@Composable
fun RewardItem(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ShareSection(
    onPlatformClick: (SharePlatform) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = R.string.share.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShareButton(
                emoji = "📱",
                platform = "WhatsApp",
                description = R.string.wp_desc.toString(),
                color = Color(0xFF25D366),
                onClick = { onPlatformClick(SharePlatform.WHATSAPP) }
            )

            ShareButton(
                emoji = "📷",
                platform = "Instagram",
                description = R.string.ig_desc.toString(),
                color = Color(0xFFE4405F),
                onClick = { onPlatformClick(SharePlatform.INSTAGRAM) }
            )

            ShareButton(
                emoji = "👥",
                platform = "Facebook",
                description = R.string.fb_desc.toString(),
                color = Color(0xFF1877F2),
                onClick = { onPlatformClick(SharePlatform.FACEBOOK) }
            )
            ShareButton(
                emoji = "🐦",
                platform = "Twitter",
                description = R.string.tw_desc.toString(),
                color = Color(0xFF1DA1F2),
                onClick = { onPlatformClick(SharePlatform.TWITTER) }
            )

            ShareButton(
                emoji = "💬",
                platform = "Mesaj",
                description = R.string.share_via_sms.toString(),
                color = Color(0xFF0084FF),
                onClick = { onPlatformClick(SharePlatform.MESSAGE) }
            )

            ShareButton(
                emoji = "✉️",
                platform = "Mail",
                description = R.string.share_via_email.toString(),
                color = Color(0xFFEA4335),
                onClick = { onPlatformClick(SharePlatform.EMAIL) }
            )

            ShareButton(
                emoji = "🔗",
                platform = R.string.copy_code.toString(),
                description = R.string.copy_desc.toString(),
                color = Color(0xFF7B5EA7),
                onClick = { onPlatformClick(SharePlatform.OTHER) }
            )
        }
    }
}

@Composable
fun ShareButton(
    emoji: String,
    platform: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .bounceClick(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = platform,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Arrow
            Text(
                text = "→",
                fontSize = 20.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReferralCodeCard(
    code: String,
    onCopyClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "🎟️ ${R.string.my_code.toString()}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2A2A3E))
                .border(
                    width = 1.dp,
                    color = Color(0xFF7B5EA7).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = code,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                            )
                        )
                        .bounceClick(onClick = onCopyClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📋", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun EnterCodeButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF7B5EA7).copy(alpha = 0.2f),
                        Color(0xFF4568DC).copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .bounceClick(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🎫", fontSize = 20.sp)
            Text(
                text = R.string.enter_invite_code.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun StatsCard(
    referralInfo: ReferralInfo?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "📊 ${R.string.statistics.toString()}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2A2A3E).copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatItem(
                    emoji = "👥",
                    label = R.string.invitation_sent.toString(),
                    value = "${referralInfo?.totalReferrals ?: 0} ${R.string.people.toString()}"
                )

                StatItem(
                    emoji = "✅",
                    label = R.string.invitation_accepted.toString(),
                    value = "${referralInfo?.referredUsers?.size ?: 0} ${R.string.people.toString()}"
                )

                StatItem(
                    emoji = "🎁",
                    label = R.string.premium_days_earned.toString(),
                    value = "${referralInfo?.premiumDaysEarned ?: 0} ${R.string.days.toString()}"
                )
            }
        }
    }
}

@Composable
fun StatItem(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}