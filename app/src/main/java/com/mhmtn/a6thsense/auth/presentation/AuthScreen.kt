package com.mhmtn.a6thsense.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mhmtn.a6thsense.auth.components.AnimatedBackgroundParticles
import com.mhmtn.a6thsense.auth.components.AnimatedLogo
import com.mhmtn.a6thsense.auth.components.AnimatedSubtitle
import com.mhmtn.a6thsense.auth.components.FeatureHighlights
import com.mhmtn.a6thsense.auth.components.FloatingOrbs
import com.mhmtn.a6thsense.auth.components.GoogleSignInButton
import com.mhmtn.a6thsense.ui.theme.MeditationDeepPurple
import com.mhmtn.a6thsense.ui.theme.MeditationLavender

@Composable
fun AuthScreen(
    state: AuthContract.State,
    googleSignInClient: GoogleSignInClient,
    onAction: (AuthContract.Action) -> Unit
) {// Source code removed.}
