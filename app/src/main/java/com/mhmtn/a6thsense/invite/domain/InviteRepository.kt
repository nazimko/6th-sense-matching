package com.mhmtn.a6thsense.invite.domain

import kotlinx.coroutines.flow.Flow

interface InviteRepository {
    suspend fun generateReferralCode(uid: String): String
    suspend fun getReferralInfo(uid: String): Flow<ReferralInfo>
    suspend fun applyReferralCode(uid: String, code: String): Result<ReferralReward>
    suspend fun trackShare(uid: String, platform: String)
    fun getShareLink(referralCode: String): String
}