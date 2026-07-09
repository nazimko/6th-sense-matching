package com.mhmtn.a6thsense

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.mhmtn.a6thsense.core.data.SubscriptionCheckWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class a6thSenseApp : Application(), Configuration.Provider {// Source code removed.}