package com.mhmtn.a6thsense.activity.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme

@Composable
fun SessionCompleteScreen(
    state: SessionCompleteContract.State,
    matchName: String,
    similarity: Int,
    onEvent: (SessionCompleteContract.Event) -> Unit
) {// Source code removed.}

@Composable
fun FreezeDurationButton(
    duration: Int,
    label: String,
    isSelected: Boolean,
    isPremium: Boolean = false,
    onClick: () -> Unit
) {// Source code removed.}

@Preview(showBackground = true)
@Composable
fun SessionCompleteScreenPreview(){// Source code removed.}

@Preview(showBackground = true)
@Composable
fun SessionCompleteScreenDarkPreview() {// Source code removed.}
