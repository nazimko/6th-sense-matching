package com.mhmtn.a6thsense.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import com.mhmtn.a6thsense.R
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

sealed class BottomNavItem(
    val route: String,
    val label: UiText,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val selectedIconXML: Int? = null,
    val unselectedIconXML: Int? = null
) {
    object Home : BottomNavItem(
        route = Routes.HOME,
        label = UiText.StringResource(R.string.home),
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Conversations : BottomNavItem(
        route = Routes.CONVERSATIONS,
        label = UiText.StringResource(R.string.messages_text),
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.MailOutline
    )

    object Friends : BottomNavItem(
        route = Routes.FRIENDS,
        label = UiText.StringResource(R.string.friends),
        selectedIconXML = R.drawable.group_24px_outlined,
        unselectedIconXML = R.drawable.group_24px
    )

    object Profile : BottomNavItem(
        route = Routes.PROFILE,
        label = UiText.StringResource(R.string.profile_text),
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    object Discover : BottomNavItem(
        route = Routes.DISCOVER,
        label = UiText.StringResource(R.string.discover_text),
        selectedIconXML = R.drawable.discover_filled,
        unselectedIconXML = R.drawable.outline_explore_24
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Discover,
    BottomNavItem.Friends,
    BottomNavItem.Conversations,
    BottomNavItem.Profile
)

@Composable
fun AppBottomNavigation(
    currentRoute: String?,
    onItemSelected: (String) -> Unit,
    unreadCount: Int = 0
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = navigationBarHeight)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route

                BottomNavItemView(
                    item = item,
                    isSelected = isSelected,
                    badge = if (item is BottomNavItem.Conversations) unreadCount else 0,
                    onClick = { onItemSelected(item.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                    )
                else
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                // ImageVector İkonları için (Home, Messages, Friends, Profile)
                val iconVector = if (isSelected) item.selectedIcon else item.unselectedIcon
                if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = item.label.asString(),
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // XML İkonları için (Discover vb.)
                val iconRes = if (isSelected) item.selectedIconXML else item.unselectedIconXML
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = item.label.asString(),
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Badge
                if (badge > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-4).dp)
                            .size(16.dp)
                            .background(Color(0xFFFF4757), CircleShape),
                        contentAlignment = Alignment.Center // İçeriği hem dikey hem yatay ortalar
                    ) {
                        Text(
                            text = if (badge > 9) "9+" else badge.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center, // Metni kendi alanı içinde ortalar
                            lineHeight = 9.sp // Dikey kaymaları önlemek için satır yüksekliğini sabitler
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = item.label.asString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.widthIn(min = 40.dp),
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavItemViewPreview() {
    MaterialTheme {
        Surface(color = Color(0xFF1A1A2E)) { // Koyu arka plan üzerinde daha net görülür
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Normal Durum
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BottomNavItemView(
                        item = BottomNavItem.Home,
                        isSelected = false,
                        onClick = {}
                    )
                    BottomNavItemView(
                        item = BottomNavItem.Home,
                        isSelected = true,
                        onClick = {}
                    )
                }

                // Unread Count Durumu (Küçük sayı)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BottomNavItemView(
                        item = BottomNavItem.Conversations,
                        isSelected = false,
                        badge = 5,
                        onClick = {}
                    )
                    BottomNavItemView(
                        item = BottomNavItem.Conversations,
                        isSelected = true,
                        badge = 5,
                        onClick = {}
                    )
                }

                // Unread Count Durumu (Büyük sayı - 9+)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BottomNavItemView(
                        item = BottomNavItem.Conversations,
                        isSelected = false,
                        badge = 15,
                        onClick = {}
                    )
                    BottomNavItemView(
                        item = BottomNavItem.Conversations,
                        isSelected = true,
                        badge = 15,
                        onClick = {}
                    )
                }
            }
        }
    }
}
