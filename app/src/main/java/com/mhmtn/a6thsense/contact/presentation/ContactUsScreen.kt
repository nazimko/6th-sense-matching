package com.mhmtn.a6thsense.contact.presentation

import com.mhmtn.a6thsense.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mhmtn.a6thsense.contact.domain.MessageSubject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    state: ContactUsContract.State,
    onAction: (ContactUsContract.Action) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contact_us)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Text(
                text = " ${stringResource(R.string.contact_us_header)} 💜",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                lineHeight = 26.sp
            )

            // Subject selection
            Text(
                text = stringResource(R.string.subject),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MessageSubject.values().forEach { subject ->
                    SubjectCard(
                        subject = subject,
                        isSelected = state.selectedSubject == subject,
                        onClick = { onAction(ContactUsContract.Action.OnSubjectSelected(subject)) }
                    )
                }
            }

            // Message input
            Text(
                text = stringResource(R.string.message),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = state.message,
                onValueChange = { onAction(ContactUsContract.Action.OnMessageChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text(stringResource(R.string.message_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = Color(0xFF667eea),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor = Color(0xFF667eea),
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 8
            )

            // Character count
            Text(
                text = "${state.message.length} / 500",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )

            // Error message
            state.error?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFf5576c),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFf5576c).copy(alpha = 0.1f))
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Send button
            Button(
                onClick = { onAction(ContactUsContract.Action.OnSendClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSending && state.message.trim().length >= 10,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF667eea).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.send),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "✉️", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: MessageSubject,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (emoji, title, description) = when (subject) {
        MessageSubject.BUG_REPORT -> Triple(
            "🐛",
            stringResource(R.string.subject_bug_title),
            stringResource(R.string.subject_bug_msg)
        )
        MessageSubject.FEATURE_REQUEST -> Triple(
            "💡",
            stringResource(R.string.subject_feature_title),
            stringResource(R.string.subject_feature_msg)
        )
        MessageSubject.GENERAL_FEEDBACK -> Triple(
            "💬",
            stringResource(R.string.subject_feedback_title),
            stringResource(R.string.subject_feedback_msg)
        )
        MessageSubject.COMPLAINT -> Triple(
            "😔",
            stringResource(R.string.subject_complaint_title),
            stringResource(R.string.subject_complaint_msg)
        )
        MessageSubject.OTHER -> Triple(
            "📝",
            stringResource(R.string.subject_other_title),
            stringResource(R.string.subject_other_msg)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(
                        listOf(Color(0xFF667eea).copy(alpha = 0.3f), Color(0xFF764ba2).copy(alpha = 0.2f))
                    )
                else {
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF667eea) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 28.sp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}