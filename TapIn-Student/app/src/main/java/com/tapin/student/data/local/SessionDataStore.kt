package com.tapin.student.data.local

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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tapin_session")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val KEY_JWT        = stringPreferencesKey("jwt_token")
        private val KEY_NFC_TOKEN  = stringPreferencesKey("nfc_token")
        private val KEY_USER_ID    = stringPreferencesKey("user_id")
        private val KEY_USER_NAME  = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_STUDENT_ID = stringPreferencesKey("student_id")
    }

    // ── Reads ────────────────────────────────────────────────

    val jwtToken: Flow<String?> = context.dataStore.data
        .map { it[KEY_JWT] }

    val nfcToken: Flow<String?> = context.dataStore.data
        .map { it[KEY_NFC_TOKEN] }

    val userId: Flow<String?> = context.dataStore.data
        .map { it[KEY_USER_ID] }

    val userName: Flow<String?> = context.dataStore.data
        .map { it[KEY_USER_NAME] }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { it[KEY_USER_EMAIL] }

    val studentId: Flow<String?> = context.dataStore.data
        .map { it[KEY_STUDENT_ID] }

    // ── Writes ───────────────────────────────────────────────

    suspend fun saveSession(
        jwt: String,
        nfcToken: String?,
        userId: String,
        userName: String,
        userEmail: String,
        studentId: String?
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_JWT]        = jwt
            prefs[KEY_USER_ID]    = userId
            prefs[KEY_USER_NAME]  = userName
            prefs[KEY_USER_EMAIL] = userEmail
            nfcToken?.let  { prefs[KEY_NFC_TOKEN]  = it }
            studentId?.let { prefs[KEY_STUDENT_ID] = it }
        }
    }

    suspend fun saveNfcToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NFC_TOKEN] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
