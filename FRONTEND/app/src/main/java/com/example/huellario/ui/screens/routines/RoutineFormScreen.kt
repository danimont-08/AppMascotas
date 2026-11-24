package com.example.huellario.ui.screens.routines

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huellario.ui.components.HuellarioButton
import com.example.huellario.ui.components.HuellarioFormCard
import com.example.huellario.ui.components.HuellarioTextField
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.ErrorContainerRed
import com.example.huellario.ui.theme.ErrorRed
import com.example.huellario.ui.theme.FondoClaro
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.VioletaPrincipal

/**
 * Pantalla de formulario para crear o editar una rutina.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineFormScreen(
    routineId: Int? = null,
    defaultPetId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: RoutineFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(routineId, defaultPetId) {
        viewModel.initForm(routineId, defaultPetId)
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = VioletaPrincipal
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (routineId != null) "Editar Rutina" else "Crear Nueva Rutina",
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

                HuellarioFormCard {
                    // Mascota Asociada
                    Text(
                        text = "Mascota Asociada *",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextoCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.pets.isEmpty()) {
                        Text(
                            text = "Debes registrar al menos una mascota antes de crear una rutina.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorRed
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uiState.pets.forEach { pet ->
                                FilterChip(
                                    selected = uiState.petId == pet.id,
                                    onClick = { viewModel.onPetSelected(pet.id) },
                                    label = { Text(pet.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VioletaPrincipal,
                                        selectedLabelColor = BlancoPuro,
                                        containerColor = AzulSuave,
                                        labelColor = TextoCharcoal
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HuellarioTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onTitleChanged(it) },
                        label = "Nombre de la actividad * (ej. Dar alimento, Pasear)",
                        errorMessage = uiState.titleError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Categoría
                    Text(
                        text = "Categoría *",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextoCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf(
                        "ALIMENTACION" to "Alimentación",
                        "AGUA" to "Agua",
                        "HIGIENE" to "Higiene",
                        "EJERCICIO" to "Ejercicio",
                        "MEDICACION" to "Medicación",
                        "LIMPIEZA" to "Limpieza",
                        "SOCIALIZACION" to "Socialización",
                        "OTRO" to "Otro"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        categories.chunked(3).forEach { chunk ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                chunk.forEach { (code, label) ->
                                    FilterChip(
                                        selected = uiState.category == code,
                                        onClick = { viewModel.onCategoryChanged(code) },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = VioletaPrincipal,
                                            selectedLabelColor = BlancoPuro,
                                            containerColor = AzulSuave,
                                            labelColor = TextoCharcoal
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Prioridad: define el orden en el feed (Alta aparece primero)
                    Text(
                        text = "Prioridad *",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextoCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val priorities = listOf(
                        "ALTA" to "Alta",
                        "MEDIA" to "Media",
                        "BAJA" to "Baja"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        priorities.forEach { (code, label) ->
                            FilterChip(
                                selected = uiState.priority == code,
                                onClick = { viewModel.onPriorityChanged(code) },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = priorityColor(code),
                                    selectedLabelColor = BlancoPuro,
                                    containerColor = AzulSuave,
                                    labelColor = TextoCharcoal
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HuellarioTextField(
                        value = uiState.scheduledTime,
                        onValueChange = { viewModel.onTimeChanged(it) },
                        label = "Hora programada (HH:MM e.g. 08:00) *",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Frecuencia
                    Text(
                        text = "Frecuencia *",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextoCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val frequencies = listOf(
                        "DIARIA" to "Diaria",
                        "SEMANAL" to "Semanal",
                        "UNICA" to "Una sola vez"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        frequencies.forEach { (code, label) ->
                            FilterChip(
                                selected = uiState.frequency == code,
                                onClick = { viewModel.onFrequencyChanged(code) },
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

                    Spacer(modifier = Modifier.height(20.dp))

                    HuellarioTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.onDescriptionChanged(it) },
                        label = "Notas o instrucciones adicionales (Opcional)",
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    HuellarioButton(
                        text = if (routineId != null) "Guardar Cambios" else "Crear Rutina",
                        isLoading = uiState.isLoading,
                        onClick = {
                            viewModel.saveRoutine {
                                onNavigateBack()
                            }
                        }
                    )
                }
            }
        }
    }
}
