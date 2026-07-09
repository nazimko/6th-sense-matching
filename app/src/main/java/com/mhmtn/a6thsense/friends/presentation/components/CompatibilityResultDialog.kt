package com.mhmtn.a6thsense.friends.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.friends.domain.model.CompatibilityTestResult
import kotlinx.coroutines.delay

@Composable
fun CompatibilityResultDialog(
    result: CompatibilityTestResult?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}