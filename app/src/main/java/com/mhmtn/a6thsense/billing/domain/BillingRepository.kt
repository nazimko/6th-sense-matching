package com.mhmtn.a6thsense.billing.domain

import android.app.Activity
import kotlinx.coroutines.flow.Flow

data class SubscriptionPlan(
    val productId: String,
    val title: String,
    val price: String,
    val description: String
)

interface BillingRepository {
    val availablePlans: Flow<List<SubscriptionPlan>>
    val isPremium: Flow<Boolean>

    suspend fun queryProducts()
    suspend fun purchaseSubscription(productId: String, activity: Activity?): Boolean
    suspend fun restorePurchases()
}