package com.mhmtn.a6thsense.premium.domain

import kotlinx.coroutines.flow.Flow

interface PremiumRepository {
    fun getPremiumStatus(uid: String): Flow<PremiumStatus>
    suspend fun incrementSwipeCount(uid: String)
    suspend fun incrementMessageCount(uid: String)
    suspend fun activatePremium(uid: String) // Gerçek ödeme entegrasyonu sonra
    suspend fun incrementSoulSyncCount(uid: String)
}