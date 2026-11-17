package com.example.huellario.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de respuesta para el endpoint GET /api/health.
 */
data class HealthResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("database")
    val database: String,
    @SerializedName("details")
    val details: String? = null
)
