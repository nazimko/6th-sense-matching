package com.mhmtn.a6thsense.onboarding.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.mhmtn.a6thsense.onboarding.domain.OnboardingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.onboardingDataStore by preferencesDataStore("onboarding")

class OnboardingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : OnboardingRepository {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    override fun hasCompletedOnboarding(): Flow<Boolean> =
        context.onboardingDataStore.data.map { prefs ->
            prefs[ONBOARDING_COMPLETED] ?: false
        }

    override suspend fun completeOnboarding() {
        context.onboardingDataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = true
        }
    }
}