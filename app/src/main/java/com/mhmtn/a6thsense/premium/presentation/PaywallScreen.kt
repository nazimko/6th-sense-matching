package com.mhmtn.a6thsense.premium.presentation

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick

@Composable
fun PaywallScreen(
    state: PaywallContract.State,
    activity: Activity? = null,
    isDark: Boolean,
    onAction: (PaywallContract.Action) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDark) listOf(
                        Color(0xFF0F0C29), Color(0xFF1A1A2E), Color(0xFF2D1B69)
                    ) else listOf(
                        Color(0xFFF8F5FF), Color(0xFFF0EBFF), Color(0xFFE8DEFF)
                    )
                )
            )
    ) {
        // Dekoratif glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7B5EA7).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Kapat butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    onClick = { onAction(PaywallContract.Action.Dismiss) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Hero bölümü
            PaywallHero()

            Spacer(modifier = Modifier.height(32.dp))

            // Özellikler
            PaywallFeatures()

            Spacer(modifier = Modifier.height(32.dp))

            // Plan seçimi
            PlanSelector(
                selectedPlan = state.selectedPlan,
                onPlanSelected = { onAction(PaywallContract.Action.SelectPlan(it)) },
                yearlyBadge = state.yearlyBadge,
                price_monthly = state.monthlyPrice,
                price_yearly = state.yearlyPrice,
                quarterlyBadge = state.quarterlyBadge,
                price_quarterly = state.quarterlyPrice
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Satın al butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        )
                    )
                    .bounceClick {
                        onAction(
                            PaywallContract.Action.Subscribe(activity)
                        )
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (state.selectedPlan) {
                                PaywallContract.Plan.MONTHLY -> stringResource(R.string.start_monthly)
                                PaywallContract.Plan.QUARTERLY -> stringResource(R.string.start_quarterly) // 👈 YENİ
                                PaywallContract.Plan.YEARLY -> stringResource(R.string.start_yearly)
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.cancel_anytime),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PaywallHero() {
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "👑",
            fontSize = 72.sp,
            modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
        )

        Text(
            text = "Aurania Premium",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.paywall_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaywallFeatures() {

    val features = listOf(
        Triple(
            "🔄",
            stringResource(R.string.premium_feature_explore_title),
            stringResource(R.string.premium_feature_explore_desc)
        ),
        Triple(
            "🔮",
            stringResource(R.string.premium_gate_title_soul_sync),
            stringResource(R.string.paywall_deep_desc),
        )
        ,
        Triple(
            "💬",
            stringResource(R.string.premium_feature_messaging_title),
            stringResource(R.string.premium_feature_messaging_desc)
        ),
        Triple(
            "💫",
            stringResource(R.string.premium_feature_matches_title),
            stringResource(R.string.premium_feature_matches_desc)
        ),
        Triple(
            "👑",
            stringResource(R.string.premium_feature_badge_title),
            stringResource(R.string.premium_feature_badge_desc)
        ),
        Triple(
            "🚫",
            stringResource(R.string.premium_feature_ads_title),
            stringResource(R.string.premium_feature_ads_desc)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEachIndexed { index, (emoji, title, desc) ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(index * 100L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF7B5EA7).copy(alpha = 0.3f),
                                        Color(0xFF4568DC).copy(alpha = 0.3f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "✓",
                        color = Color(0xFF43E97B),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanSelector(
    selectedPlan: PaywallContract.Plan,
    price_monthly: String,
    price_quarterly: String,
    price_yearly: String,
    quarterlyBadge: String?,
    yearlyBadge: String?,
    onPlanSelected: (PaywallContract.Plan) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_plan),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Aylık
        PlanCard(
            title = stringResource(R.string.monthly),
            price = price_monthly,
            period = stringResource(R.string.price_per_month),
            badge = null,
            isSelected = selectedPlan == PaywallContract.Plan.MONTHLY,
            onClick = { onPlanSelected(PaywallContract.Plan.MONTHLY) }
        )

        PlanCard(
            title = stringResource(R.string.quarterly), // 👈 YENİ
            price = price_quarterly,
            period = stringResource(R.string.price_per_quarter), // 👈 YENİ
            badge = quarterlyBadge?.let { "$it ${stringResource(R.string.save_badge)}" }
                ?: stringResource(R.string.most_popular), // 👈 Badge yoksa "Most Popular"
            badgeColor = Color(0xFFFFD700), // 👈 Gold badge (Most Popular)
            isSelected = selectedPlan == PaywallContract.Plan.QUARTERLY,
            onClick = { onPlanSelected(PaywallContract.Plan.QUARTERLY) }
        )

        // Yıllık
        PlanCard(
            title = stringResource(R.string.yearly),
            price = price_yearly,
            period = stringResource(R.string.price_per_year),
            badge = yearlyBadge?.let { "$it ${stringResource(R.string.save_badge)}" },
            badgeColor = Color(0xFF43E97B),
            isSelected = selectedPlan == PaywallContract.Plan.YEARLY,
            onClick = { onPlanSelected(PaywallContract.Plan.YEARLY) }
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    badge: String?,
    badgeColor: Color = Color(0xFF43E97B),
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderBrush = if (isSelected) {
        Brush.linearGradient(listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)))
    } else {
        Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp) // TÜM kartların en az bu yükseklikte olmasını sağlar
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .bounceClick(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart // İçeriği dikeyde ortalar
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // SOL: Radio ve Başlık
            Row(
                modifier = Modifier.weight(1f), // Başlığın genişlemesine izin verir
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Radio button (Boyutu sabit)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF7B5EA7) else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color(0xFF7B5EA7) else MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.4f
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(modifier = Modifier
                            .size(8.dp)
                            .background(Color.White, CircleShape))
                    }
                }

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    // maxLines ve overflow kullanmıyoruz, başlık sığmazsa 2 satıra geçer
                    // ve heightIn sayesinde kart yüksekliği hemen değişmez
                )
            }

            // SAĞ: Badge ve Fiyat Grubu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badge?.let {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                // Fiyat ve Periyodu dikeyde hizalamak, "/ 3 months" gibi uzun metinlerde
                // yatay alanı korur ve Row'un patlamasını engeller.
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = price,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                    Text(
                        text = period,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        maxLines = 1,
                        softWrap = false // Metnin kendi içinde kırılmasını engeller
                    )
                }
            }
        }
    }
}