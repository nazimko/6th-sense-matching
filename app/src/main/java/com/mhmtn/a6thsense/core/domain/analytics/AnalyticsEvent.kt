package com.mhmtn.a6thsense.core.domain.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

sealed class AnalyticsEvent(val name: String, val params: Bundle = Bundle()) {

    // Onboarding
    class OnboardingStarted : AnalyticsEvent("onboarding_started")
    class OnboardingCompleted : AnalyticsEvent("onboarding_completed")

    // Auth
    class SignUp(method: String) : AnalyticsEvent(
        FirebaseAnalytics.Event.SIGN_UP,
        Bundle().apply { putString(FirebaseAnalytics.Param.METHOD, method) }
    )

    // Daily Activity
    class DailyActivityStarted : AnalyticsEvent("daily_activity_started")
    class DailyActivityCompleted() : AnalyticsEvent("daily_activity_completed")
    class PhaseCompleted(phase: Int) : AnalyticsEvent(
        "phase_completed",
        Bundle().apply { putInt("phase", phase) }
    )

    // Matching
    class MatchFound(similarity: String) : AnalyticsEvent(
        "match_found",
        Bundle().apply { putString("similarity", similarity) }
    )
    class NoMatchFound : AnalyticsEvent("no_match_found")
    class MatchViewed(similarity: Int) : AnalyticsEvent(
        "match_viewed",
        Bundle().apply { putInt("similarity", similarity) }
    )

    // Discover
    class DiscoverSwipeLeft : AnalyticsEvent("discover_swipe_left")
    class DiscoverSwipeRight : AnalyticsEvent("discover_swipe_right")
    class DiscoverLimitReached : AnalyticsEvent("discover_limit_reached")

    // Messaging
    class MessageSent : AnalyticsEvent("message_sent")
    class ConversationOpened : AnalyticsEvent("conversation_opened")
    class MessageLimitReached : AnalyticsEvent("message_limit_reached")

    // Premium
    class PaywallViewed(source: String) : AnalyticsEvent(
        "paywall_viewed",
        Bundle().apply { putString("source", source) }
    )
    class PaywallDismissed(source: String) : AnalyticsEvent(
        "paywall_dismissed",
        Bundle().apply { putString("source", source) }
    )
    class SubscriptionStarted(plan: String) : AnalyticsEvent(
        "subscription_started",
        Bundle().apply { putString("plan", plan) }
    )
    class SubscriptionCompleted(plan: String) : AnalyticsEvent(
        FirebaseAnalytics.Event.PURCHASE,
        Bundle().apply {
            putString("plan", plan)
        }
    )
    class SubscriptionCancelled(plan: String) : AnalyticsEvent(
        "subscription_cancelled",
        Bundle().apply { putString("plan", plan) }
    )

    // Settings
    class SettingsOpened : AnalyticsEvent("settings_opened")
    class ThemeChanged(isDark: Boolean) : AnalyticsEvent(
        "theme_changed",
        Bundle().apply { putBoolean("is_dark", isDark) }
    )
    class NotificationToggled(type: String, enabled: Boolean) : AnalyticsEvent(
        "notification_toggled",
        Bundle().apply {
            putString("type", type)
            putBoolean("enabled", enabled)
        }
    )
    class ReferralShared(platform: String) : AnalyticsEvent(
        "referral_shared",
        Bundle().apply { putString("platform", platform) }
    )

    class ReferralApplied(code: String) : AnalyticsEvent(
        "referral_applied",
        Bundle().apply { putString("code", code) }
    )
}