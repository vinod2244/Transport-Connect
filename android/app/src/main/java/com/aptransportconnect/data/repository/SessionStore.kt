package com.aptransportconnect.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "transport_connect")

class SessionStore(private val context: Context) {
    private val syncPrefs = context.getSharedPreferences("transport_connect_sync", Context.MODE_PRIVATE)
    private object Keys {
        val onboardingDone = booleanPreferencesKey("onboarding_done")
        val authToken = stringPreferencesKey("auth_token")
        val refreshToken = stringPreferencesKey("refresh_token")
        val mobile = stringPreferencesKey("mobile")
        val role = stringPreferencesKey("role")
        val rememberMe = booleanPreferencesKey("remember_me")
    }

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.onboardingDone] ?: false }
    val authToken: Flow<String> = context.dataStore.data.map { it[Keys.authToken] ?: "" }
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { it[Keys.rememberMe] ?: false }

    suspend fun setOnboardingDone(value: Boolean) {
        context.dataStore.edit { it[Keys.onboardingDone] = value }
    }

    suspend fun persistSession(token: String, refreshToken: String, mobile: String, role: String, remember: Boolean) {
        syncPrefs.edit().putString("auth_token", token).apply()
        context.dataStore.edit {
            it[Keys.authToken] = token
            it[Keys.refreshToken] = refreshToken
            it[Keys.mobile] = mobile
            it[Keys.role] = role
            it[Keys.rememberMe] = remember
        }
    }

    suspend fun clearSession() {
        syncPrefs.edit().remove("auth_token").apply()
        context.dataStore.edit {
            it.remove(Keys.authToken)
            it.remove(Keys.refreshToken)
            it.remove(Keys.mobile)
            it.remove(Keys.role)
            it[Keys.rememberMe] = false
        }

        fun authTokenSync(): String = syncPrefs.getString("auth_token", "").orEmpty()
    }
}
