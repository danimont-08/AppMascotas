package com.example.huellario.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Representación de una rutina devuelta por la API.
 */
data class RoutineDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("pet_id")
    val petId: Int,
    @SerializedName("pet_name")
    val petName: String? = null,
    @SerializedName("pet_species")
    val petSpecies: String? = null,
    @SerializedName("name")
    val title: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("scheduled_time")
    val scheduledTime: String,
    @SerializedName("frequency")
    val frequency: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("selected_days")
    val selectedDays: String? = null,
    @SerializedName("is_active")
    val isActiveRaw: Any? = true,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("status")
    val status: String? = "PENDIENTE",
    @SerializedName("log_id")
    val logId: Int? = null
) {
    val isActive: Boolean
        get() = when (isActiveRaw) {
            is Boolean -> isActiveRaw
            is Number -> isActiveRaw.toInt() != 0
            else -> true
        }
}

fun RoutineDto.formatScheduledTime(): String {
        val localTime = LocalTime.parse(scheduledTime.take(5), DateTimeFormatter.ofPattern("HH:mm"))
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
}

/**
 * Solicitud para crear o actualizar una rutina (POST/PUT /api/routines).
 */
data class RoutineRequest(
    @SerializedName("pet_id")
    val petId: Int,
    @SerializedName("name")
    val title: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("scheduled_time")
    val scheduledTime: String,
    @SerializedName("frequency")
    val frequency: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("selected_days")
    val selectedDays: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true
)

/**
 * Respuesta del resumen diario (GET /api/routines/daily).
 */
data class DailyRoutineResponse(
    @SerializedName("date")
    val date: String,
    @SerializedName("petId")
    val petId: Int? = null,
    @SerializedName("routines")
    val routines: List<RoutineDto> = emptyList()
)

/**
 * Solicitud para registrar o cambiar el cumplimiento de una rutina (POST /api/routines/logs).
 * El campo `scheduled_date` debe coincidir con el backend.
 */
data class RoutineLogRequest(
    @SerializedName("routine_id")
    val routineId: Int,
    @SerializedName("scheduled_date")
    val logDate: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("notes")
    val notes: String? = null
)
