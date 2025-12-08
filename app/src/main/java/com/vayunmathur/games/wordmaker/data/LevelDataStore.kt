package com.vayunmathur.games.wordmaker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LevelDataStore(context: Context) {

    private val appContext = context.applicationContext
    private val LEVEL_KEY = intPreferencesKey("current_level")

    val currentLevel: Flow<Int> = appContext.dataStore.data
        .map { preferences ->
            preferences[LEVEL_KEY] ?: 1
        }

    suspend fun saveLevel(level: Int) {
        appContext.dataStore.edit { settings ->
            settings[LEVEL_KEY] = level
        }
    }
}