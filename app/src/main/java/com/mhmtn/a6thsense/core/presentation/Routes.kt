package com.mhmtn.a6thsense.core.presentation

import android.net.Uri
import android.util.Log
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import android.util.Base64

object Routes {
    const val HOME = "home"
    const val DAILY_ACTIVITY_INTUITION = "daily_activity/intuition"
    const val DAILY_ACTIVITY_PREFERENCE = "daily_activity/preference"
    const val DAILY = "daily/{sessionType}/{minSimilarity}"
    const val AUTH = "auth"
    const val START = "start"
    const val SIMILARITY = "similarity/{matchId}/{userName}/{userPhoto}/{similarity}"
    const val SESSION_COMPLETE = "session_complete/{matchId}/{sessionId}/{matchName}/{similarity}"
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
    const val CONTACT_US = "contact_us"

    fun dailyActivityRoute(sessionType: DailyActivityContract.SessionType, minSimilarity: Int): String {
        return "daily/${sessionType.name}/$minSimilarity" // ✅ minSimilarity added
    }

    fun sessionCompleteRoute(matchId: String, sessionId: String, matchName: String, similarity: Int): String {
        return "session_complete/$matchId/$sessionId/${Uri.encode(matchName)}/$similarity"
    }

    fun soulSyncRoute(roomId: String?): String {
        if (roomId.isNullOrBlank()) {
            Log.e("Routes", "Room ID is null or blank!")
            return HOME
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
        val encodedName = Uri.encode(matchedUserName)
        val encodedPhoto = Base64.encodeToString(
            matchedUserPhotoUrl.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
        return "messaging/$conversationId/$encodedName/$encodedPhoto/$matchedUserId"
    }
}
