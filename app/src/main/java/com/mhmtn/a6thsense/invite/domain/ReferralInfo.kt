package com.mhmtn.a6thsense.invite.domain

data class ReferralInfo(
    val referralCode: String,
    val referredBy: String? = null,
    val referredUsers: List<String> = emptyList(),
    val totalReferrals: Int = 0,
    val premiumDaysEarned: Int = 0
)
