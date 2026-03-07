package com.mhmtn.a6thsense.billing.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.billing.domain.SubscriptionPlan
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BillingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : BillingRepository, PurchasesUpdatedListener {

    private val _availablePlans = MutableStateFlow<List<SubscriptionPlan>>(emptyList())
    override val availablePlans = _availablePlans.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    override val isPremium = _isPremium.asStateFlow()

    private lateinit var billingClient: BillingClient
    private var productDetails: List<ProductDetails> = emptyList()

    companion object {
        const val MONTHLY_PRODUCT_ID = "premium_monthly"
        const val YEARLY_PRODUCT_ID = "premium_yearly"
    }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Bağlantı başarılı
                }
            }

            override fun onBillingServiceDisconnected() {
                // Bağlantı koptu - yeniden bağlan
                setupBillingClient()
            }
        })
    }

    override suspend fun queryProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MONTHLY_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(YEARLY_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        suspendCancellableCoroutine { continuation ->
            billingClient.queryProductDetailsAsync(params) { result, details ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetails = details
                    _availablePlans.value = details.map { product ->
                        val offer = product.subscriptionOfferDetails?.firstOrNull()
                        val price = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()

                        SubscriptionPlan(
                            productId = product.productId,
                            title = product.title,
                            price = price?.formattedPrice ?: "",
                            description = product.description
                        )
                    }
                }
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun purchaseSubscription(productId: String, activity: Activity?): Boolean {
        val product = productDetails.firstOrNull { it.productId == productId }
            ?: return false

        val offer = product.subscriptionOfferDetails?.firstOrNull()
            ?: return false

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)
            .setOfferToken(offer.offerToken)
            .build()

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        if (activity == null) { // 👈 Activity null kontrolü
            Log.e("BillingRepo", "Activity is null!")
            return false
        }

        val result = billingClient.launchBillingFlow(activity, params)
        return result.responseCode == BillingClient.BillingResponseCode.OK
    }

    override suspend fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(params) { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasPremium = purchases.any { purchase ->
                        purchase.products.any {
                            it == MONTHLY_PRODUCT_ID || it == YEARLY_PRODUCT_ID
                        } && purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                    _isPremium.value = hasPremium

                    // Firestore'a kaydet
                    if (hasPremium) {
                        auth.currentUser?.uid?.let { uid ->
                            firestore.collection("users")
                                .document(uid)
                                .update("isPremium", true)
                        }
                    }
                }
                continuation.resume(Unit)
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Satın alma başarılı
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) { result ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isPremium.value = true
                    // Firestore'a kaydet
                    auth.currentUser?.uid?.let { uid ->
                        firestore.collection("users")
                            .document(uid)
                            .update("isPremium", true)
                    }
                }
            }
        }
    }
}