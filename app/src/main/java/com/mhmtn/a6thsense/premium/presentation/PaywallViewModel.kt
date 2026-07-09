package com.mhmtn.a6thsense.premium.presentation

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.billing.data.BillingRepositoryImpl
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val analyticsHelper: AnalyticsHelper,
    private val billingRepository: BillingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(PaywallContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PaywallContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            billingRepository.queryProducts()
            billingRepository.availablePlans.collect { plans ->

                val monthly = plans.find { it.productId == BillingRepositoryImpl.MONTHLY_PRODUCT_ID }
                val quarterly = plans.find { it.productId == BillingRepositoryImpl.QUARTERLY_PRODUCT_ID }
                val yearly = plans.find { it.productId == BillingRepositoryImpl.YEARLY_PRODUCT_ID }

                val monthlyPriceValue = monthly?.price
                    ?.replace("[^\\d.]".toRegex(), "")
                    ?.toDoubleOrNull()

                val quarterlyPriceValue = quarterly?.price
                    ?.replace("[^\\d.]".toRegex(), "")
                    ?.toDoubleOrNull()

                val yearlyPriceValue = yearly?.price
                    ?.replace("[^\\d.]".toRegex(), "")
                    ?.toDoubleOrNull()

                val quarterlyBadge = if (monthlyPriceValue != null && quarterlyPriceValue != null) {
                    val quarterlyMonthlyEquivalent = quarterlyPriceValue / 3
                    val discount = 1 - (quarterlyMonthlyEquivalent / monthlyPriceValue)
                    val percent = (discount * 100).toInt()
                    if (percent > 0) "%$percent" else null
                } else null

                val yearlyBadge = if (monthlyPriceValue != null && yearlyPriceValue != null) {

                    val yearlyMonthlyEquivalent = yearlyPriceValue / 12
                    val discount = 1 - (yearlyMonthlyEquivalent / monthlyPriceValue)
                    val percent = (discount * 100).toInt()
                    if (percent > 0) "%$percent" else null
                } else null

                _state.update {
                    it.copy(
                        isLoading = false,
                        monthlyPrice = monthly?.price ?: "",
                        quarterlyPrice = quarterly?.price ?: "",
                        yearlyPrice = yearly?.price ?: "",
                        monthlyPeriod = monthly?.billingPeriod ?: "",
                        quarterlyPeriod = quarterly?.billingPeriod ?: "",
                        yearlyPeriod = yearly?.billingPeriod ?: "",
                        quarterlyBadge = quarterlyBadge,
                        yearlyBadge = yearlyBadge
                    )
                }

                plans.forEach { plan ->
                    Log.d("PaywallVM", "Plan: ${plan.productId} - ${plan.price}")
                }
            }
            analyticsHelper.logPaywallViewed("home")
        }

        viewModelScope.launch {
            billingRepository.isPremium.collect { isPremium ->
                if (isPremium) {
                    _effect.emit(PaywallContract.Effect.SubscriptionSuccess)
                }
            }
        }
    }

    fun onAction(action: PaywallContract.Action, activity: Activity? = null) {
        when (action) {
            is PaywallContract.Action.SelectPlan -> {
                _state.update { it.copy(selectedPlan = action.plan) }
            }

            is PaywallContract.Action.Subscribe -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    try {
                        val productId = when (_state.value.selectedPlan) {
                            PaywallContract.Plan.MONTHLY ->
                                BillingRepositoryImpl.MONTHLY_PRODUCT_ID
                            PaywallContract.Plan.QUARTERLY ->
                                BillingRepositoryImpl.QUARTERLY_PRODUCT_ID
                            PaywallContract.Plan.YEARLY ->
                                BillingRepositoryImpl.YEARLY_PRODUCT_ID
                        }
                        analyticsHelper.logSubscriptionStarted(productId) // 👈
                        val success = billingRepository.purchaseSubscription(productId,activity)
                        if (success) {
                            // 👇 Billing flow başarıyla açıldı
                            // Kullanıcı Google Play'de ödeme yapacak
                            // Sonuç onPurchasesUpdated'te gelecek ve isPremium flow'u güncellenecek
                            analyticsHelper.logSubscriptionCompleted(productId)
                        } else {
                            _state.update { it.copy(isLoading = false) }
                            _effect.emit(PaywallContract.Effect.Dismiss)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }

            PaywallContract.Action.Dismiss -> {
                viewModelScope.launch {
                    analyticsHelper.logPaywallDismissed("home")
                    _effect.emit(PaywallContract.Effect.Dismiss)
                }
            }
        }
    }
}