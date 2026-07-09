package com.mhmtn.a6thsense.home.presentation

import android.content.Context
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.core.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.*
import com.mhmtn.a6thsense.home.components.CompactMatchCard
import com.mhmtn.a6thsense.home.components.ExpandableSessionSection
import com.mhmtn.a6thsense.messaging.presentation.PremiumEntryPoint
import com.mhmtn.a6thsense.home.components.UserStatsCard
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import com.mhmtn.a6thsense.activity.presentation.components.MatchThresholdPicker
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    isDark: Boolean,
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onShowSheet: () -> Unit,
    onAction: (HomeAction) -> Unit
) {// Source code removed.}