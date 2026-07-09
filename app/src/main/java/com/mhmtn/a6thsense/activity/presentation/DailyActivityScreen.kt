package com.mhmtn.a6thsense.activity.presentation

import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.presentation.components.PhaseTransitionView
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType
import com.mhmtn.a6thsense.activity.presentation.components.PreferencePhaseTransitionView
import com.mhmtn.a6thsense.activity.presentation.components.RenderIntuitionMode
import com.mhmtn.a6thsense.activity.presentation.components.RenderPreferenceMode
import com.mhmtn.a6thsense.core.domain.ActivityConfig.MAX_STEP
import com.mhmtn.a6thsense.core.presentation.AmbientParticles
import com.mhmtn.a6thsense.ui.theme.MeditationDeepPurple
import com.mhmtn.a6thsense.ui.theme.MeditationLavender

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DailyActivityScreen(
    modifier: Modifier = Modifier,
    state: DailyActivityContract.State,
    onAction: (DailyActivityContract.Action) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(state.type) {
        Log.d("DailyActivityScreen", "Rendering with sessionType: ${state.type}")
    }


    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(2)
            .build()
    }

    val dropSoundId = remember {
        soundPool.load(context, R.raw.drop, 1)
    }

    fun playDropSound() {
        soundPool.play(dropSoundId, 1f, 1f, 1, 0, 1f)
    }

    DisposableEffect(Unit) {
        val mediaPlayer = MediaPlayer.create(
            context,
            R.raw.inner_tidal_pool
        ).apply {
            isLooping = true
            setVolume(0.15f, 0.15f)
            start()
        }

        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
            soundPool.release()
        }
    }

    // 👇 Loading state
    if (state.isLoadingQuestions) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F0C29),
                            Color(0xFF1A1A2E),
                            Color(0xFF24243E)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = Color.White)
                Text(
                    text = stringResource(R.string.questions_loading),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    val questionSet = state.questionSet
    val phaseKey = when (state.phase) {
        DailyActivityContract.Phase.PHASE_1 -> "phase1"
        DailyActivityContract.Phase.PHASE_2 -> "phase2"
        DailyActivityContract.Phase.PHASE_3 -> "phase3"
        DailyActivityContract.Phase.PHASE_4 -> "phase4"
        DailyActivityContract.Phase.PHASE_5 -> "phase5"
        DailyActivityContract.Phase.PHASE_6 -> "phase6"
    }

    // 👇 SessionType'a göre farklı background gradient
    val backgroundGradient = when {
        state.type == DailyActivityContract.SessionType.INTUITION -> {
            when (state.phase) {
                DailyActivityContract.Phase.PHASE_1 -> Brush.verticalGradient(
                    colors = listOf(
                        MeditationDeepPurple,
                        MeditationLavender,
                        MeditationDeepPurple.copy(alpha = 0.9f)
                    )
                )

                DailyActivityContract.Phase.PHASE_2 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF9A9E),
                        Color(0xFFFAD0C4),
                        Color(0xFFFBC2EB)
                    )
                )

                DailyActivityContract.Phase.PHASE_3 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3192),
                        Color(0xFF1BFFFF),
                        Color(0xFF2E3192)
                    )
                )

                DailyActivityContract.Phase.PHASE_4 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )

                DailyActivityContract.Phase.PHASE_5 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF0F0C29),
                        Color(0xFF302B63),
                        Color(0xFF24243E)
                    )
                )

                DailyActivityContract.Phase.PHASE_6 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3192),
                        Color(0xFF1BFFFF),
                        Color(0xFF2E3192)
                    )
                )
            }
        }
        state.type == DailyActivityContract.SessionType.PREFERENCE -> {
            // Preference mode: Pembe/turuncu tonları
            when (state.phase) {
                DailyActivityContract.Phase.PHASE_1 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFf093fb),
                        Color(0xFFf5576c)
                    )
                )
                DailyActivityContract.Phase.PHASE_2 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFfa709a),
                        Color(0xFFfee140)
                    )
                )
                DailyActivityContract.Phase.PHASE_3 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00b894),
                        Color(0xFF00cec9)
                    )
                )
                DailyActivityContract.Phase.PHASE_4 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00b894),
                        Color(0xFF00cec9)
                    )
                )
                DailyActivityContract.Phase.PHASE_5 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00b894),
                        Color(0xFF00cec9)
                    )
                )
                DailyActivityContract.Phase.PHASE_6 -> Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00b894),
                        Color(0xFF00cec9)
                    )
                )
            }
        }

        else -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F0C29),
                Color(0xFF1A1A2E),
                Color(0xFF24243E)
            ))

    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Ambient particles effect
        AmbientParticles()

        // Phase Transition Overlay
        if (state.isPhaseTransition) {

            when (state.type) {
                SessionType.INTUITION -> {
                    PhaseTransitionView(
                        phase = state.phase,
                        onTransitionComplete = {
                            onAction(DailyActivityContract.Action.PhaseTransitionShown)
                        }
                    )
                }


                SessionType.PREFERENCE -> {
                    PreferencePhaseTransitionView(
                        phase = state.phase,
                        phaseInfo = state.questionSet?.phases?.get(phaseKey),
                        onTransitionEnd = {
                            onAction(DailyActivityContract.Action.PhaseTransitionShown)
                        }
                    )
                }
            }
        } else {
            // Ana içerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header

                // ✅ DÜZELTME: PHASE_6 (Intuition) için soru sayısını FreeTextQuestionProvider'dan al
                val totalQuestions = if (state.type == SessionType.INTUITION && state.phase == DailyActivityContract.Phase.PHASE_6) {
                    FreeTextQuestionProvider.getAllQuestions().size
                } else {
                    questionSet?.phases?.get(phaseKey)?.questions?.size ?: MAX_STEP
                }
                
                val phaseInfo = questionSet?.phases?.get(phaseKey)

                PhaseHeader(
                    phase = state.phase,
                    step = state.step,
                    maxStep = totalQuestions,
                    sessionType = state.type,
                    phaseTitle = phaseInfo?.title?.asString(),
                    phaseDescription = phaseInfo?.description?.asString()
                )

                // Content area - Center vertically
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center // 👈 Changed from TopCenter to Center
                ) {
                    AnimatedContent(
                        targetState = state.step,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(600, easing = EaseInOutCubic)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 8 },
                                        animationSpec = tween(600, easing = EaseInOutCubic)
                                    )).togetherWith(
                                fadeOut(animationSpec = tween(400))
                            )
                        },
                        label = "StepTransition"
                    ) { step ->
                        when (state.type) {
                            SessionType.INTUITION -> {
                                // 👇 Intuition Mode: Hardcoded seçimler
                                RenderIntuitionMode(
                                    state = state,
                                    step = step,
                                    onAction = onAction,
                                    modifier = Modifier,
                                    playSound = { playDropSound() }
                                )
                            }
                            SessionType.PREFERENCE -> {
                                // 👇 Preference Mode: Firebase dynamic questions
                                RenderPreferenceMode(
                                    state = state,
                                    step = step,
                                    onAction = onAction,
                                    playSound = { playDropSound() }
                                )
                            }
                        }
                    }
                }

                // Progress Dots
                ProgressDots(
                    current = state.step + 1,
                    total = totalQuestions
                )
            }
        }
    }
}

@Composable
private fun PhaseHeader(
    phase: DailyActivityContract.Phase,
    step: Int,
    sessionType: SessionType,
    maxStep: Int,
    phaseTitle: String? = null, // 👈 Dynamic title
    phaseDescription: String? = null // 👈 Dynamic description
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when (sessionType) {
                        SessionType.INTUITION -> Color(0xFF667eea).copy(alpha = 0.3f)
                        SessionType.PREFERENCE -> Color(0xFFf093fb).copy(alpha = 0.3f)
                    }
                )
                .border(
                    width = 1.dp,
                    color = when (sessionType) {
                        SessionType.INTUITION -> Color(0xFF667eea)
                        SessionType.PREFERENCE -> Color(0xFFf093fb)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = when (sessionType) {
                    SessionType.INTUITION -> "🌙 Soul Sync"
                    SessionType.PREFERENCE -> "✨ Vibe Check"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 👇 Firestore'dan gelen title varsa kullan, yoksa hardcoded
        val (phaseText, phaseEmoji) = if (phaseTitle != null) {
            phaseTitle to getPhaseEmoji(phase)
        } else {
            when (phase) {
                DailyActivityContract.Phase.PHASE_1 -> stringResource(R.string.intuition) to "🌙"
                DailyActivityContract.Phase.PHASE_2 -> stringResource(R.string.colors) to "🎨"
                DailyActivityContract.Phase.PHASE_3 -> stringResource(R.string.spirit_animals) to "🦁"
                DailyActivityContract.Phase.PHASE_4 -> stringResource(R.string.elements) to "🌊"
                DailyActivityContract.Phase.PHASE_5 -> stringResource(R.string.dimensions) to "✨"
                DailyActivityContract.Phase.PHASE_6 -> stringResource(R.string.free_spirit) to "💭"
            }
        }

        Text(
            text = "$phaseEmoji $phaseText $phaseEmoji",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        // 👇 Phase description (opsiyonel)
        phaseDescription?.let { desc ->
            Text(
                text = desc,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Text(
            text = "${stringResource(R.string.qustion)} ${step + 1} / $maxStep",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

// 👇 Helper function
private fun getPhaseEmoji(phase: DailyActivityContract.Phase): String {
    return when (phase) {
        DailyActivityContract.Phase.PHASE_1 -> "🌙"
        DailyActivityContract.Phase.PHASE_2 -> "🎨"
        DailyActivityContract.Phase.PHASE_3 -> "🦁"
        DailyActivityContract.Phase.PHASE_4 -> "🌊"
        DailyActivityContract.Phase.PHASE_5 -> "✨"
        DailyActivityContract.Phase.PHASE_6 -> "💭"
    }
}

@Composable
private fun ProgressDots(
    current: Int,
    total: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        repeat(total) { index ->
            val isActive = index < current
            val scale by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.7f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "dot_scale"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        if (isActive) Color.White
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}
