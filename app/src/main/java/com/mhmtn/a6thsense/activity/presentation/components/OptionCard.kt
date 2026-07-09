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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
    val contentColor = getOptionContentColor(option)

    Box(
        modifier = modifier
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
                color = if (contentColor == Color.White) {
                    Color.White.copy(alpha = if (isSelected) 0.6f else 0.3f)
                } else {
                    contentColor.copy(alpha = if (isSelected) 0.5f else 0.2f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .scale(scale)
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
                text = stringResource(option.displayNameRes),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    shadow = if (contentColor == Color.White) Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        blurRadius = 4f
                    ) else null
                )
            )
        }
    }
}

private fun getOptionContentColor(option: Option): Color {
    return when (option) {
        // Açık renkli gradyanlara sahip olanlar için koyu metin rengi
        Option.IVORY, Option.SILVER, Option.BEIGE, Option.EAGLE,
        Option.SWAN, Option.SNOW, Option.CLOUD, Option.TRUTH,
        Option.SPIRIT, Option.IVORY -> Color(0xFF2D1B69) // Tema koyu moru
        else -> Color.White
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
        Option.SILVER -> Brush.verticalGradient(listOf(Color(0xFFC0C0C0), Color(0xFFA9A9A9)))
        Option.BRONZE -> Brush.verticalGradient(listOf(Color(0xFFCD7F32), Color(0xFF8B4513)))
        Option.EMERALD -> Brush.verticalGradient(listOf(Color(0xFF50C878), Color(0xFF006400)))
        Option.RUBY -> Brush.verticalGradient(listOf(Color(0xFFE0115F), Color(0xFF8B0000)))
        Option.SAPPHIRE -> Brush.verticalGradient(listOf(Color(0xFF0F52BA), Color(0xFF000080)))
        Option.AMBER -> Brush.verticalGradient(listOf(Color(0xFFFFBF00), Color(0xFFD2691E)))
        Option.TURQUOISE -> Brush.verticalGradient(listOf(Color(0xFF40E0D0), Color(0xFF00CED1)))
        Option.VIOLET -> Brush.verticalGradient(listOf(Color(0xFF8F00FF), Color(0xFF4B0082)))
        Option.BEIGE -> Brush.verticalGradient(listOf(Color(0xFFF5F5DC), Color(0xFFD2B48C)))
        Option.MAROON -> Brush.verticalGradient(listOf(Color(0xFF800000), Color(0xFF4B0000)))
        Option.OLIVE -> Brush.verticalGradient(listOf(Color(0xFF808000), Color(0xFF556B2F)))
        Option.NAVY -> Brush.verticalGradient(listOf(Color(0xFF000080), Color(0xFF000033)))
        Option.SLATE -> Brush.verticalGradient(listOf(Color(0xFF708090), Color(0xFF2F4F4F)))
        Option.CRIMSON -> Brush.verticalGradient(listOf(Color(0xFFDC143C), Color(0xFF800000)))
        Option.CHARCOAL -> Brush.verticalGradient(listOf(Color(0xFF36454F), Color(0xFF121212)))
        Option.IVORY -> Brush.verticalGradient(listOf(Color(0xFFFFFFF0), Color(0xFFEEE8AA)))

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
        Option.CAT -> Brush.verticalGradient(listOf(Color(0xFFFFA07A), Color(0xFFCD5C5C)))
        Option.DOG -> Brush.verticalGradient(listOf(Color(0xFFDEB887), Color(0xFF8B4513)))
        Option.RABBIT -> Brush.verticalGradient(listOf(Color(0xFFF0F8FF), Color(0xFFB0C4DE)))
        Option.DEER -> Brush.verticalGradient(listOf(Color(0xFFBC8F8F), Color(0xFF5D4037)))
        Option.SHARK -> Brush.verticalGradient(listOf(Color(0xFF708090), Color(0xFF2F4F4F)))
        Option.OCTOPUS -> Brush.verticalGradient(listOf(Color(0xFF9370DB), Color(0xFF4B0082)))
        Option.RAY -> Brush.verticalGradient(listOf(Color(0xFFADD8E6), Color(0xFF4682B4)))
        Option.TURTLE -> Brush.verticalGradient(listOf(Color(0xFF556B2F), Color(0xFF006400)))
        Option.BAT -> Brush.verticalGradient(listOf(Color(0xFF2F4F4F), Color(0xFF000000)))
        Option.SCORPION -> Brush.verticalGradient(listOf(Color(0xFFB22222), Color(0xFF330000)))
        Option.SPIDER -> Brush.verticalGradient(listOf(Color(0xFF424242), Color(0xFF000000)))
        Option.RAVEN -> Brush.verticalGradient(listOf(Color(0xFF212121), Color(0xFF000000)))
        Option.SWAN -> Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE0E0E0)))
        Option.HUMMINGBIRD -> Brush.verticalGradient(listOf(Color(0xFF00FF7F), Color(0xFF008080)))
        Option.KOALA -> Brush.verticalGradient(listOf(Color(0xFFBDBDBD), Color(0xFF616161)))
        Option.PANDA -> Brush.verticalGradient(listOf(Color(0xFFF5F5F5), Color(0xFF212121)))

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
        Option.STORM -> Brush.verticalGradient(listOf(Color(0xFF485563), Color(0xFF29323C)))
        Option.THUNDER -> Brush.verticalGradient(listOf(Color(0xFFF7971E), Color(0xFFFFD200)))
        Option.CLOUD -> Brush.verticalGradient(listOf(Color(0xFFECE9E6), Color(0xFFFFFFFF)))
        Option.FOG -> Brush.verticalGradient(listOf(Color(0xFFBDC3C7), Color(0xFF2C3E50)))
        Option.RIVER -> Brush.verticalGradient(listOf(Color(0xFF2193B0), Color(0xFF6DD5ED)))
        Option.LAKE -> Brush.verticalGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))
        Option.CAVE -> Brush.verticalGradient(listOf(Color(0xFF434343), Color(0xFF000000)))
        Option.CANYON -> Brush.verticalGradient(listOf(Color(0xFFD38312), Color(0xFFA83279)))
        Option.STAR -> Brush.verticalGradient(listOf(Color(0xFFFFF9B0), Color(0xFFFFE66D)))
        Option.GALAXY -> Brush.verticalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))
        Option.NEBULA -> Brush.verticalGradient(listOf(Color(0xFFE94057), Color(0xFF8A2387)))
        Option.COMET -> Brush.verticalGradient(listOf(Color(0xFF7474BF), Color(0xFF348AC7)))
        Option.LEAF -> Brush.verticalGradient(listOf(Color(0xFF91E842), Color(0xFF40910E)))
        Option.FLOWER -> Brush.verticalGradient(listOf(Color(0xFFFF5F6D), Color(0xFFFFC371)))
        Option.ROOT -> Brush.verticalGradient(listOf(Color(0xFFDAE2F8), Color(0xFFD6A4A4)))
        Option.SEED -> Brush.verticalGradient(listOf(Color(0xFFFBD3E9), Color(0xFFBB377D)))

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
        Option.MIND -> Brush.verticalGradient(listOf(Color(0xFF00B4DB), Color(0xFF0083B0)))
        Option.SOUL -> Brush.verticalGradient(listOf(Color(0xFFFF00CC), Color(0xFF333399)))
        Option.SPIRIT -> Brush.verticalGradient(listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3)))
        Option.BODY -> Brush.verticalGradient(listOf(Color(0xFFF12711), Color(0xFFF5AF19)))
        Option.DREAM -> Brush.verticalGradient(listOf(Color(0xFFFC466B), Color(0xFF3F5EFB)))
        Option.REALITY -> Brush.verticalGradient(listOf(Color(0xFF2193B0), Color(0xFF6DD5ED)))
        Option.TRUTH -> Brush.verticalGradient(listOf(Color(0xFFECE9E6), Color(0xFFFFFFFF)))
        Option.ILLUSION -> Brush.verticalGradient(listOf(Color(0xFFEB3349), Color(0xFFF45C43)))
        Option.ORDER -> Brush.verticalGradient(listOf(Color(0xFF1D2B64), Color(0xFFF8CDDA)))
        Option.CHAOS -> Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF434343)))
        Option.HARMONY -> Brush.verticalGradient(listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)))
        Option.DISCORD -> Brush.verticalGradient(listOf(Color(0xFFCB3066), Color(0xFF161616)))
        Option.WISDOM -> Brush.verticalGradient(listOf(Color(0xFFF7971E), Color(0xFFFDDC33)))
        Option.KNOWLEDGE -> Brush.verticalGradient(listOf(Color(0xFF200122), Color(0xFF6F0000)))
        Option.INSTINCT -> Brush.verticalGradient(listOf(Color(0xFF3CA55C), Color(0xFFB5AC49)))
        Option.REASON -> Brush.verticalGradient(listOf(Color(0xFF4CA1AF), Color(0xFFC4E0E5)))

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
        Option.SILVER -> Color(0xFFC0C0C0)
        Option.BRONZE -> Color(0xFFCD7F32)
        Option.EMERALD -> Color(0xFF50C878)
        Option.RUBY -> Color(0xFFE0115F)
        Option.SAPPHIRE -> Color(0xFF0F52BA)
        Option.AMBER -> Color(0xFFFFBF00)
        Option.TURQUOISE -> Color(0xFF40E0D0)
        Option.VIOLET -> Color(0xFF8F00FF)
        Option.BEIGE -> Color(0xFFF5F5DC)
        Option.MAROON -> Color(0xFF800000)
        Option.OLIVE -> Color(0xFF808000)
        Option.NAVY -> Color(0xFF000080)
        Option.SLATE -> Color(0xFF708090)
        Option.CRIMSON -> Color(0xFFDC143C)
        Option.CHARCOAL -> Color(0xFF36454F)
        Option.IVORY -> Color(0xFFFFFFF0)

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
        Option.CAT -> Color(0xFFFFA07A)
        Option.DOG -> Color(0xFFDEB887)
        Option.RABBIT -> Color(0xFFF0F8FF)
        Option.DEER -> Color(0xFFBC8F8F)
        Option.SHARK -> Color(0xFF708090)
        Option.OCTOPUS -> Color(0xFF9370DB)
        Option.RAY -> Color(0xFFADD8E6)
        Option.TURTLE -> Color(0xFF556B2F)
        Option.BAT -> Color(0xFF2F4F4F)
        Option.SCORPION -> Color(0xFFB22222)
        Option.SPIDER -> Color(0xFF424242)
        Option.RAVEN -> Color(0xFF212121)
        Option.SWAN -> Color(0xFFFFFFFF)
        Option.HUMMINGBIRD -> Color(0xFF00FF7F)
        Option.KOALA -> Color(0xFFBDBDBD)
        Option.PANDA -> Color(0xFFF5F5F5)

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
        Option.STORM -> Color(0xFF485563)
        Option.THUNDER -> Color(0xFFF7971E)
        Option.CLOUD -> Color(0xFFECE9E6)
        Option.FOG -> Color(0xFFBDC3C7)
        Option.RIVER -> Color(0xFF2193B0)
        Option.LAKE -> Color(0xFF00C6FF)
        Option.CAVE -> Color(0xFF434343)
        Option.CANYON -> Color(0xFFD38312)
        Option.STAR -> Color(0xFFFFF9B0)
        Option.GALAXY -> Color(0xFF6A11CB)
        Option.NEBULA -> Color(0xFFE94057)
        Option.COMET -> Color(0xFF7474BF)
        Option.LEAF -> Color(0xFF91E842)
        Option.FLOWER -> Color(0xFFFF5F6D)
        Option.ROOT -> Color(0xFFDAE2F8)
        Option.SEED -> Color(0xFFFBD3E9)

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
        Option.MIND -> Color(0xFF00B4DB)
        Option.SOUL -> Color(0xFFFF00CC)
        Option.SPIRIT -> Color(0xFFE0EAFC)
        Option.BODY -> Color(0xFFF12711)
        Option.DREAM -> Color(0xFFFC466B)
        Option.REALITY -> Color(0xFF2193B0)
        Option.TRUTH -> Color(0xFFECE9E6)
        Option.ILLUSION -> Color(0xFFEB3349)
        Option.ORDER -> Color(0xFF1D2B64)
        Option.CHAOS -> Color(0xFF000000)
        Option.HARMONY -> Color(0xFF00F2FE)
        Option.DISCORD -> Color(0xFFCB3066)
        Option.WISDOM -> Color(0xFFF7971E)
        Option.KNOWLEDGE -> Color(0xFF200122)
        Option.INSTINCT -> Color(0xFF3CA55C)
        Option.REASON -> Color(0xFF4CA1AF)

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
        Option.CYAN -> "🫧"
        Option.MAGENTA -> "🔮"
        Option.LIME -> "🍏"
        Option.TEAL -> "🧿"
        Option.CORAL -> "🪸"
        Option.INDIGO -> "🫐"
        Option.MINT -> "🧊"
        Option.LAVENDER -> "🪻"
        Option.GOLD -> "📀"
        Option.SILVER -> "🔘"
        Option.BRONZE -> "🥉"
        Option.EMERALD -> "✳️"
        Option.RUBY -> "🏮"
        Option.SAPPHIRE -> "💎"
        Option.AMBER -> "🍯"
        Option.TURQUOISE -> "💠"
        Option.VIOLET -> "🌆"
        Option.BEIGE -> "📜"
        Option.MAROON -> "🧱"
        Option.OLIVE -> "🫒"
        Option.NAVY -> "⚓"
        Option.SLATE -> "🗿"
        Option.CRIMSON -> "🩸"
        Option.CHARCOAL -> "🖤"
        Option.IVORY -> "🦷"

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
        Option.CAT -> "🐱"
        Option.DOG -> "🐶"
        Option.RABBIT -> "🐰"
        Option.DEER -> "🦌"
        Option.SHARK -> "🦈"
        Option.OCTOPUS -> "🐙"
        Option.RAY -> "🐟"
        Option.TURTLE -> "🐢"
        Option.BAT -> "🦇"
        Option.SCORPION -> "🦂"
        Option.SPIDER -> "🕷️"
        Option.RAVEN -> "🐦‍⬛"
        Option.SWAN -> "🦢"
        Option.HUMMINGBIRD -> "🐦"
        Option.KOALA -> "🐨"
        Option.PANDA -> "🐼"

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
        Option.STORM -> "🌩️"
        Option.THUNDER -> "⚡"
        Option.CLOUD -> "☁️"
        Option.FOG -> "🌫️"
        Option.RIVER -> "🏞️"
        Option.LAKE -> "🛶"
        Option.CAVE -> "🕳️"
        Option.CANYON -> "🏜️"
        Option.STAR -> "⭐"
        Option.GALAXY -> "🌌"
        Option.NEBULA -> "🌀"
        Option.COMET -> "☄️"
        Option.LEAF -> "🍃"
        Option.FLOWER -> "🌸"
        Option.ROOT -> "🪵"
        Option.SEED -> "🌱"

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
        Option.MIND -> "🧠"
        Option.SOUL -> "☯️"
        Option.SPIRIT -> "👻"
        Option.BODY -> "🧘"
        Option.DREAM -> "🛌"
        Option.REALITY -> "🏢"
        Option.TRUTH -> "⚖️"
        Option.ILLUSION -> "🪄"
        Option.ORDER -> "📐"
        Option.CHAOS -> "💥"
        Option.HARMONY -> "🤝"
        Option.DISCORD -> "⚡"
        Option.WISDOM -> "📜"
        Option.KNOWLEDGE -> "📚"
        Option.INSTINCT -> "🐾"
        Option.REASON -> "💡"
    }
}
