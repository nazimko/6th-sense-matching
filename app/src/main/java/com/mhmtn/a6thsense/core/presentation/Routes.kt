package com.mhmtn.a6thsense.core.presentation
import android.net.Uri
import android.util.Log
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

object Routes {
    const val HOME = "home"
    const val DAILY_ACTIVITY_INTUITION = "daily_activity/intuition"
    const val DAILY_ACTIVITY_PREFERENCE = "daily_activity/preference"
    const val DAILY = "daily/{sessionType}"
    const val AUTH = "auth"
    const val START = "start"
    const val SIMILARITY = "similarity/{matchId}/{userName}/{userPhoto}/{similarity}"
    const val NO_MATCH = "no_match"
    const val ALREADY_COMPLETED = "already_completed"
    const val MESSAGING = "messaging/{conversationId}/{matchedUserName}/{matchedUserPhotoUrl}/{matchedUserId}"
    const val CONVERSATIONS = "conversations"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val MATCH_HISTORY = "match_history"
    const val DISCOVER = "discover"
    const val ONBOARDING = "onboarding"
    const val PAYWALL = "paywall"
    const val SOUL_SYNC = "soul_sync/{roomId}"
    const val INVITE_FRIENDS = "invite_friends"
    const val FRIENDS = "friends"

    fun dailyActivityRoute(sessionType: DailyActivityContract.SessionType): String {
        return "daily/${sessionType.name}"
    }
    fun soulSyncRoute(roomId: String?): String {
        if (roomId.isNullOrBlank()) {
            Log.e("Routes", "Room ID is null or blank!")
            return HOME // Fallback
        }
        return "soul_sync/${Uri.encode(roomId)}"
    }

    fun similarityRoute(
        matchId: String,
        otherUserName: String,
        otherUserPhoto: String,
        similarity: Int
    ): String {
        return "similarity/$matchId/${Uri.encode(otherUserName)}/${Uri.encode(otherUserPhoto)}/$similarity"
    }

    fun messagingRoute(
        conversationId: String,
        matchedUserName: String,
        matchedUserPhotoUrl: String = "none",
        matchedUserId: String
    ): String {
        // 👇 URL encode - slash ve özel karakterler route'u bozuyor
        val encodedName = Uri.encode(matchedUserName)
        val encodedPhoto = Uri.encode(matchedUserPhotoUrl)
        return "messaging/$conversationId/$encodedName/$encodedPhoto/$matchedUserId"
    }
}
