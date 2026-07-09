package com.mhmtn.a6thsense.invite.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.mhmtn.a6thsense.R
import java.io.File

class PlatformShareHelper(private val context: Context) {

    // WhatsApp - Direct share with contact picker
    fun shareToWhatsApp(message: String, //link: String
         ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, "$message\n")
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // WhatsApp yüklü değil
                shareToGeneric(message
                    //link
                    )
                Toast.makeText(context, context.getString(R.string.wp_isnt_loaded), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            shareToGeneric(message
                //link
            )
        }
    }

    // Instagram - Stories or DM
    fun shareToInstagram(message: String, //link: String
    ) {
        try {
            // Instagram Stories için background image gerekir
            // Basit text share için generic share kullan
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.instagram.android")
                putExtra(Intent.EXTRA_TEXT, "$message")
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, context.getString(R.string.ig_isnt_loaded), Toast.LENGTH_SHORT).show()
                shareToGeneric(message
                    //link
                )
            }
        } catch (e: Exception) {
            shareToGeneric(message
                //link
            )
        }
    }

    // Facebook - SDK ile multi-select friends (requires Facebook app)
    fun shareToFacebook( link: String, callbackManager: CallbackManager, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Facebook App Invites veya Share Dialog kullan
        val content = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse(link))
            .build()

        // ShareDialog requires Activity context
        // Bu yüzden Activity'den çağrılmalı
        Toast.makeText(context, context.getString(R.string.share_fb_text), Toast.LENGTH_SHORT).show()

        // Alternatif: Browser'da aç
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=$link"))
        context.startActivity(browserIntent)
    }

    // Twitter - Web intent
    fun shareToTwitter(message: String, //link: String
         ) {
        val tweetText = "$message\n"
        val tweetUrl = "https://twitter.com/intent/tweet?text=${Uri.encode(tweetText)}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))
        context.startActivity(intent)
    }

    // SMS - Multiple contacts
    fun shareViaSMS(message: String, //link: String
         ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$message\n")
        }

        val smsIntent = Intent.createChooser(intent, context.getString(R.string.share_via_sms))
        context.startActivity(smsIntent)
    }

    // Email - Multiple recipients
    fun shareViaEmail(subject: String, body: String, //link: String
         ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "$body\n")
        }

        val emailIntent = Intent.createChooser(intent, context.getString(R.string.share_via_email))
        context.startActivity(emailIntent)
    }

    // Generic share (System share sheet)
    fun shareToGeneric(message: String, //link: String
         ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$message\n")
        }

        val chooser = Intent.createChooser(intent, context.getString(R.string.share))
        context.startActivity(chooser)
    }

    // Copy link
    fun copyLink(link: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Referral Link", link)
        clipboard.setPrimaryClip(clip)
    }
}