package com.example.huellario.data.repository

import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.AuthResponse
import com.example.huellario.data.model.ErrorResponse
import com.example.huellario.data.model.HealthResponse
import com.example.huellario.data.model.LoginRequest
import com.example.huellario.data.model.RegisterRequest
import com.example.huellario.data.remote.AuthApiService
import com.example.huellario.data.remote.HealthApiService
import com.example.huellario.data.remote.RetrofitClient
import com.example.huellario.data.remote.TokenManager
import com.google.gson.Gson
import retrofit2.Response

/**
 * Repositorio encargado de gestionar las peticiones de autenticación y salud del backend.
 */
class AuthRepository(
    private val authApiService: AuthApiService = RetrofitClient.authApiService,
    private val healthApiService: HealthApiService = RetrofitClient.healthApiService,
    private val tokenManager: TokenManager? = null
) {
    suspend fun checkHealth(): ApiResult<HealthResponse> {
        return try {
            val response = healthApiService.checkHealth()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("Respuesta de salud vacía")
                }
            } else {
                val errorMsg = parseErrorMessage(response)
                ApiResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("No se pudo conectar con el servidor: ${e.localizedMessage ?: "Verifica la IP y la red Wi-Fi"}")
        }
    }

    suspend fun login(email: String, password: String): ApiResult<AuthResponse> {
        val result = safeApiCall {
            authApiService.login(LoginRequest(email = email, password = password))
        }
        if (result is ApiResult.Success && !result.data.token.isNullOrEmpty()) {
            val token = result.data.token
            tokenManager?.saveToken(token)
            RetrofitClient.setTokenProvider { token }
        }
        return result
    }

    suspend fun register(fullName: String, email: String, password: String): ApiResult<AuthResponse> {
        val result = safeApiCall {
            authApiService.register(
                RegisterRequest(
                    fullName = fullName,
                    email = email,
                    password = password
                )
            )
        }
        if (result is ApiResult.Success && !result.data.token.isNullOrEmpty()) {
            val token = result.data.token
            tokenManager?.saveToken(token)
            RetrofitClient.setTokenProvider { token }
        }
        return result
    }

    private suspend fun safeApiCall(call: suspend () -> Response<AuthResponse>): ApiResult<AuthResponse> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorMsg = parseErrorMessage(response)
                ApiResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error de conexión: ${e.localizedMessage ?: "No se pudo conectar con el servidor"}")
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string()
            if (!errorJson.isNullOrBlank()) {
                val errorObj = Gson().fromJson(errorJson, ErrorResponse::class.java)
                errorObj.message ?: errorObj.error ?: "Error ${response.code()}: ${response.message()}"
            } else {
                "Error ${response.code()}: ${response.message()}"
            }
        } catch (e: Exception) {
            "Error ${response.code()}: ${response.message()}"
        }
    }
}
