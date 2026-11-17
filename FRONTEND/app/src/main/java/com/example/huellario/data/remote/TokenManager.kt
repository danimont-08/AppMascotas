package com.example.huellario.data.remote

import android.content.Context
import android.content.SharedPreferences

/**
 * Administrador ligero para la persistencia del token de sesión JWT.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "huellario_session_prefs"
        private const val KEY_JWT_TOKEN = "jwt_token"

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenManager(context.applicationContext)
                INSTANCE = instance
                RetrofitClient.setTokenProvider { instance.getToken() }
                instance
            }
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
        RetrofitClient.setTokenProvider { token }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_JWT_TOKEN).apply()
        RetrofitClient.setTokenProvider { null }
    }

    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}
