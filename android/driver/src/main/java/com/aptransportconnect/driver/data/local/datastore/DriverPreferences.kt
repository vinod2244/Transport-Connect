package com.aptransportconnect.driver.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.driverDataStore: DataStore<Preferences> by preferencesDataStore(name = "driver_preferences")

@Singleton
class DriverPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val accessToken = stringPreferencesKey("access_token")
        val refreshToken = stringPreferencesKey("refresh_token")
        val phone = stringPreferencesKey("phone")
        val darkTheme = booleanPreferencesKey("dark_theme")
        val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
        val onlineStatus = booleanPreferencesKey("online_status")
    }

    private val prefsFlow: Flow<Preferences> = context.driverDataStore.data.catch { ex ->
        if (ex is IOException) emit(emptyPreferences()) else throw ex
    }

    val accessToken: Flow<String?> = prefsFlow.map { it[Keys.accessToken] }
    val refreshToken: Flow<String?> = prefsFlow.map { it[Keys.refreshToken] }
    val phone: Flow<String?> = prefsFlow.map { it[Keys.phone] }
    val isLoggedIn: Flow<Boolean> = prefsFlow.map { !it[Keys.accessToken].isNullOrBlank() }
    val isDarkTheme: Flow<Boolean> = prefsFlow.map { it[Keys.darkTheme] ?: false }
    val notificationsEnabled: Flow<Boolean> = prefsFlow.map { it[Keys.notificationsEnabled] ?: true }
    val isOnline: Flow<Boolean> = prefsFlow.map { it[Keys.onlineStatus] ?: false }

    suspend fun savePhone(phone: String) {
        context.driverDataStore.edit { it[Keys.phone] = phone }
    }

    suspend fun saveAuthTokens(accessToken: String, refreshToken: String) {
        context.driverDataStore.edit {
            it[Keys.accessToken] = accessToken
            it[Keys.refreshToken] = refreshToken
        }
    }

    suspend fun setOnlineStatus(isOnline: Boolean) {
        context.driverDataStore.edit { it[Keys.onlineStatus] = isOnline }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.driverDataStore.edit { it[Keys.notificationsEnabled] = enabled }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.driverDataStore.edit { it[Keys.darkTheme] = enabled }
    }

    suspend fun clearAuth() {
        context.driverDataStore.edit {
            it.remove(Keys.accessToken)
            it.remove(Keys.refreshToken)
        }
    }
}
