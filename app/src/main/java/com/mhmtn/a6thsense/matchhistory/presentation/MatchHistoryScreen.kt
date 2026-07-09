package com.mhmtn.a6thsense.matchhistory.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.matchhistory.presentation.components.MatchHistoryCard
import com.mhmtn.a6thsense.matchhistory.presentation.components.PremiumGateCard

@Composable
fun MatchHistoryScreen(
    state: MatchHistoryContract.State,
    onAction: (MatchHistoryContract.Action) -> Unit,
    onBackClick: () -> Unit
){// Source code removed.}