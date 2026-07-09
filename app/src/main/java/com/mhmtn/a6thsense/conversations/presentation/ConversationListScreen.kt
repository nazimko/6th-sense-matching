package com.mhmtn.a6thsense.conversations.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.mhmtn.a6thsense.R
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.conversations.presentation.components.ConversationListItem
import com.mhmtn.a6thsense.core.presentation.ConversationsScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView

@Composable
fun ConversationListScreen(
    state: ConversationListContract.State,
    isDark: Boolean,
    onAction: (ConversationListContract.Action) -> Unit
) {// Source code removed.}
