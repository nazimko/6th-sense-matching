package com.mhmtn.a6thsense.firebase.data

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions.merge
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mhmtn.a6thsense.MainActivity
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.firebase.NotificationHelper.showMatchNotification
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource.Companion.CHANNEL_ID
import com.mhmtn.a6thsense.core.data.DataStoreManager
import com.mhmtn.a6thsense.firebase.NotificationHelper.showMessageNotification
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToFirestore(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]
        
        // 👇 Bildirim Tercihlerini Kontrol Et (Cihaz Tarafı Filtreleme)
        val isAllowed = runBlocking {
            val key = when (type) {
                "MATCH" -> booleanPreferencesKey("match_notifications")
                "SOUL_SYNC_INVITE" -> booleanPreferencesKey("match_notifications") // Soul sync de eşleşme bildirimine dahil edilebilir
                else -> booleanPreferencesKey("message_notifications")
            }
            DataStoreManager.getDataStore(applicationContext).data.first()[key] ?: true
        }

        if (!isAllowed) {
            Log.d("FCM", "Bildirim kullanıcı tarafından engellendiği için gösterilmedi (Type: $type)")
            return
        }

        when (type) {
            "MATCH" -> {
                val title = message.notification?.title ?: getString(R.string.matched)
                val body = message.notification?.body ?: getString(R.string.match_notification_body)
                showMatchNotification(applicationContext, title, body)
            }
            "SOUL_SYNC_INVITE" -> {
                val roomId = message.data["roomId"] ?: return
                val matchId = message.data["matchId"] ?: return
                showSoulSyncInviteNotification(
                    context = applicationContext,
                    title = message.notification?.title ?: getString(R.string.soul_sync_notification_title),
                    body = message.notification?.body ?: "",
                    roomId = roomId,
                    matchId = matchId
                )
            }

            "MESSAGE" -> {
                val matchId = message.data["conversationId"] ?: return
                val title = message.notification?.title ?: "New Message"
                val body = message.notification?.body ?: ""
                showMessageNotification(applicationContext, title, body, matchId)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showSoulSyncInviteNotification(context: Context, title: String, body: String, roomId: String, matchId: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "soul_sync")
            putExtra("room_id", roomId)
            putExtra("match_id", matchId)
        }

        val pendingIntent = PendingIntent.getActivity(context, roomId.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, "soul_sync") // Updated channel ID
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(context.getColor(R.color.purple_700))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(R.drawable.ic_wifi, getString(R.string.join_the_game), pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(roomId.hashCode(), notification)
    }

    private fun saveTokenToFirestore(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid).set(mapOf("fcmToken" to token), merge())
    }
}
