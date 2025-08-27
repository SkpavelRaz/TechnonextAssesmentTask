package com.pavel.tecchnonextkotlinassesment.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


// extension for DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class PrefsManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PASS = stringPreferencesKey("passCode")
    }

    suspend fun saveUser(email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASS] = password
        }
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMAIL]
    }

    val userPassword: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_PASS]
    }
}