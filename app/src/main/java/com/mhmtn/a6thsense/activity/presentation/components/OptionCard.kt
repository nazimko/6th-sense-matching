package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.domain.Option

@Composable
fun OptionCard(
    option: Option,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val gradient = getOptionGradient(option)
    val shadowColor = getOptionShadowColor(option)
    val emoji = getOptionEmoji(option)

    Box(
        // 👇 MODIFIER SIRASI ÇOK ÖNEMLİ - fillMaxWidth/fillMaxHeight ÖNCE GELMELİ
        modifier = modifier // 👈 1. Parent'tan gelen (weight, fillMaxHeight)
            .shadow(
                elevation = if (isSelected) 16.dp else 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = shadowColor.copy(alpha = 0.4f),
                spotColor = shadowColor.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = Color.White.copy(alpha = if (isSelected) 0.6f else 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .scale(scale) // 👈 Scale EN SONA (shadow'dan sonra)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 44.sp
            )

            Text(
                text = option.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun getOptionGradient(option: Option): Brush {
    return when (option) {
        // Colors - Phase 2
        Option.RED -> Brush.verticalGradient(listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F)))
        Option.BLUE -> Brush.verticalGradient(listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)))
        Option.GREEN -> Brush.verticalGradient(listOf(Color(0xFF43E97B), Color(0xFF38F9D7)))
        Option.YELLOW -> Brush.verticalGradient(listOf(Color(0xFFFAD961), Color(0xFFF76B1C)))
        Option.PURPLE -> Brush.verticalGradient(listOf(Color(0xFFB06AB3), Color(0xFF4568DC)))
        Option.ORANGE -> Brush.verticalGradient(listOf(Color(0xFFFF9A56), Color(0xFFFF6347)))
        Option.PINK -> Brush.verticalGradient(listOf(Color(0xFFFFB6C1), Color(0xFFFF69B4)))
        Option.CYAN -> Brush.verticalGradient(listOf(Color(0xFF00D4FF), Color(0xFF00BFFF)))
        Option.MAGENTA -> Brush.verticalGradient(listOf(Color(0xFFFF00FF), Color(0xFFDA70D6)))
        Option.LIME -> Brush.verticalGradient(listOf(Color(0xFF32CD32), Color(0xFF7FFF00)))
        Option.TEAL -> Brush.verticalGradient(listOf(Color(0xFF008080), Color(0xFF20B2AA)))
        Option.CORAL -> Brush.verticalGradient(listOf(Color(0xFFFF7F50), Color(0xFFFF6347)))
        Option.INDIGO -> Brush.verticalGradient(listOf(Color(0xFF4B0082), Color(0xFF6A5ACD)))
        Option.MINT -> Brush.verticalGradient(listOf(Color(0xFF98FF98), Color(0xFF00FA9A)))
        Option.LAVENDER -> Brush.verticalGradient(listOf(Color(0xFFE6E6FA), Color(0xFFDDA0DD)))
        Option.GOLD -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))

        // Animals - Phase 3
        Option.LION -> Brush.verticalGradient(listOf(Color(0xFFFFD89B), Color(0xFFFF9A56)))
        Option.EAGLE -> Brush.verticalGradient(listOf(Color(0xFF8E9EAB), Color(0xFFEEF2F3)))
        Option.DOLPHIN -> Brush.verticalGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))
        Option.WOLF -> Brush.verticalGradient(listOf(Color(0xFF485563), Color(0xFF29323C)))
        Option.TIGER -> Brush.verticalGradient(listOf(Color(0xFFFF7700), Color(0xFFFF5500)))
        Option.OWL -> Brush.verticalGradient(listOf(Color(0xFF8B7355), Color(0xFF654321)))
        Option.BUTTERFLY -> Brush.verticalGradient(listOf(Color(0xFFFFB6D9), Color(0xFFFF85C8)))
        Option.BEAR -> Brush.verticalGradient(listOf(Color(0xFF6B4423), Color(0xFF4A2C17)))
        Option.HORSE -> Brush.verticalGradient(listOf(Color(0xFFA0826D), Color(0xFF7D5A3E)))
        Option.SNAKE -> Brush.verticalGradient(listOf(Color(0xFF228B22), Color(0xFF006400)))
        Option.PHOENIX -> Brush.verticalGradient(listOf(Color(0xFFFF4500), Color(0xFFDC143C)))
        Option.FOX -> Brush.verticalGradient(listOf(Color(0xFFFF8C00), Color(0xFFFF6347)))
        Option.DRAGON -> Brush.verticalGradient(listOf(Color(0xFF8B0000), Color(0xFF4B0000)))
        Option.WHALE -> Brush.verticalGradient(listOf(Color(0xFF4682B4), Color(0xFF1E3A5F)))
        Option.HAWK -> Brush.verticalGradient(listOf(Color(0xFF696969), Color(0xFF2F4F4F)))
        Option.PEACOCK -> Brush.verticalGradient(listOf(Color(0xFF00CED1), Color(0xFF20B2AA)))

        // Elements - Phase 4
        Option.FIRE -> Brush.verticalGradient(listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F)))
        Option.WATER -> Brush.verticalGradient(listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)))
        Option.EARTH -> Brush.verticalGradient(listOf(Color(0xFF8B6914), Color(0xFF6B5414)))
        Option.AIR -> Brush.verticalGradient(listOf(Color(0xFFB8E0FF), Color(0xFF88C9FF)))
        Option.LIGHTNING -> Brush.verticalGradient(listOf(Color(0xFFFFFF00), Color(0xFFFFA500)))
        Option.ICE -> Brush.verticalGradient(listOf(Color(0xFFAFEEEE), Color(0xFF87CEEB)))
        Option.FOREST -> Brush.verticalGradient(listOf(Color(0xFF228B22), Color(0xFF006400)))
        Option.DESERT -> Brush.verticalGradient(listOf(Color(0xFFDEB887), Color(0xFFD2691E)))
        Option.MOUNTAIN -> Brush.verticalGradient(listOf(Color(0xFF708090), Color(0xFF2F4F4F)))
        Option.OCEAN -> Brush.verticalGradient(listOf(Color(0xFF006994), Color(0xFF003D5C)))
        Option.VOLCANO -> Brush.verticalGradient(listOf(Color(0xFFFF4500), Color(0xFF8B0000)))
        Option.WIND -> Brush.verticalGradient(listOf(Color(0xFFE0FFFF), Color(0xFFAFEEEE)))
        Option.RAIN -> Brush.verticalGradient(listOf(Color(0xFF4682B4), Color(0xFF1E90FF)))
        Option.SNOW -> Brush.verticalGradient(listOf(Color(0xFFFFFAFA), Color(0xFFF0F8FF)))
        Option.SUN -> Brush.verticalGradient(listOf(Color(0xFFFDB813), Color(0xFFFF8C00)))
        Option.MOON -> Brush.verticalGradient(listOf(Color(0xFFF0E68C), Color(0xFFE6E6FA)))

        // Dimensions - Phase 5
        Option.LIGHT -> Brush.verticalGradient(listOf(Color(0xFFFFF9B0), Color(0xFFFFE66D)))
        Option.DARK -> Brush.verticalGradient(listOf(Color(0xFF2C2C54), Color(0xFF1A1A2E)))
        Option.TIME -> Brush.verticalGradient(listOf(Color(0xFFB06AB3), Color(0xFF4568DC)))
        Option.SPACE -> Brush.verticalGradient(listOf(Color(0xFF1A2980), Color(0xFF26D0CE)))
        Option.ENERGY -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF6347)))
        Option.GRAVITY -> Brush.verticalGradient(listOf(Color(0xFF4B0082), Color(0xFF2F4F4F)))
        Option.INFINITY -> Brush.verticalGradient(listOf(Color(0xFF8A2BE2), Color(0xFF9370DB)))
        Option.VOID -> Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF1C1C1C)))
        Option.COSMOS -> Brush.verticalGradient(listOf(Color(0xFF191970), Color(0xFF000080)))
        Option.QUANTUM -> Brush.verticalGradient(listOf(Color(0xFF00FFFF), Color(0xFF0080FF)))
        Option.DIMENSION -> Brush.verticalGradient(listOf(Color(0xFF8B008B), Color(0xFF9932CC)))
        Option.PARALLEL -> Brush.verticalGradient(listOf(Color(0xFF4169E1), Color(0xFF6495ED)))
        Option.PAST -> Brush.verticalGradient(listOf(Color(0xFF8B7355), Color(0xFF654321)))
        Option.FUTURE -> Brush.verticalGradient(listOf(Color(0xFF00CED1), Color(0xFF48D1CC)))
        Option.PRESENT -> Brush.verticalGradient(listOf(Color(0xFF32CD32), Color(0xFF00FA9A)))
        Option.ETERNITY -> Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))

        else -> Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray))
    }
}


private fun getOptionShadowColor(option: Option): Color {
    return when (option) {
        // Phase 2 - Colors
        Option.RED -> Color(0xFFFF6B6B)
        Option.BLUE -> Color(0xFF4FACFE)
        Option.GREEN -> Color(0xFF43E97B)
        Option.YELLOW -> Color(0xFFFAD961)
        Option.PURPLE -> Color(0xFFB06AB3)
        Option.ORANGE -> Color(0xFFFF9A56)
        Option.PINK -> Color(0xFFFFB6C1)
        Option.CYAN -> Color(0xFF00D4FF)
        Option.MAGENTA -> Color(0xFFFF00FF)
        Option.LIME -> Color(0xFF32CD32)
        Option.TEAL -> Color(0xFF008080)
        Option.CORAL -> Color(0xFFFF7F50)
        Option.INDIGO -> Color(0xFF4B0082)
        Option.MINT -> Color(0xFF98FF98)
        Option.LAVENDER -> Color(0xFFE6E6FA)
        Option.GOLD -> Color(0xFFFFD700)

        // Phase 3 - Animals
        Option.LION -> Color(0xFFFFD89B)
        Option.EAGLE -> Color(0xFF8E9EAB)
        Option.DOLPHIN -> Color(0xFF667EEA)
        Option.WOLF -> Color(0xFF485563)
        Option.TIGER -> Color(0xFFFF7700)
        Option.OWL -> Color(0xFF8B7355)
        Option.BUTTERFLY -> Color(0xFFFFB6D9)
        Option.BEAR -> Color(0xFF6B4423)
        Option.HORSE -> Color(0xFFA0826D)
        Option.SNAKE -> Color(0xFF228B22)
        Option.PHOENIX -> Color(0xFFFF4500)
        Option.FOX -> Color(0xFFFF8C00)
        Option.DRAGON -> Color(0xFF8B0000)
        Option.WHALE -> Color(0xFF4682B4)
        Option.HAWK -> Color(0xFF696969)
        Option.PEACOCK -> Color(0xFF00CED1)

        // Phase 4 - Elements
        Option.FIRE -> Color(0xFFFF6B6B)
        Option.WATER -> Color(0xFF4FACFE)
        Option.EARTH -> Color(0xFF8B6914)
        Option.AIR -> Color(0xFFB8E0FF)
        Option.LIGHTNING -> Color(0xFFFFFF00)
        Option.ICE -> Color(0xFFAFEEEE)
        Option.FOREST -> Color(0xFF228B22)
        Option.DESERT -> Color(0xFFDEB887)
        Option.MOUNTAIN -> Color(0xFF708090)
        Option.OCEAN -> Color(0xFF006994)
        Option.VOLCANO -> Color(0xFFFF4500)
        Option.WIND -> Color(0xFFE0FFFF)
        Option.RAIN -> Color(0xFF4682B4)
        Option.SNOW -> Color(0xFFFFFAFA)
        Option.SUN -> Color(0xFFFDB813)
        Option.MOON -> Color(0xFFF0E68C)

        // Phase 5 - Dimensions
        Option.LIGHT -> Color(0xFFFFE66D)
        Option.DARK -> Color(0xFF2C2C54)
        Option.TIME -> Color(0xFFB06AB3)
        Option.SPACE -> Color(0xFF26D0CE)
        Option.ENERGY -> Color(0xFFFFD700)
        Option.GRAVITY -> Color(0xFF4B0082)
        Option.INFINITY -> Color(0xFF8A2BE2)
        Option.VOID -> Color(0xFF1C1C1C)
        Option.COSMOS -> Color(0xFF191970)
        Option.QUANTUM -> Color(0xFF00FFFF)
        Option.DIMENSION -> Color(0xFF8B008B)
        Option.PARALLEL -> Color(0xFF4169E1)
        Option.PAST -> Color(0xFF8B7355)
        Option.FUTURE -> Color(0xFF00CED1)
        Option.PRESENT -> Color(0xFF32CD32)
        Option.ETERNITY -> Color(0xFFFFD700)

        // Default
        else -> Color.Gray
    }
}

private fun getOptionEmoji(option: Option): String {
    return when (option) {
        // Binary
        Option.A, Option.B -> "⭕"

        // Colors
        Option.RED -> "🔴"
        Option.BLUE -> "🔵"
        Option.GREEN -> "🟢"
        Option.YELLOW -> "🟡"
        Option.PURPLE -> "🟣"
        Option.ORANGE -> "🟠"
        Option.PINK -> "🩷"
        Option.CYAN -> "🩵"
        Option.MAGENTA -> "🔴"
        Option.LIME -> "🟢"
        Option.TEAL -> "🩵"
        Option.CORAL -> "🧡"
        Option.INDIGO -> "🟣"
        Option.MINT -> "💚"
        Option.LAVENDER -> "💜"
        Option.GOLD -> "🟡"

        // Animals
        Option.LION -> "🦁"
        Option.EAGLE -> "🦅"
        Option.DOLPHIN -> "🐬"
        Option.WOLF -> "🐺"
        Option.TIGER -> "🐯"
        Option.OWL -> "🦉"
        Option.BUTTERFLY -> "🦋"
        Option.BEAR -> "🐻"
        Option.HORSE -> "🐴"
        Option.SNAKE -> "🐍"
        Option.PHOENIX -> "🔥"
        Option.FOX -> "🦊"
        Option.DRAGON -> "🐉"
        Option.WHALE -> "🐋"
        Option.HAWK -> "🦅"
        Option.PEACOCK -> "🦚"

        // Elements & Nature
        Option.FIRE -> "🔥"
        Option.WATER -> "💧"
        Option.EARTH -> "🌍"
        Option.AIR -> "💨"
        Option.LIGHTNING -> "⚡"
        Option.ICE -> "❄️"
        Option.FOREST -> "🌲"
        Option.DESERT -> "🏜️"
        Option.MOUNTAIN -> "⛰️"
        Option.OCEAN -> "🌊"
        Option.VOLCANO -> "🌋"
        Option.WIND -> "🌪️"
        Option.RAIN -> "🌧️"
        Option.SNOW -> "☃️"
        Option.SUN -> "☀️"
        Option.MOON -> "🌙"

        // Dimensions & Abstract
        Option.LIGHT -> "✨"
        Option.DARK -> "🌑"
        Option.TIME -> "⏰"
        Option.SPACE -> "🌌"
        Option.ENERGY -> "⚡"
        Option.GRAVITY -> "🪐"
        Option.INFINITY -> "♾️"
        Option.VOID -> "⚫"
        Option.COSMOS -> "🌠"
        Option.QUANTUM -> "⚛️"
        Option.DIMENSION -> "🔲"
        Option.PARALLEL -> "↔️"
        Option.PAST -> "⏪"
        Option.FUTURE -> "⏩"
        Option.PRESENT -> "⏺️"
        Option.ETERNITY -> "∞"
    }
}