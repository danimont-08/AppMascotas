package com.example.huellario.data.remote

import com.example.huellario.data.model.DailyRoutineResponse
import com.example.huellario.data.model.RoutineDto
import com.example.huellario.data.model.RoutineLogRequest
import com.example.huellario.data.model.RoutineRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para la gestión de rutinas y diario de cumplimiento.
 */
interface RoutineApiService {

    @GET("routines")
    suspend fun listRoutines(
        @Query("petId") petId: Int? = null
    ): Response<List<RoutineDto>>

    @GET("routines/daily")
    suspend fun getDailyRoutines(
        @Query("date") date: String,
        @Query("petId") petId: Int? = null
    ): Response<DailyRoutineResponse>

    @POST("routines")
    suspend fun createRoutine(
        @Body request: RoutineRequest
    ): Response<RoutineDto>

    @GET("routines/{id}")
    suspend fun getRoutineById(
        @Path("id") id: Int
    ): Response<RoutineDto>

    @PUT("routines/{id}")
    suspend fun updateRoutine(
        @Path("id") id: Int,
        @Body request: RoutineRequest
    ): Response<RoutineDto>

    @DELETE("routines/{id}")
    suspend fun deleteRoutine(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("routines/logs")
    suspend fun setLogStatus(
        @Body request: RoutineLogRequest
    ): Response<Any>
}
