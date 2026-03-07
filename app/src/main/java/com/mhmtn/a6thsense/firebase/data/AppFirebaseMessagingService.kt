package com.mhmtn.a6thsense.firebase.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions.merge
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mhmtn.a6thsense.MainActivity
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.firebase.NotificationHelper.showMatchNotification
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource.Companion.CHANNEL_ID

class AppFirebaseMessagingService : FirebaseMessagingService() {

    /*
    override fun onCreate() {
        super.onCreate()
        //createNotificationChannel()
    }

     */


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToFirestore(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {

        val type = message.data["type"]

        when (type) {
            "MATCH" -> {
                val title = message.notification?.title ?: R.string.matched.toString()
                val body = message.notification?.body ?: R.string.match_notification_body.toString()
                showMatchNotification(applicationContext, title, body)
            }
            "SOUL_SYNC_INVITE" -> {
                // 👇 Soul Sync davetiyesi
                val roomId = message.data["roomId"] ?: return
                val matchId = message.data["matchId"] ?: return
                val otherUserId = message.data["otherUserId"] ?: ""
                val otherUserName = message.data["otherUserName"] ?: ""

                showSoulSyncInviteNotification(
                    context = applicationContext,
                    title = message.notification?.title ?: R.string.soul_sync_notification_title.toString(),
                    body = message.notification?.body ?: "",
                    roomId = roomId,
                    matchId = matchId
                )
            }
        }
        /*
        if (type == "MATCH") {
            showMatchNotification(context = applicationContext,title, body)
        }

         */
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showSoulSyncInviteNotification(
        context: Context,
        title: String,
        body: String,
        roomId: String,
        matchId: String
    ) {
        // Deep link intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "soul_sync")
            putExtra("room_id", roomId)
            putExtra("match_id", matchId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            roomId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(context.getColor(R.color.purple_700))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            // Action button
            .addAction(
                R.drawable.ic_wifi,
                R.string.join_the_game.toString(),
                pendingIntent
            )
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(roomId.hashCode(), notification)
    }

    private fun saveTokenToFirestore(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(
                mapOf("fcmToken" to token),
                merge()
            )
    }
/*
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Match Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Eşleşme bildirimleri"
            }

            val manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

 */
}