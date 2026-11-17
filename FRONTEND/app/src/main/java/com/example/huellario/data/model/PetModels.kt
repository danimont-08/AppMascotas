package com.example.huellario.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representación de una mascota retornada por la API (GET /api/pets).
 */
data class PetDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("breed")
    val breed: String? = null,
    @SerializedName("birth_date")
    val birthDate: String? = null,
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)

/**
 * Solicitud para crear o actualizar una mascota (POST /api/pets).
 */
data class PetRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("breed")
    val breed: String? = null,
    @SerializedName("birth_date")
    val birthDate: String? = null,
    @SerializedName("photo_url")
    val photoUrl: String? = null
)
