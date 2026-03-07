package com.mhmtn.a6thsense.invite.domain

data class ReferralReward(
    val premiumDays: Int = 7,
    val extraSwipes: Int = 5,
    val badgeUnlocked: Boolean = false
)
