package com.mhmtn.a6thsense.core.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.mhmtn.a6thsense.core.domain.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkConnectivityObserver(
    private val context: Context
) : ConnectivityObserver {// Source code removed.}

    private fun isNetworkAvailable(): Boolean {// Source code removed.}
