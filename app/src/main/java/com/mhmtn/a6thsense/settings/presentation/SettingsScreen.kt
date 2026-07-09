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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
import androidx.compose.ui.res.stringResource
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

    if (state.showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = { onAction(SettingsContract.Action.OnLogoutConfirm) },
            onDismiss = { onAction(SettingsContract.Action.OnLogoutDismiss) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                title = stringResource(R.string.theme),
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
                title = stringResource(R.string.notifications),
                modifier = Modifier.revealFromBottom(delayMillis = 200)
            ) {
                NotificationItem(
                    icon = Icons.Outlined.Favorite,
                    title = stringResource(R.string.match_notifications),
                    subtitle = stringResource(R.string.match_notifications_desc),
                    enabled = state.settings.matchNotificationsEnabled,
                    onToggle = {
                        onAction(SettingsContract.Action.UpdateMatchNotifications(it))
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline
                )

                NotificationItem(
                    icon = Icons.Outlined.MailOutline,
                    title = stringResource(R.string.message_notifications),
                    subtitle = stringResource(R.string.message_notifications_desc),
                    enabled = state.settings.messageNotificationsEnabled,
                    onToggle = {
                        onAction(SettingsContract.Action.UpdateMessageNotifications(it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = stringResource(R.string.privacy),
                modifier = Modifier.revealFromBottom(delayMillis = 250)
            ) {
                PrivacySettingItem(
                    icon = Icons.Filled.Search,
                    title = stringResource(R.string.show_in_discover),
                    description = stringResource(R.string.show_in_discover_desc),
                    isEnabled = state.showInDiscover,
                    onToggle = { onAction(SettingsContract.Action.OnShowInDiscoverToggle) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = stringResource(R.string.contact_us),
                modifier = Modifier.revealFromBottom(delayMillis = 300)
            ) {
                SettingsItemWithEmoji(
                    icon = Icons.Filled.Settings,
                    title = stringResource(R.string.contact_us),
                    description = stringResource(R.string.contact_us_subtitle),
                    onClick = { onAction(SettingsContract.Action.OnContactUsClick) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            LogoutButton(
                isLoading = state.isLoggingOut,
                onClick = { onAction(SettingsContract.Action.OnLogoutClick) },
                modifier = Modifier.revealFromBottom(delayMillis = 350)
            )

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
    val colorScheme = MaterialTheme.colorScheme

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
                .background(colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(R.string.settings),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
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
    val colorScheme = MaterialTheme.colorScheme

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
            .background(colorScheme.surface)
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
                                    .background(colorScheme.surfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = colorScheme.onSurface,
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
                                        .background(colorScheme.surfaceVariant)
                                        .bounceClick {
                                            onAction(SettingsContract.Action.CancelEditingName)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.cancel),
                                        fontSize = 13.sp,
                                        color = colorScheme.onSurfaceVariant
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
                                            text = stringResource(R.string.save),
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
                                    text = state.settings.displayName.ifBlank { stringResource(R.string.anonymous) },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.edit_name),
                                    fontSize = 12.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }

                            // Edit butonu
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(colorScheme.surfaceVariant)
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

    val colorScheme = MaterialTheme.colorScheme

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
            color = colorScheme.onSurfaceVariant,
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
                .background(colorScheme.surface)
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

    val colorScheme = MaterialTheme.colorScheme

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
                        if (isDark) colorScheme.surface
                        else Color(0xFFFFF9C4)
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
                    text = if (isDark) stringResource(R.string.dark_theme) else stringResource(R.string.light_theme),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = if (isDark) stringResource(R.string.dark_desc) else stringResource(R.string.light_desc),
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
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

    val colorScheme = MaterialTheme.colorScheme


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
                        else Brush.linearGradient(
                            listOf(colorScheme.surfaceVariant, colorScheme.surfaceVariant)  // 0xFFF5F5F5
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
                    color = colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
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

    val colorScheme = MaterialTheme.colorScheme

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
                    tint = if (isEnabled) colorScheme.onSurfaceVariant else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        ThemeToggleSwitch(
            isChecked = isEnabled,
            onToggle
        )
    }
}

@Composable
fun SettingsItemWithEmoji(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        Color(0xFF7B5EA7).copy(alpha = 0.15f),
        Color(0xFF4568DC).copy(alpha = 0.15f)
    )
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Emoji container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF7B5EA7),
                )
            }

            // Text content
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Arrow indicator
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}