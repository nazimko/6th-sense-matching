package com.mhmtn.a6thsense

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import com.mhmtn.a6thsense.core.domain.ConnectivityObserver
import com.mhmtn.a6thsense.onboarding.domain.OnboardingRepository
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {// Source code removed.}

sealed class PendingNavigation {// Source code removed.}