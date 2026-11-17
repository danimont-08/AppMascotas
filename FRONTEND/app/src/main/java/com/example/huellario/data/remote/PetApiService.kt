package com.example.huellario.data.remote

import com.example.huellario.data.model.PetDto
import com.example.huellario.data.model.PetRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Servicio Retrofit para las operaciones de mascotas.
 */
interface PetApiService {

    @GET("pets")
    suspend fun listPets(): Response<List<PetDto>>

    @GET("pets/{id}")
    suspend fun getPetById(
        @Path("id") id: Int
    ): Response<PetDto>

    @POST("pets")
    suspend fun createPet(
        @Body request: PetRequest
    ): Response<PetDto>

    @PUT("pets/{id}")
    suspend fun updatePet(
        @Path("id") id: Int,
        @Body request: PetRequest
    ): Response<PetDto>

    @DELETE("pets/{id}")
    suspend fun deletePet(
        @Path("id") id: Int
    ): Response<Unit>

    @Multipart
    @POST("pets/{id}/photo")
    suspend fun uploadPetPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Response<PetDto>
}
