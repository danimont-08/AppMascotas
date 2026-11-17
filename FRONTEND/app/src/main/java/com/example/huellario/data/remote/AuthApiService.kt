package com.example.huellario.data.remote

import com.example.huellario.data.model.AuthResponse
import com.example.huellario.data.model.LoginRequest
import com.example.huellario.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio Retrofit para autenticación del propietario.
 */
interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
}
