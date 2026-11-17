package com.example.huellario.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huellario.data.model.RoutineDto
import com.example.huellario.data.model.formatScheduledTime
import com.example.huellario.ui.components.HuellarioFormCard
import com.example.huellario.ui.theme.AzulPrincipal
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.ErrorRed
import com.example.huellario.ui.theme.FondoClaro
import com.example.huellario.ui.theme.Lavanda
import com.example.huellario.ui.theme.SuccessContainerGreen
import com.example.huellario.ui.theme.SuccessGreen
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted
import com.example.huellario.ui.theme.VioletaPrincipal
import java.time.LocalDate
import java.time.ZoneId

/**
 * Pantalla principal de consulta y cumplimiento diario de rutinas.
 * [initialPetId] permite pre-filtrar por mascota cuando se llega desde PetDetailScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    initialPetId: Int? = null,
    onAddRoutineClick: (Int?) -> Unit,
    onEditRoutineClick: (Int) -> Unit,
    onViewCalendar: () -> Unit = {},
    viewModel: RoutinesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bogotaZone = ZoneId.of("America/Bogota")
    val today = LocalDate.now(bogotaZone).toString()
    val tomorrow = LocalDate.now(bogotaZone).plusDays(1).toString()

    LaunchedEffect(initialPetId) {
        if (initialPetId != null) viewModel.onPetFilterSelected(initialPetId)
        viewModel.loadPetsAndDailyRoutines()
    }

    Scaffold(
        containerColor = FondoClaro,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddRoutineClick(uiState.selectedPetId) },
                containerColor = AzulPrincipal,
                contentColor = TextoCharcoal,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva rutina")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Header con título y botón de calendario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rutinas de Cuidado",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextoCharcoal
                        )
                        Text(
                            text = "Diario de tareas cotidianas y seguimiento",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoMuted
                        )
                    }
                    IconButton(onClick = onViewCalendar) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Ver calendario",
                            tint = AzulPrincipal,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Fecha
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AzulSuave)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(onClick = {
                        val prevDate = LocalDate.parse(uiState.selectedDate).minusDays(1).toString()
                        viewModel.onDateSelected(prevDate)
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Día anterior", tint = VioletaPrincipal)
                    }

                    val dateLabel = when (uiState.selectedDate) {
                        today -> "Hoy  •  ${uiState.selectedDate}"
                        tomorrow -> "Mañana  •  ${uiState.selectedDate}"
                        else -> uiState.selectedDate
                    }
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextoCharcoal
                    )

                    IconButton(onClick = {
                        val nextDate = LocalDate.parse(uiState.selectedDate).plusDays(1).toString()
                        viewModel.onDateSelected(nextDate)
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Día siguiente", tint = VioletaPrincipal)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filtro por Mascota
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedPetId == null,
                            onClick = { viewModel.onPetFilterSelected(null) },
                            label = { Text("Todas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPrincipal,
                                selectedLabelColor = TextoCharcoal,
                                containerColor = AzulSuave,
                                labelColor = TextoCharcoal
                            )
                        )
                    }
                    items(uiState.pets) { pet ->
                        FilterChip(
                            selected = uiState.selectedPetId == pet.id,
                            onClick = { viewModel.onPetFilterSelected(pet.id) },
                            label = { Text(pet.name) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPrincipal,
                                selectedLabelColor = TextoCharcoal,
                                containerColor = AzulSuave,
                                labelColor = TextoCharcoal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(color = AzulPrincipal)
                    }
                } else if (uiState.dailyRoutines.isEmpty()) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        HuellarioFormCard {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(80.dp).clip(CircleShape).background(AzulSuave)
                                ) {
                                    Icon(imageVector = Icons.Default.EventNote, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(44.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No hay rutinas para esta fecha", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextoCharcoal, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Crea actividades como alimentación, paseos, medicamentos o cepillado.", style = MaterialTheme.typography.bodyMedium, color = TextoMuted, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { onAddRoutineClick(uiState.selectedPetId) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal, contentColor = TextoCharcoal)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Crear rutina")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.dailyRoutines) { routine ->
                            RoutineCardItem(
                                routine = routine,
                                canChangeStatus = uiState.selectedDate <= today,
                                onStatusChange = { newStatus -> viewModel.markLogStatus(routine.id, newStatus) },
                                onClick = { onEditRoutineClick(routine.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual de rutina para el diario de cumplimiento.
 * Usa iconos de Material en lugar de emojis para diseño profesional.
 */
@Composable
fun RoutineCardItem(
    routine: RoutineDto,
    canChangeStatus: Boolean = true,
    onStatusChange: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryLabel = when (routine.category.uppercase()) {
        "ALIMENTACION" -> "Alimentación"
        "AGUA" -> "Agua"
        "HIGIENE" -> "Higiene"
        "EJERCICIO" -> "Ejercicio"
        "MEDICACION" -> "Medicación"
        "LIMPIEZA" -> "Limpieza"
        "SOCIALIZACION" -> "Socialización"
        else -> routine.category.replaceFirstChar { it.uppercase() }
    }

    val currentStatus = when (routine.status?.uppercase()) {
        "COMPLETADA", "COMPLETED" -> "COMPLETED"
        "NO_FINALIZADA" -> "NOT_FINISHED"
        "OMITIDA", "SKIPPED" -> "SKIPPED"
        else -> "PENDING"
    }

    val statusBadgeText = when (currentStatus) {
        "COMPLETED" -> "Completada"
        "SKIPPED" -> "Omitida"
        "NOT_FINISHED" -> "No finalizada"
        else -> "Pendiente"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            // Botón de estado interactivo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (currentStatus) {
                            "COMPLETED" -> SuccessContainerGreen
                            "SKIPPED", "NOT_FINISHED" -> ErrorRed.copy(alpha = 0.15f)
                            else -> AzulSuave
                        }
                    )
                    .clickable(enabled = canChangeStatus) {
                        val nextStatus = when (currentStatus) {
                            "COMPLETED", "SKIPPED" -> "PENDING"
                            else -> "COMPLETED"
                        }
                        onStatusChange(nextStatus)
                    }
            ) {
                Icon(
                    imageVector = when (currentStatus) {
                        "COMPLETED" -> Icons.Default.CheckCircle
                        "SKIPPED", "NOT_FINISHED" -> Icons.Default.Close
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = "Estado de rutina",
                    tint = when (currentStatus) {
                        "COMPLETED" -> SuccessGreen
                        "SKIPPED", "NOT_FINISHED" -> ErrorRed
                        else -> AzulPrincipal
                    },
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = routine.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextoCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = routine.formatScheduledTime(),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AzulPrincipal
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!routine.petName.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(imageVector = Icons.Default.Pets, contentDescription = null, tint = TextoMuted, modifier = Modifier.size(12.dp))
                            Text(text = routine.petName, style = MaterialTheme.typography.bodySmall, color = TextoMuted)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Lavanda)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = categoryLabel, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VioletaPrincipal)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (currentStatus) {
                                    "COMPLETED" -> SuccessContainerGreen
                                    "SKIPPED", "NOT_FINISHED" -> ErrorRed.copy(alpha = 0.15f)
                                    else -> AzulSuave
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (currentStatus) {
                                "COMPLETED" -> SuccessGreen
                                "SKIPPED", "NOT_FINISHED" -> ErrorRed
                                else -> TextoCharcoal
                            }
                        )
                    }
                }
            }
        }
    }
}


