package com.example.huellario.navigation

/**
 * Constantes centralizadas para la navegación de la aplicación Huellario.
 */
object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val PETS = "pets"
    const val PET_DETAIL = "pet_detail/{id}"
    const val PET_FORM = "pet_form?id={id}"
    const val ROUTINES = "routines"
    const val ROUTINES_BY_PET = "routines?petId={petId}"
    const val ROUTINE_FORM = "routine_form?id={id}&petId={petId}"
    const val ROUTINE_CALENDAR = "routine_calendar"

    fun petDetail(id: Int) = "pet_detail/$id"
    fun petForm(id: Int? = null) = if (id != null) "pet_form?id=$id" else "pet_form"
    fun routines(petId: Int? = null) = if (petId != null) "routines?petId=$petId" else "routines"
    fun routineForm(id: Int? = null, petId: Int? = null): String {
        val queryParams = mutableListOf<String>()
        if (id != null) queryParams.add("id=$id")
        if (petId != null) queryParams.add("petId=$petId")
        return if (queryParams.isNotEmpty()) "routine_form?${queryParams.joinToString("&")}" else "routine_form"
    }
}
