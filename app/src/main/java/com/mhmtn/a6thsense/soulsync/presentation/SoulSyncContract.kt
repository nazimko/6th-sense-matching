package com.mhmtn.a6thsense.soulsync.presentation

import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.soulsync.domain.PlayerState

data class SoulSyncState(
// Source code removed.
)

enum class GameState {
    WAITING, COUNTDOWN, PLAYING, WAITING_FOR_OTHER, REVEALING, FINISHED, CANCELLED // 👈 CANCELLED eklendi
}

sealed class SoulSyncEffect {// Source code removed.}