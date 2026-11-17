package com.example.huellario.data.repository

import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.ErrorResponse
import com.example.huellario.data.model.UpdateUserRequest
import com.example.huellario.data.model.UpdateUserResponse
import com.example.huellario.data.remote.RetrofitClient
import com.example.huellario.data.remote.UserApiService
import com.google.gson.Gson
import retrofit2.Response

/**
 * Repositorio encargado de gestionar la información de perfil del usuario.
 */
class UserRepository(
    private val apiService: UserApiService = RetrofitClient.userApiService
) {
    suspend fun updateProfile(fullName: String, email: String): ApiResult<UpdateUserResponse> {
        return try {
            val response = apiService.updateProfile(UpdateUserRequest(fullName = fullName, email = email))
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
            ApiResult.Error("Error al actualizar perfil: ${e.localizedMessage ?: "Verifica la conexión con el servidor"}")
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
