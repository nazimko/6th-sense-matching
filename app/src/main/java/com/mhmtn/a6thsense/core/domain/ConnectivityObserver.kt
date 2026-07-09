package com.mhmtn.a6thsense.core.domain

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {// Source code removed.}
}