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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // Rutina que el usuario quiere eliminar; mientras no sea null se muestra la confirmación
    var routineToDelete by remember { mutableStateOf<RoutineDto?>(null) }

    routineToDelete?.let { routine ->
        AlertDialog(
            onDismissRequest = { routineToDelete = null },
            title = { Text("Eliminar rutina") },
            text = { Text("¿Seguro que quieres eliminar \"${routine.title}\"? También se borrará su historial de cumplimiento. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRoutine(routine.id)
                    routineToDelete = null
                }) { Text("Eliminar", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { routineToDelete = null }) { Text("Cancelar") }
            }
        )
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
                                onClick = { onEditRoutineClick(routine.id) },
                                onDelete = { routineToDelete = routine }
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
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categoryLabel = when (routine.category.uppercase()) {
        "ALIMENTACION", "AGUA" -> "Alimentación"
        "HIGIENE", "LIMPIEZA" -> "Higiene"
        "SALUD", "MEDICACION" -> "Salud"
        "ACTIVIDAD", "EJERCICIO", "SOCIALIZACION" -> "Actividad"
        "JUEGO" -> "Juego"
        "OTRO" -> "Otro"
        else -> routine.category.replaceFirstChar { it.uppercase() }
    }

    val isCompleted = routine.status?.uppercase() in setOf("COMPLETADA", "COMPLETED")

    val priorityCode = routine.priority?.uppercase() ?: "MEDIA"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            // Casilla de cumplimiento: chulito verde si está hecha, círculo vacío si no
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(enabled = canChangeStatus) {
                        onStatusChange(if (isCompleted) "PENDING" else "COMPLETED")
                    }
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isCompleted) "Marcar como pendiente" else "Marcar como hecha",
                    tint = if (isCompleted) SuccessGreen else TextoMuted,
                    modifier = Modifier.size(32.dp)
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
                    IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar rutina",
                            tint = VioletaPrincipal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar rutina",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Prioridad y categoría como texto simple (sin etiquetas)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(priorityColor(priorityCode))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Prioridad ${priorityLabel(priorityCode).lowercase()}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = priorityColor(priorityCode)
                    )
                    Text(
                        text = "  ·  $categoryLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoMuted
                    )
                }
            }
        }
    }
}

/** Etiqueta visible de la prioridad de una rutina. */
fun priorityLabel(priority: String): String = when (priority.uppercase()) {
    "ALTA" -> "Alta"
    "BAJA" -> "Baja"
    else -> "Media"
}

/** Color asociado a cada prioridad (rojo alta, ambar media, verde baja). */
fun priorityColor(priority: String): Color = when (priority.uppercase()) {
    "ALTA" -> ErrorRed
    "BAJA" -> SuccessGreen
    else -> Color(0xFFB26A00)
}
