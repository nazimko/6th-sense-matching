package com.mhmtn.a6thsense.messaging.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.MessagingScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.core.presentation.PremiumLimitSnackbar
import com.mhmtn.a6thsense.messaging.presentation.components.*
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.flowOf

@Composable
fun MessagingScreen(
    matchedUserName: String,
    matchedUserPhotoUrl: String,
    state: MessagingContract.State,
    listState: LazyListState,
    showPremiumSnackbar: Boolean,
    onDismissSnackbar: () -> Unit,
    onUpgradeClick: () -> Unit,
    onAction: (MessagingContract.Action) -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }

    val premiumStatus by remember {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            EntryPointAccessors
                .fromApplication(
                    context.applicationContext,
                    PremiumEntryPoint::class.java
                )
                .premiumRepository()
                .getPremiumStatus(uid)
        } else {
            flowOf(PremiumStatus())
        }
    }.collectAsStateWithLifecycle(initialValue = PremiumStatus())

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

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
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    onAction(MessagingContract.Action.HideReactionPicker)
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

            ConversationHeader(
                userName = matchedUserName,
                photoUrl = matchedUserPhotoUrl,
                onBackClick = onBackClick,
                onMenuClick = { showMenu = true }
            )

            when {
                state.isLoading -> {
                    MessagingScreenSkeleton()
                }

                state.error != null -> {
                    NetworkErrorView(
                        message = state.error,
                        onRetry = { onAction(MessagingContract.Action.Reload) }
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 8.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.messages,
                            key = { it.id }
                        ) { message ->
                            val isOwnMessage =
                                message.senderId == FirebaseAuth.getInstance().currentUser!!.uid

                            Box {
                                MessageBubble(
                                    message = message,
                                    isOwnMessage = isOwnMessage,
                                    currentUserId = FirebaseAuth.getInstance().currentUser!!.uid,
                                    onLongPress = { messageId ->
                                        onAction(
                                            MessagingContract.Action.ShowReactionPicker(
                                                messageId
                                            )
                                        )
                                    },
                                    onReactionClick = { emoji ->
                                        if (message.reactions[FirebaseAuth.getInstance().currentUser!!.uid] == emoji) {
                                            onAction(MessagingContract.Action.RemoveReaction(message.id))
                                        } else {
                                            onAction(
                                                MessagingContract.Action.AddReaction(
                                                    message.id,
                                                    emoji
                                                )
                                            )
                                        }
                                    }
                                )

                                androidx.compose.animation.AnimatedVisibility(
                                    visible = state.reactionTargetMessageId == message.id,
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut(),
                                    // AnimatedVisibility'nin kendisine de Box içindeki konumunu söyleyebiliriz
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                ) {
                                    EmojiReactionPicker(
                                        onEmojiSelected = { emoji ->
                                            onAction(
                                                MessagingContract.Action.AddReaction(
                                                    message.id,
                                                    emoji
                                                )
                                            )
                                        },
                                        onDismiss = {
                                            onAction(MessagingContract.Action.HideReactionPicker)
                                        }
                                    )
                                }

                            }
                        }
                    }

                    // Input
                    MessageInput(
                        value = state.currentInput,
                        onValueChange = { onAction(MessagingContract.Action.TypeMessage(it)) },
                        onSend = { onAction(MessagingContract.Action.SendMessage) },
                        isLimitReached = !premiumStatus.isPremium &&
                                premiumStatus.dailyMessagesUsed >= premiumStatus.dailyMessageLimit
                    )
                }
            }
        }

        if (showMenu) {
            MessagingMenu(
                onUnmatchClick = {
                    showMenu = false
                    onAction(MessagingContract.Action.OnUnmatchClick)
                },
                onDismiss = { showMenu = false }
            )
        }

        // 👇 YENİ: Unmatch confirmation dialog
        if (state.showUnmatchDialog) {
            UnmatchConfirmationDialog(
                userName = state.matchedUserName,
                onConfirm = {
                    onAction(MessagingContract.Action.OnConfirmUnmatch)
                },
                onDismiss = {
                    onAction(MessagingContract.Action.OnDismissUnmatchDialog)
                }
            )
        }

        AnimatedVisibility(
            visible = showPremiumSnackbar,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .navigationBarsPadding()
        ) {
            PremiumLimitSnackbar(
                message = R.string.premium_limit_reached.toString(),
                onUpgradeClick = onUpgradeClick,
                onDismiss = onDismissSnackbar
            )
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PremiumEntryPoint {
    fun premiumRepository(): PremiumRepository
}