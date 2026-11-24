package com.example.huellario.ui.screens.pets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellario.data.remote.ApiConfig
import com.example.huellario.ui.components.HuellarioButton
import com.example.huellario.ui.components.HuellarioFormCard
import com.example.huellario.ui.components.HuellarioTextField
import com.example.huellario.ui.components.DateWheelSheet
import com.example.huellario.ui.components.PickerField
import com.example.huellario.ui.theme.AzulCielo
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.ErrorContainerRed
import com.example.huellario.ui.theme.ErrorRed
import com.example.huellario.ui.theme.FondoClaro
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted
import com.example.huellario.ui.theme.VioletaPrincipal

/**
 * Pantalla para registrar o editar una mascota con Photo Picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    petId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: PetFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(petId) {
        if (petId != null) {
            viewModel.loadPetForEditing(petId)
        }
    }

    // Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onPhotoSelected(uri)
    }

    Scaffold(
        containerColor = FondoClaro,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = VioletaPrincipal
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (petId != null) "Editar Mascota" else "Registrar Mascota",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextoCharcoal
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorContainerRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Sección Foto de la mascota
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(AzulSuave)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        val displayUrl = if (uiState.photoUri != null) {
                            uiState.photoUri.toString()
                        } else if (!uiState.existingPhotoUrl.isNullOrBlank()) {
                            if (uiState.existingPhotoUrl!!.startsWith("http")) uiState.existingPhotoUrl!!
                            else "${ApiConfig.baseUrl.removeSuffix("api/")}${uiState.existingPhotoUrl!!.removePrefix("/")}"
                        } else null

                        if (displayUrl != null) {
                            AsyncImage(
                                model = displayUrl,
                                contentDescription = "Foto de la mascota",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = VioletaPrincipal,
                                modifier = Modifier
                                    .size(52.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VioletaPrincipal)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Cambiar foto",
                                tint = BlancoPuro,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Toca para seleccionar una foto de la galería",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoMuted
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Formulario
                HuellarioFormCard {
                    HuellarioTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = "Nombre de la mascota *",
                        errorMessage = uiState.nameError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Especie *",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextoCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val speciesOptions = listOf(
                        "PERRO" to "Perro",
                        "GATO" to "Gato",
                        "AVE" to "Ave",
                        "HAMSTER" to "Hámster",
                        "PEZ" to "Pez",
                        "OTRO" to "Otro"
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        speciesOptions.take(3).forEach { (code, label) ->
                            FilterChip(
                                selected = uiState.species == code,
                                onClick = { viewModel.onSpeciesChanged(code) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VioletaPrincipal,
                                    selectedLabelColor = BlancoPuro,
                                    containerColor = AzulSuave,
                                    labelColor = TextoCharcoal
                                )
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        speciesOptions.drop(3).forEach { (code, label) ->
                            FilterChip(
                                selected = uiState.species == code,
                                onClick = { viewModel.onSpeciesChanged(code) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VioletaPrincipal,
                                    selectedLabelColor = BlancoPuro,
                                    containerColor = AzulSuave,
                                    labelColor = TextoCharcoal
                                )
                            )
                        }
                    }

                    if (uiState.species == "AVE") {
                        Spacer(modifier = Modifier.height(12.dp))
                        HuellarioTextField(
                            value = uiState.specificSpecies,
                            onValueChange = { viewModel.onSpecificSpeciesChanged(it) },
                            label = "Tipo de ave (ej. Canario, Periquito, Loro)",
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )
                    } else if (uiState.species == "OTRO") {
                        Spacer(modifier = Modifier.height(12.dp))
                        HuellarioTextField(
                            value = uiState.specificSpecies,
                            onValueChange = { viewModel.onSpecificSpeciesChanged(it) },
                            label = "Especificar animal (ej. Conejo, Hurón, Tortuga)",
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HuellarioTextField(
                        value = uiState.breed,
                        onValueChange = { viewModel.onBreedChanged(it) },
                        label = "Raza o variedad (Opcional)",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BirthDatePickerField(
                        value = uiState.birthDate,
                        onDateSelected = { viewModel.onBirthDateChanged(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HuellarioTextField(
                        value = uiState.weightKg,
                        onValueChange = { viewModel.onWeightChanged(it) },
                        label = "Peso en kg (Opcional, ej. 4.5)",
                        errorMessage = uiState.weightError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    HuellarioButton(
                        text = if (petId != null) "Guardar Cambios" else "Registrar Mascota",
                        isLoading = uiState.isLoading,
                        onClick = {
                            viewModel.savePet(context) {
                                onNavigateBack()
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Campo de fecha de nacimiento: al tocarlo abre una rueda (mes, día, año) en español.
 * Guarda la fecha como AAAA-MM-DD, el formato que espera el backend. No permite fechas futuras.
 */
@Composable
private fun BirthDatePickerField(
    value: String,
    onDateSelected: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val currentDate = runCatching { LocalDate.parse(value.take(10)) }.getOrNull()
    val spanish = java.util.Locale.forLanguageTag("es-CO")
    val shownValue = currentDate?.format(java.time.format.DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", spanish)) ?: ""

    PickerField(
        value = shownValue,
        label = "Fecha de nacimiento (Opcional)",
        placeholder = "Toca para elegir la fecha",
        icon = Icons.Default.CalendarMonth,
        onClick = { showSheet = true }
    )

    if (showSheet) {
        DateWheelSheet(
            title = "Fecha de nacimiento",
            initialDate = currentDate,
            onDismiss = { showSheet = false },
            onConfirm = { date ->
                onDateSelected(date.toString())
                showSheet = false
            },
            onClear = if (currentDate != null) {
                {
                    onDateSelected("")
                    showSheet = false
                }
            } else null
        )
    }
}
