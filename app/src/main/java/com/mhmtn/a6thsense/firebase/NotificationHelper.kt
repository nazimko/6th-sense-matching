package com.mhmtn.a6thsense.firebase

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mhmtn.a6thsense.MainActivity
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource.Companion.CHANNEL_ID
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource.Companion.MATCH_NOTIFICATION_ID

object NotificationHelper {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMatchNotification(
        context: Context,
        title: String,
        body: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", Routes.SIMILARITY)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent) // 👈 TIKLAMA
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getColor(R.color.purple_700))
            .build()

        NotificationManagerCompat.from(context)
            .notify(MATCH_NOTIFICATION_ID, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMessageNotification(
        context: Context,
        title: String,
        body: String,
        matchId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "chat")
            putExtra("match_id", matchId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            matchId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "message_channel")
            .setSmallIcon(R.drawable.ic_notification_mail)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getColor(R.color.purple_700))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        NotificationManagerCompat.from(context)
            .notify(matchId.hashCode(), notification)
    }

}
