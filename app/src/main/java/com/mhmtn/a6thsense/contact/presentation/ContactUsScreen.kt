package com.mhmtn.a6thsense.contact.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.contact.domain.MessageSubject

@Composable
fun ContactUsScreen(
    state: ContactUsContract.State,
    onAction: (ContactUsContract.Action) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}

@Composable
fun SubjectCard(
    subject: MessageSubject,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}