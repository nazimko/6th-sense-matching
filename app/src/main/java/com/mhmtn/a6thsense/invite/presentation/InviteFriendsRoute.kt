package com.mhmtn.a6thsense.invite.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.*
import com.mhmtn.a6thsense.R
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.CallbackManager
import com.mhmtn.a6thsense.invite.data.PlatformShareHelper
import com.mhmtn.a6thsense.invite.presentation.components.RewardDialog

@Composable
fun InviteFriendsRoute(
    isDark: Boolean,
    onBackClick: () -> Unit,
    viewModel: InviteFriendsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val shareHelper = remember { PlatformShareHelper(context) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var rewardDays by remember { mutableStateOf(0) }

    // Effect handling
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InviteFriendsContract.Effect.ShareLink -> {
                    shareContent(context = context, message = effect.message.asString(context))
                }

                is InviteFriendsContract.Effect.ShareToPlatform -> { // 👈 Yeni
                    val code = state.referralInfo?.referralCode ?: return@collect
                    //val link = "aurania://invite?code=$code"
                    val playStoreLink = "https://play.google.com/apps/internaltest/4700981964110252736"

                    when (effect.platform) {
                        SharePlatform.WHATSAPP -> {
                            val message = context.getString(R.string.invite_message_simple, code, playStoreLink)
                            shareHelper.shareToWhatsApp(message, //link
                         )
                        }

                        SharePlatform.INSTAGRAM -> {
                            val message = context.getString(R.string.invite_message_simple, code, playStoreLink)
                            shareHelper.shareToInstagram(message, //link
                            )
                        }

                        SharePlatform.FACEBOOK -> {
                            shareHelper.shareToFacebook(
                                link = playStoreLink,
                                callbackManager = CallbackManager.Factory.create(),
                                onSuccess = {
                                    Toast.makeText(context, context.getString(R.string.shared), Toast.LENGTH_SHORT).show()
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        SharePlatform.TWITTER -> {
                            val message = context.getString(R.string.invite_message_simple, code, playStoreLink)
                            shareHelper.shareToTwitter(message, //link
                            )
                        }

                        SharePlatform.MESSAGE -> {
                            val message = context.getString(R.string.invite_message_simple, code, playStoreLink)
                            shareHelper.shareViaSMS(message, //link
                            )
                        }

                        SharePlatform.EMAIL -> {
                            val subject = context.getString(R.string.email_subject)
                            val body = context.getString(R.string.invite_email_simple, code)
                            shareHelper.shareViaEmail(subject, body, //link
                            )
                        }

                        SharePlatform.OTHER -> {
                            shareHelper.copyLink(playStoreLink)
                            Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is InviteFriendsContract.Effect.CopyToClipboard -> {
                    shareHelper.copyLink(effect.text)
                }

                is InviteFriendsContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                }

                is InviteFriendsContract.Effect.ShowReward -> {
                    rewardDays = effect.premiumDays
                    showRewardDialog = true
                }
            }
        }
    }

    InviteFriendsScreen(
        isDark = isDark,
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick
    )

    // Reward dialog
    if (showRewardDialog) {
        RewardDialog(
            premiumDays = rewardDays,
            onDismiss = {
                showRewardDialog = false
                onBackClick() // Geri dön
            }
        )
    }
}

private fun shareContent(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }

    val chooser = Intent.createChooser(intent, context.getString(R.string.share_aurania))
    context.startActivity(chooser)
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Referral Code", text)
    clipboard.setPrimaryClip(clip)
}