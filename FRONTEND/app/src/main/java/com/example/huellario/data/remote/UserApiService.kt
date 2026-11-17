package com.example.huellario.data.remote

import com.example.huellario.data.model.UpdateUserRequest
import com.example.huellario.data.model.UpdateUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

/**
 * Servicio Retrofit para las operaciones del perfil de usuario.
 */
interface UserApiService {

    @PUT("users/me")
    suspend fun updateProfile(
        @Body request: UpdateUserRequest
    ): Response<UpdateUserResponse>
}
