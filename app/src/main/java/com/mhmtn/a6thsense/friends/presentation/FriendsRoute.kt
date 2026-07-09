package com.mhmtn.a6thsense.friends.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.matchhistory.presentation.components.PremiumGateCard

@Composable
fun FriendsRoute(
    onBackClick: () -> Unit,
    onNavigateToSoulSync: (String) -> Unit,
    onNavigateToInvite: () -> Unit,
    isDark: Boolean,
    onNavigateToPremium: () -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showPremiumGate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FriendsContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                }
                is FriendsContract.Effect.ShowTestResult -> {
                    // Handled in state
                }
                is FriendsContract.Effect.NavigateToSoulSync -> {
                    Log.d("FriendsRoute", "Navigating to Soul Sync room: ${effect.roomId}")
                    onNavigateToSoulSync(effect.roomId)
                }
                FriendsContract.Effect.ShowPremiumGate -> showPremiumGate = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FriendsScreen(
            state = state,
            isDark = isDark,
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
            onInviteClick = onNavigateToInvite
        )

        if (showPremiumGate) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showPremiumGate = false }, // dışarı tıklayınca kapat
                contentAlignment = Alignment.Center
            ) {
                PremiumGateCard(
                    text = stringResource(R.string.premium_gate_title_soul_sync),
                    desc = stringResource(R.string.premium_gate_desc),
                    onUpgradeClick = {
                        showPremiumGate = false
                        onNavigateToPremium()
                    },
                    modifier = Modifier.clickable(enabled = false) {} // card tıklaması arkaya geçmesin
                )
            }
        }
    }
}
