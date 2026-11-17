package com.example.huellario.data.remote

import com.example.huellario.data.model.HealthResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Servicio Retrofit para comprobar el estado de salud del backend y la base de datos.
 */
interface HealthApiService {

    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>
}
