package com.mhmtn.a6thsense.core.domain.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor() {

    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val crashlytics = Firebase.crashlytics

    // User Properties
    fun setUserId(userId: String) {
        analytics.setUserId(userId)
        crashlytics.setUserId(userId)
    }

    // Events
    fun logEvent(event: AnalyticsEvent) {
        analytics.logEvent(event.name, event.params)
    }

    // Onboarding Events
    fun logOnboardingStarted() {
        logEvent(AnalyticsEvent.OnboardingStarted())
    }

    fun logOnboardingCompleted() {
        logEvent(AnalyticsEvent.OnboardingCompleted())
    }

    // Auth Events
    fun logSignUp(method: String) {
        logEvent(AnalyticsEvent.SignUp(method))
    }

    // Activity Events
    fun logDailyActivityStarted() {
        logEvent(AnalyticsEvent.DailyActivityStarted())
    }

    fun logDailyActivityCompleted() {
        logEvent(AnalyticsEvent.DailyActivityCompleted())
    }

    fun logPhaseCompleted(phase: Int) {
        logEvent(AnalyticsEvent.PhaseCompleted(phase))
    }

    // Match Events
    fun logMatchFound(similarity: String) {
        logEvent(AnalyticsEvent.MatchFound(similarity))
    }

    fun logNoMatchFound() {
        logEvent(AnalyticsEvent.NoMatchFound())
    }

    fun logMatchViewed(similarity: Int) {
        logEvent(AnalyticsEvent.MatchViewed(similarity))
    }

    // Discover Events
    fun logDiscoverSwipeLeft() {
        logEvent(AnalyticsEvent.DiscoverSwipeLeft())
    }

    fun logDiscoverSwipeRight() {
        logEvent(AnalyticsEvent.DiscoverSwipeRight())
    }

    fun logDiscoverLimitReached() {
        logEvent(AnalyticsEvent.DiscoverLimitReached())
    }

    // Messaging Events
    fun logMessageSent() {
        logEvent(AnalyticsEvent.MessageSent())
    }

    fun logConversationOpened() {
        logEvent(AnalyticsEvent.ConversationOpened())
    }

    fun logMessageLimitReached() {
        logEvent(AnalyticsEvent.MessageLimitReached())
    }

    // Premium Events
    fun logPaywallViewed(source: String) {
        logEvent(AnalyticsEvent.PaywallViewed(source))
    }

    fun logPaywallDismissed(source: String) {
        logEvent(AnalyticsEvent.PaywallDismissed(source))
    }

    fun logSubscriptionStarted(plan: String) {
        logEvent(AnalyticsEvent.SubscriptionStarted(plan))
    }

    fun logSubscriptionCompleted(plan: String) {
        logEvent(AnalyticsEvent.SubscriptionCompleted(plan))
    }

    fun logSubscriptionCancelled(plan: String) {
        logEvent(AnalyticsEvent.SubscriptionCancelled(plan))
    }

    // Settings Events
    fun logSettingsOpened() {
        logEvent(AnalyticsEvent.SettingsOpened())
    }

    fun logThemeChanged(isDark: Boolean) {
        logEvent(AnalyticsEvent.ThemeChanged(isDark))
    }

    fun logNotificationToggled(type: String, enabled: Boolean) {
        logEvent(AnalyticsEvent.NotificationToggled(type, enabled))
    }

    // Error Tracking
    fun logError(error: Throwable, context: String) {
        crashlytics.recordException(error)
        crashlytics.log("Error in $context: ${error.message}")
    }

    fun logNonFatalError(message: String, context: String) {
        crashlytics.log("Non-fatal error in $context: $message")
    }

    // Screen Tracking
    fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}