package com.example.huellario.data.model

import com.google.gson.annotations.SerializedName

/**
 * Solicitud para actualizar el perfil del usuario (PUT /api/users/me).
 */
data class UpdateUserRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String
)

/**
 * Respuesta de actualización del perfil del usuario.
 */
data class UpdateUserResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("user")
    val user: UserDto? = null
)
