package com.mhmtn.a6thsense.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DataStoreManager {
    private val Context.settingsDataStore by preferencesDataStore(name = "settings")

    fun getDataStore(context: Context): DataStore<Preferences> {
        return context.applicationContext.settingsDataStore
    }
}