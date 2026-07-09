package com.mhmtn.a6thsense.conversations.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mhmtn.a6thsense.conversations.domain.ConversationItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationListItem(
    isDark: Boolean,
    item: ConversationItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {// Source code removed.}

@Composable
private fun LetterAvatar(name: String) {
    Text(
        text = name.firstOrNull()?.uppercase() ?: "?",
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
}
