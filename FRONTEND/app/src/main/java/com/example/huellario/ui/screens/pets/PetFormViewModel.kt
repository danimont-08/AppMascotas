package com.example.huellario.ui.screens.pets

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.PetRequest
import com.example.huellario.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel encargado del formulario de creación y edición de mascotas.
 */
class PetFormViewModel(
    private val petRepository: PetRepository = PetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetFormUiState())
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    fun loadPetForEditing(petId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = petRepository.getPetById(petId)) {
                is ApiResult.Success -> {
                    val pet = result.data
                    val (baseSpecies, custom) = parseSpecies(pet.species)
                    _uiState.update {
                        it.copy(
                            petId = pet.id,
                            name = pet.name,
                            species = baseSpecies,
                            specificSpecies = custom,
                            breed = pet.breed ?: "",
                            birthDate = pet.birthDate ?: "",
                            existingPhotoUrl = pet.photoUrl,
                            isLoading = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun parseSpecies(speciesRaw: String): Pair<String, String> {
        val upper = speciesRaw.uppercase()
        return when {
            upper == "PERRO" || upper == "GATO" || upper == "AVE" || upper == "HAMSTER" || upper == "PEZ" -> Pair(upper, "")
            upper.startsWith("AVE:") -> Pair("AVE", speciesRaw.substringAfter("AVE:").trim())
            upper.startsWith("OTRO:") -> Pair("OTRO", speciesRaw.substringAfter("OTRO:").trim())
            else -> Pair("OTRO", speciesRaw)
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, errorMessage = null) }
    }

    fun onSpeciesChanged(species: String) {
        _uiState.update { it.copy(species = species, specificSpecies = "") }
    }

    fun onSpecificSpeciesChanged(specific: String) {
        _uiState.update { it.copy(specificSpecies = specific) }
    }

    fun onBreedChanged(breed: String) {
        _uiState.update { it.copy(breed = breed) }
    }

    fun onBirthDateChanged(birthDate: String) {
        _uiState.update { it.copy(birthDate = birthDate) }
    }

    fun onPhotoSelected(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun savePet(context: Context, onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val name = currentState.name.trim()

        if (name.isEmpty()) {
            _uiState.update { it.copy(nameError = "El nombre de la mascota es obligatorio") }
            return
        }

        val finalSpecies = when (currentState.species) {
            "AVE" -> if (currentState.specificSpecies.isNotBlank()) "Ave: ${currentState.specificSpecies.trim()}" else "AVE"
            "OTRO" -> if (currentState.specificSpecies.isNotBlank()) "Otro: ${currentState.specificSpecies.trim()}" else "OTRO"
            else -> currentState.species
        }

        val formattedBirthDate = currentState.birthDate.trim().replace(" ", "-").replace("/", "-").ifEmpty { null }

        val petRequest = PetRequest(
            name = name,
            species = finalSpecies,
            breed = currentState.breed.trim().ifEmpty { null },
            birthDate = formattedBirthDate
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (currentState.petId != null) {
                petRepository.updatePet(currentState.petId, petRequest)
            } else {
                petRepository.createPet(petRequest)
            }

            when (result) {
                is ApiResult.Success -> {
                    val savedPet = result.data
                    if (currentState.photoUri != null) {
                        val imageFile = getFileFromUri(context, currentState.photoUri)
                        if (imageFile != null) {
                            petRepository.uploadPetPhoto(savedPet.id, imageFile)
                        }
                    }
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val file = File(context.cacheBufferDir(), "pet_temp_${System.currentTimeMillis()}.jpg")
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun Context.cacheBufferDir(): File {
        val dir = File(cacheDir, "pet_images")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
