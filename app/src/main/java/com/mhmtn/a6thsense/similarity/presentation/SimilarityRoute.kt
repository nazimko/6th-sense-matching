package com.mhmtn.a6thsense.similarity.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.home.presentation.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun SimilarityRoute(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    isDark: Boolean,
    onNavigateToMessaging: (String) -> Unit,
    onNavigateToSoulSync: (String) -> Unit,
    viewModel: SimilarityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val analyticsHelper = remember {
        EntryPointAccessors
            .fromApplication(context, AnalyticsEntryPoint::class.java)
            .analyticsHelper()
    }

    LaunchedEffect(Unit) {
        analyticsHelper.logScreenView("SimilarityScreen")
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SimilarityContract.Effect.NavigateHome -> onFinish()
                is SimilarityContract.Effect.NavigateToMessaging -> {
                    onNavigateToMessaging(
                        Routes.messagingRoute(
                            conversationId = effect.conversationId,
                            matchedUserName = effect.matchedUserName,
                            matchedUserPhotoUrl = effect.matchedUserPhotoUrl,
                            matchedUserId = effect.matchedUserId
                        )
                    )
                }
            }
        }
    }


    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Green)
        }
        return
    }

    if (state.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = state.error!!.asString(),
                    color = Color.White,
                    fontSize = 16.sp
                )
                Button(onClick = onFinish) {
                    Text(stringResource(R.string.home))
                }
            }
        }
        return
    }

    val currentUser = state.currentUser
    val matchedUser = state.matchedUser

    if (currentUser != null && matchedUser != null) {
        SimilarityResultScreen(
            modifier = modifier,
            similarity = state.similarity,
            currentUser = currentUser,
            matchedUser = matchedUser,
            roomId = state.roomId,
            isDark = isDark,
            onContinue = {
                viewModel.onAction(SimilarityContract.Action.Continue)
            },
            onNavigateToSoulSync = {
                state.roomId?.let { roomId ->
                    onNavigateToSoulSync(roomId)
                }
            },
            onAction = viewModel::onAction
        )
    }
}
