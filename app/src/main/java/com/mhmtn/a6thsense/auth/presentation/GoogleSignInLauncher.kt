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
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.result
        val idToken = account.idToken
        if (idToken != null) {
            onIdTokenReceived(idToken)
        }
    }
}