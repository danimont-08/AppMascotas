package com.example.huellario.data.repository

import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.DailyRoutineResponse
import com.example.huellario.data.model.ErrorResponse
import com.example.huellario.data.model.RoutineDto
import com.example.huellario.data.model.RoutineLogRequest
import com.example.huellario.data.model.RoutineRequest
import com.example.huellario.data.remote.RetrofitClient
import com.example.huellario.data.remote.RoutineApiService
import com.google.gson.Gson
import retrofit2.Response

/**
 * Repositorio encargado de orquestar la gestión de rutinas y diario de cumplimiento.
 */
class RoutineRepository(
    private val apiService: RoutineApiService = RetrofitClient.routineApiService
) {
    suspend fun listRoutines(petId: Int? = null): ApiResult<List<RoutineDto>> {
        return try {
            val response = apiService.listRoutines(petId)
            if (response.isSuccessful) {
                ApiResult.Success(response.body() ?: emptyList())
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al consultar rutinas: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun getDailyRoutines(date: String, petId: Int? = null): ApiResult<DailyRoutineResponse> {
        return try {
            val response = apiService.getDailyRoutines(date, petId)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al consultar las actividades del día: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun createRoutine(request: RoutineRequest): ApiResult<RoutineDto> {
        return try {
            val response = apiService.createRoutine(request)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al crear la rutina: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun getRoutineById(id: Int): ApiResult<RoutineDto> {
        return try {
            val response = apiService.getRoutineById(id)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al obtener la rutina: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun updateRoutine(id: Int, request: RoutineRequest): ApiResult<RoutineDto> {
        return try {
            val response = apiService.updateRoutine(id, request)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al actualizar la rutina: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun deleteRoutine(id: Int): ApiResult<Unit> {
        return try {
            val response = apiService.deleteRoutine(id)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al eliminar la rutina: ${e.localizedMessage ?: "Verifica la conexión"}")
        }
    }

    suspend fun setLogStatus(routineId: Int, logDate: String, status: String, notes: String? = null): ApiResult<Unit> {
        val backendStatus = when (status.uppercase()) {
            "COMPLETED" -> "COMPLETADA"
            "SKIPPED" -> "OMITIDA"
            "PENDING" -> "PENDIENTE"
            else -> status.uppercase()
        }
        return try {
            val response = apiService.setLogStatus(
                RoutineLogRequest(routineId = routineId, logDate = logDate, status = backendStatus, notes = notes)
            )
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(parseErrorMessage(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al actualizar el estado de cumplimiento: ${e.localizedMessage ?: "Verifica la conexión"}")
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
