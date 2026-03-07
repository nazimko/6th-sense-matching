package com.mhmtn.a6thsense.settings.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.revealFromBottom

@Composable
fun SettingsScreen(
    state: SettingsContract.State,
    onAction: (SettingsContract.Action) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5FF))
    ) {
        // Dekoratif arka plan
        SettingsBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            SettingsHeader(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(8.dp))

            // Profil kartı
            ProfileSection(
                state = state,
                onAction = onAction,
                modifier = Modifier.revealFromBottom(delayMillis = 0)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tema
            SettingsSection(
                title = R.string.theme.toString(),
                modifier = Modifier.revealFromBottom(delayMillis = 100)
            ) {
                ThemeToggleItem(
                    isDark = state.settings.isDarkTheme,
                    onToggle = { onAction(SettingsContract.Action.UpdateTheme(it)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bildirimler
            SettingsSection(
                title = R.string.notifications.toString(),
                modifier = Modifier.revealFromBottom(delayMillis = 200)
            ) {
                NotificationItem(
                    icon = Icons.Outlined.Favorite,
                    title = R.string.match_notifications.toString(),
                    subtitle = R.string.match_notifications_desc.toString(),
                    enabled = state.settings.matchNotificationsEnabled,
                    onToggle = {
                        onAction(SettingsContract.Action.UpdateMatchNotifications(it))
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFE0E0E0)
                )

                NotificationItem(
                    icon = Icons.Outlined.MailOutline,
                    title = R.string.message_notifications.toString(),
                    subtitle = R.string.message_notifications_desc.toString(),
                    enabled = state.settings.messageNotificationsEnabled,
                    onToggle = {
                        onAction(SettingsContract.Action.UpdateMessageNotifications(it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = R.string.privacy.toString(),
                modifier = Modifier.revealFromBottom(delayMillis = 250)
            ) {
                PrivacySettingItem(
                    icon = Icons.Filled.Search,
                    title = R.string.show_in_discover.toString(),
                    description = R.string.show_in_discover_desc.toString(),
                    isEnabled = state.showInDiscover,
                    onToggle = { onAction(SettingsContract.Action.OnShowInDiscoverToggle) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingsBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .background(
                    Color(0xFF7B5EA7).copy(alpha = 0.06f),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
                .background(
                    Color(0xFF4568DC).copy(alpha = 0.05f),
                    CircleShape
                )
        )
    }
}

@Composable
private fun SettingsHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF2D1B69)
            )
        }

        Text(
            text = R.string.settings.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )
    }
}

@Composable
private fun ProfileSection(
    state: SettingsContract.State,
    onAction: (SettingsContract.Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isEditingName) {
        if (state.isEditingName) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (state.settings.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = state.settings.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = state.settings.displayName
                            .firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // İsim + edit
            Column(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.isEditingName,
                    label = "name_edit"
                ) { isEditing ->
                    if (isEditing) {
                        // Edit modu
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            BasicTextField(
                                value = state.nameInput,
                                onValueChange = {
                                    onAction(SettingsContract.Action.TypeName(it))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3F0FF))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = Color(0xFF2D1B69),
                                    fontWeight = FontWeight.SemiBold
                                ),
                                cursorBrush = SolidColor(Color(0xFF7B5EA7)),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onAction(SettingsContract.Action.SaveName)
                                    }
                                )
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // İptal
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF5F5F5))
                                        .bounceClick {
                                            onAction(SettingsContract.Action.CancelEditingName)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = R.string.cancel.toString(),
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Kaydet
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(0xFF7B5EA7),
                                                    Color(0xFF4568DC)
                                                )
                                            )
                                        )
                                        .bounceClick {
                                            onAction(SettingsContract.Action.SaveName)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    if (state.isSavingName) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = R.string.save.toString(),
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Görüntüleme modu
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = state.settings.displayName.ifBlank { R.string.anonymous.toString() },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D1B69)
                                )
                                Text(
                                    text = R.string.edit_name.toString(),
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                            }

                            // Edit butonu
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3F0FF))
                                    .bounceClick {
                                        onAction(SettingsContract.Action.StartEditingName)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color(0xFF7B5EA7),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF9E9E9E),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun ThemeToggleItem(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // İkon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isDark)
                            Color(0xFF1A1A2E)
                        else
                            Color(0xFFFFF9C4)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isDark) "🌙" else "☀️",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = if (isDark) R.string.dark_theme.toString() else R.string.light_theme.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = if (isDark) R.string.dark_desc.toString() else R.string.light_desc.toString(),
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        // Custom toggle
        ThemeToggleSwitch(
            isChecked = isDark,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun ThemeToggleSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "thumb"
    )

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isChecked)
                    Brush.linearGradient(
                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                    )
                else
                    Brush.linearGradient(
                        listOf(Color(0xFFE0E0E0), Color(0xFFE0E0E0))
                    )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!isChecked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = (4 + thumbOffset * 24).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun NotificationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (enabled)
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF7B5EA7).copy(alpha = 0.15f),
                                    Color(0xFF4568DC).copy(alpha = 0.15f)
                                )
                            )
                        else
                            Brush.linearGradient(
                                listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
                            )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) Color(0xFF7B5EA7) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        ThemeToggleSwitch(
            isChecked = enabled,
            onToggle
        )
    }
}

@Composable
fun PrivacySettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isEnabled)
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF7B5EA7).copy(alpha = 0.15f),
                                    Color(0xFF4568DC).copy(alpha = 0.15f)
                                )
                            )
                        else
                            Brush.linearGradient(
                                listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
                            )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) Color(0xFF7B5EA7) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D1B69)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        ThemeToggleSwitch(
            isChecked = isEnabled,
            onToggle
        )
    }
}