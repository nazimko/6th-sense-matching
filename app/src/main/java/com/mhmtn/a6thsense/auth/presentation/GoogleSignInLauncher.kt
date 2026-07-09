package com.mhmtn.a6thsense.auth.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
@Composable
fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) {// Source code removed.}