package bob.colbaskin.umirhack7.di.token

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private const val TOKEN_DATA_STORE_NAME = "token_preferences"
private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    name = TOKEN_DATA_STORE_NAME
)
private const val TAG = "Auth"

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    private val dataStore: DataStore<Preferences> = context.tokenDataStore

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        Log.d(TAG, "Saving tokens. \nAccess: $accessToken, \nRefresh: $refreshToken")
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    private val accessToken: Flow<String?> = dataStore.data.map { it[ACCESS_TOKEN_KEY] }
    fun getAccessToken(): String? = runBlocking { accessToken.first() }

    private val refreshToken: Flow<String?> = dataStore.data.map { it[REFRESH_TOKEN_KEY] }
    fun getRefreshToken(): String? = runBlocking { refreshToken.first() }

    suspend fun cleatTokens() {
        Log.d(TAG, "Clearing tokens")
        dataStore.edit { it.clear() }
    }
}
