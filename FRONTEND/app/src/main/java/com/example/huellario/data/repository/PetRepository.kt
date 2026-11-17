package com.example.huellario.data.repository

import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.ErrorResponse
import com.example.huellario.data.model.PetDto
import com.example.huellario.data.model.PetRequest
import com.example.huellario.data.remote.PetApiService
import com.example.huellario.data.remote.RetrofitClient
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

/**
 * Repositorio encargado de gestionar las operaciones CRUD de mascotas.
 */
class PetRepository(
    private val apiService: PetApiService = RetrofitClient.petApiService
) {
    suspend fun getPets(): ApiResult<List<PetDto>> {
        return try {
            val response = apiService.listPets()
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al obtener las mascotas: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun getPetById(id: Int): ApiResult<PetDto> {
        return try {
            val response = apiService.getPetById(id)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al consultar la mascota: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun createPet(request: PetRequest): ApiResult<PetDto> {
        return try {
            val response = apiService.createPet(request)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al crear la mascota: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun updatePet(id: Int, request: PetRequest): ApiResult<PetDto> {
        return try {
            val response = apiService.updatePet(id, request)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al actualizar la mascota: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun deletePet(id: Int): ApiResult<Unit> {
        return try {
            val response = apiService.deletePet(id)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al eliminar la mascota: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun uploadPetPhoto(id: Int, imageFile: File): ApiResult<PetDto> {
        return try {
            val requestFile = RequestBody.create(null, imageFile)
            val body = MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)
            val response = apiService.uploadPetPhoto(id, body)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al subir la imagen: ${e.localizedMessage ?: "Verifica la conexión"}")
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
