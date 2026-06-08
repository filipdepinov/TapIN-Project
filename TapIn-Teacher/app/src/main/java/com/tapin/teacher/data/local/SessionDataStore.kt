package com.tapin.teacher.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tapin_teacher_session")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_JWT        = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID    = stringPreferencesKey("user_id")
        private val KEY_USER_NAME  = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    val jwtToken: Flow<String?> = context.dataStore.data.map { it[KEY_JWT] }
    val userId: Flow<String?>   = context.dataStore.data.map { it[KEY_USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[KEY_USER_NAME] }

    suspend fun saveSession(jwt: String, userId: String, userName: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_JWT]        = jwt
            prefs[KEY_USER_ID]    = userId
            prefs[KEY_USER_NAME]  = userName
            prefs[KEY_USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
