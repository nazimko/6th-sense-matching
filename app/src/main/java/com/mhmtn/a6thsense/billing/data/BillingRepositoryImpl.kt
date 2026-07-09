package com.mhmtn.a6thsense.billing.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.billing.domain.SubscriptionPlan
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
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
        const val QUARTERLY_PRODUCT_ID = "premium_quarterly"
        const val YEARLY_PRODUCT_ID = "premium_yearly"
    }

    init {
        setupBillingClient()
    }
    private val _billingReady = MutableStateFlow(false)

    private fun setupBillingClient() {

        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enablePrepaidPlans()    // Aboneliklerdeki ön ödemeli planlar için kritik
            .enableOneTimeProducts() // Google genellikle bunu da eklemeni bekler (hata almamak için)
            .build()

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _billingReady.value = true
                }
            }

            override fun onBillingServiceDisconnected() {
                _billingReady.value = false
                setupBillingClient()
            }
        })
    }

    override suspend fun queryProducts() {
        withTimeoutOrNull(10_000) { _billingReady.first { it } } ?: return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder().setProductId(MONTHLY_PRODUCT_ID).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(QUARTERLY_PRODUCT_ID).setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId(YEARLY_PRODUCT_ID).setProductType(BillingClient.ProductType.SUBS).build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        try {
            val productDetailsResult = billingClient.queryProductDetails(params)

            if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val details = productDetailsResult.productDetailsList ?: emptyList()
                productDetails = details
                _availablePlans.value = details.map { product ->
                    val offer = product.subscriptionOfferDetails?.firstOrNull()
                    val phase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()
                    SubscriptionPlan(
                        productId = product.productId,
                        title = product.title,
                        price = phase?.formattedPrice ?: "",
                        description = product.description,
                        billingPeriod = phase?.billingPeriod ?: ""
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("Billing", "Ürünler çekilirken hata oluştu: ${e.message}")
        }
    }

    override suspend fun purchaseSubscription(productId: String, activity: Activity?): Boolean {
        val product = productDetails.firstOrNull { it.productId == productId } ?: return false
        val offer = product.subscriptionOfferDetails?.firstOrNull() ?: return false
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(product).setOfferToken(offer.offerToken).build()
        val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productDetailsParams)).build()
        if (activity == null) return false
        return billingClient.launchBillingFlow(activity, params).responseCode == BillingClient.BillingResponseCode.OK
    }

    override fun resetPremiumState() { _isPremium.value = false }

    override suspend fun checkSubscriptionStatus() {
        val uid = auth.currentUser?.uid ?: return
        withTimeoutOrNull(10_000) { _billingReady.first { it } } ?: return

        try {
            val params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
            val purchasesResult = queryPurchasesSync(params)
            
            if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val activePurchase = purchasesResult.purchasesList.firstOrNull { 
                    it.purchaseState == Purchase.PurchaseState.PURCHASED
                }

                if (activePurchase != null) {
                    Log.d("Billing", "✅ User has active sub until: ${activePurchase.purchaseTime}")
                    // 1. Google Play'de aktif abonelik var -> Premium yap ve güncelle
                    _isPremium.value = true
                    updatePremiumStatusInFirestore(true, activePurchase, uid)
                } else {
                    Log.d("Billing", "❌ No active sub. Removing premium status.")
                    // 2. Google Play'de yok -> Önce mevcut durumu kontrol et
                    //val userDoc = firestore.collection("users").document(uid).get().await()
                    //val expiryDate = userDoc.getLong("premiumExpiryDate") ?: 0L
                    //val isRewardValid = expiryDate > System.currentTimeMillis()

                    val userDoc = firestore.collection("users").document(uid).get().await()
                    val premiumType = userDoc.getString("premiumType") ?: ""
                    val expiryDate = userDoc.getLong("premiumExpiryDate") ?: 0L


                    if (premiumType == "reward" && expiryDate > System.currentTimeMillis()) {
                        // Kullanıcı davet koduyla premium kazanmış ve süresi dolmamış
                        Log.d("Billing", "✅ User has active REWARD premium. Keeping active.")
                        _isPremium.value = true
                    } else {
                        Log.d("Billing", "❌ No sub or valid reward. Clearing.")
                        _isPremium.value = false
                        clearPremiumStatusInFirestore(uid)
                    }
                    /*
                    if (isRewardValid) {
                        Log.d("Billing", "✅ User has active reward premium until: $expiryDate. Keeping isPremium=true")
                        _isPremium.value = true
                        // Sadece kontrol zamanını güncelle, isPremium'u değiştirme
                        firestore.collection("users").document(uid).update("lastSubscriptionCheck", FieldValue.serverTimestamp())
                    } else {
                        Log.d("Billing", "❌ No sub or reward. Removing premium status.")
                        _isPremium.value = false
                        clearPremiumStatusInFirestore(uid)
                    }

                     */
                }
            }
        } catch (e: Exception) {
            Log.e("Billing", "Error in checkSubscriptionStatus: ${e.message}")
        }
    }

    private suspend fun queryPurchasesSync(params: QueryPurchasesParams): PurchasesResult = suspendCancellableCoroutine { continuation ->
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            continuation.resume(PurchasesResult(result, purchases))
        }
    }

    private fun updatePremiumStatusInFirestore(isPremium: Boolean, purchase: Purchase?, uid: String) {
        if (!isPremium || purchase == null) return
        
        val productId = purchase.products.firstOrNull() ?: ""
        val billingPeriodMillis = when (productId) {
            MONTHLY_PRODUCT_ID -> 30L * 24 * 60 * 60 * 1000
            YEARLY_PRODUCT_ID -> 365L * 24 * 60 * 60 * 1000
            QUARTERLY_PRODUCT_ID -> 90L * 24 * 60 * 60 * 1000
            else -> 30L * 24 * 60 * 60 * 1000
        }
        
        val updates = mapOf(
            "isPremium" to true,
            "premiumType" to "store_purchase",
            "premiumExpiryDate" to purchase.purchaseTime + billingPeriodMillis,
            "premiumPurchaseToken" to purchase.purchaseToken,
            "isAutoRenewing" to purchase.isAutoRenewing,
            "lastSubscriptionCheck" to FieldValue.serverTimestamp()
        )
        firestore.collection("users").document(uid).update(updates)
    }

    private suspend fun clearPremiumStatusInFirestore(uid: String) {
        // Tekrar bir son kontrol (güvenlik için)
        val deletes = mapOf(
            "isPremium" to false,
            "premiumType" to FieldValue.delete(),
            "premiumExpiryDate" to FieldValue.delete(),
            "premiumPurchaseToken" to FieldValue.delete(),
            "lastSubscriptionCheck" to FieldValue.serverTimestamp()
        )
        firestore.collection("users").document(uid).update(deletes).await()
        Log.d("Billing", "✅ Store subscription ended. Premium status cleared.")

    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (!purchase.isAcknowledged) {
                    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d("Billing", "Purchase Acknowledged")
                        }
                    }
                }
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    _isPremium.value = true
                    auth.currentUser?.uid?.let { uid ->
                        // Burada suspend olmayan bir update çağrısı
                        updatePremiumStatusInFirestore(true, purchase, uid)
                    }
                }
            }
        }
    }
}
