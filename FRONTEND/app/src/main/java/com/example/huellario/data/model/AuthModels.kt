package com.example.huellario.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de solicitud para inicio de sesión.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

/**
 * Modelo de solicitud para registro de dueño/propietario.
 * Nota: La confirmación de contraseña se valida en el cliente y no se envía a la API.
 */
data class RegisterRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

/**
 * Representación del usuario/propietario retornado por el servidor.
 */
data class UserDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String
)

/**
 * Respuesta estándar enviada por el backend tras autenticar o registrar.
 */
data class AuthResponse(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("user")
    val user: UserDto? = null
)

/**
 * Estructura para procesar respuestas de error JSON de la API.
 */
data class ErrorResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val error: String? = null
)
