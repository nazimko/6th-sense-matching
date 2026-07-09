package com.mhmtn.a6thsense.premium.presentation

import android.app.Activity
import com.mhmtn.a6thsense.premium.domain.PremiumStatus

object PaywallContract {

    data class State(
        val premiumStatus: PremiumStatus = PremiumStatus(),
        val isLoading: Boolean = false,
        val selectedPlan: Plan = Plan.MONTHLY,

        val monthlyPrice: String = "",
        val quarterlyPrice: String = "",
        val yearlyPrice: String = "",

        val quarterlyPeriod: String = "",
        val monthlyPeriod: String = "",
        val yearlyPeriod: String = "",

        val quarterlyBadge: String? = null,
        val yearlyBadge: String? = null,
    )

    enum class Plan {
        MONTHLY, YEARLY, QUARTERLY
    }

    sealed class Action {
        data class SelectPlan(val plan: Plan) : Action()
        data class Subscribe(val activity: Activity? = null) : Action()
        object Dismiss : Action()
    }

    sealed class Effect {
        object SubscriptionSuccess : Effect()
        object Dismiss : Effect()
    }
}