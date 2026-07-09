package com.mhmtn.a6thsense.core.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SubscriptionCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val billingRepository: BillingRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SubscriptionWorker", "⏰ Running periodic subscription check...")

            // Subscription durumunu kontrol et
            billingRepository.checkSubscriptionStatus()

            Log.d("SubscriptionWorker", "✅ Subscription check completed")
            Result.success()
        } catch (e: Exception) {
            Log.e("SubscriptionWorker", "❌ Error checking subscription: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "subscription_check_worker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<SubscriptionCheckWorker>(
                repeatInterval = 24, // 👈 Her 24 saatte bir
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

            Log.d("SubscriptionWorker", "📅 Periodic subscription check scheduled (every 24 hours)")
        }
    }
}