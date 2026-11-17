package com.example.huellario.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.huellario.ui.theme.AzulCielo
import com.example.huellario.ui.theme.AzulPrincipal
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.FondoClaro
import com.example.huellario.ui.theme.Lavanda
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted
import com.example.huellario.ui.theme.VioletaPrincipal
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/** Color por frecuencia de rutina */
private val COLOR_DIARIA = AzulPrincipal         // Azul principal para rutinas diarias
private val COLOR_SEMANAL = VioletaPrincipal      // Violeta para rutinas semanales
private val COLOR_UNICA = AzulCielo               // Azul cielo para rutinas de una sola vez

/**
 * Pantalla de calendario mensual de rutinas.
 * Muestra puntos de color según la frecuencia: Diaria (azul), Semanal (violeta), Única (azul cielo).
 */
@Composable
fun RoutineCalendarScreen(
    onNavigateBack: () -> Unit,
    viewModel: RoutineCalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bogotaZone = ZoneId.of("America/Bogota")
    var currentMonth by remember { mutableStateOf(YearMonth.now(bogotaZone)) }
    var selectedDate by remember { mutableStateOf(LocalDate.now(bogotaZone)) }
    val allRoutines = uiState.routines

    // Rutinas del día seleccionado
    val selectedDayRoutines = allRoutines.filter { routine ->
        routine.isScheduledOn(selectedDate)
    }

    Scaffold(containerColor = FondoClaro) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulPrincipal)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calendario de Rutinas",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextoCharcoal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leyenda de colores
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {
                    LegendItem(color = COLOR_DIARIA, label = "Diaria")
                    LegendItem(color = COLOR_SEMANAL, label = "Semanal")
                    LegendItem(color = COLOR_UNICA, label = "Única")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cabecera del mes con navegación
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Navegación de mes
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior", tint = AzulPrincipal)
                        }
                        Text(
                            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))} ${currentMonth.year}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextoCharcoal
                        )
                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente", tint = AzulPrincipal)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Encabezados días de semana
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TextoMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isLoading) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            CircularProgressIndicator(color = AzulPrincipal)
                        }
                    } else if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = TextoMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                        )
                    } else {
                        CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            allRoutines = allRoutines,
                            onDateSelected = { selectedDate = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de rutinas del día seleccionado
            Text(
                text = "Rutinas del ${selectedDate.dayOfMonth} de ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es"))}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextoCharcoal
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.errorMessage != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else if (selectedDayRoutines.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sin rutinas programadas para este día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedDayRoutines) { routine ->
                        CalendarRoutineItem(routine = routine)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    allRoutines: List<RoutineDto>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = currentMonth.atDay(1)
    // Día de semana 1=Lunes, 7=Domingo
    val startOffset = (firstDay.dayOfWeek.value - 1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val totalCells = startOffset + daysInMonth

    val rows = (totalCells + 6) / 7

    for (row in 0 until rows) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (col in 0..6) {
                val cellIndex = row * 7 + col
                val dayNumber = cellIndex - startOffset + 1

                if (dayNumber < 1 || dayNumber > daysInMonth) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                } else {
                    val date = currentMonth.atDay(dayNumber)
                    val routinesForDay = allRoutines.filter { routine ->
                        routine.isScheduledOn(date)
                    }
                    val hasRoutines = routinesForDay.isNotEmpty()

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    date == selectedDate -> AzulPrincipal
                                    else -> BlancoPuro
                                }
                            )
                            .border(
                                width = if (hasRoutines && date != selectedDate) 1.dp else 0.dp,
                                color = if (hasRoutines) AzulSuave else BlancoPuro,
                                shape = CircleShape
                            )
                            .clickable { onDateSelected(date) }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$dayNumber",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (date == selectedDate) FontWeight.Bold else FontWeight.Normal),
                                color = if (date == selectedDate) BlancoPuro else TextoCharcoal,
                                fontSize = 12.sp
                            )
                            if (routinesForDay.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.Center) {
                                    val hasDaily = routinesForDay.any { it.frequency.uppercase() == "DIARIA" }
                                    val hasWeekly = routinesForDay.any {
                                        it.frequency.uppercase() in setOf("SEMANAL", "DIAS_ESPECIFICOS")
                                    }
                                    val hasOnce = routinesForDay.any { it.frequency.uppercase() == "UNICA" }
                                    if (hasDaily) DotIndicator(color = if (date == selectedDate) BlancoPuro else COLOR_DIARIA)
                                    if (hasWeekly) DotIndicator(color = if (date == selectedDate) BlancoPuro else COLOR_SEMANAL)
                                    if (hasOnce) DotIndicator(color = if (date == selectedDate) BlancoPuro else COLOR_UNICA)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DotIndicator(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .size(5.dp)
            .padding(horizontal = 1.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextoMuted)
    }
}

@Composable
private fun CalendarRoutineItem(routine: RoutineDto) {
    val frequencyColor = when (routine.frequency.uppercase()) {
        "DIARIA" -> COLOR_DIARIA
        "SEMANAL", "DIAS_ESPECIFICOS" -> COLOR_SEMANAL
        "UNICA" -> COLOR_UNICA
        else -> AzulSuave
    }
    val frequencyLabel = when (routine.frequency.uppercase()) {
        "DIARIA" -> "Diaria"
        "SEMANAL" -> "Semanal"
        "UNICA" -> "Una sola vez"
        "DIAS_ESPECIFICOS" -> "Días específicos"
        else -> routine.frequency
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(frequencyColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = routine.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextoCharcoal)
                if (!routine.petName.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Pets, contentDescription = null, tint = TextoMuted, modifier = Modifier.size(12.dp))
                        Text(text = routine.petName, style = MaterialTheme.typography.bodySmall, color = TextoMuted)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = routine.formatScheduledTime(), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = AzulPrincipal)
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Lavanda).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(text = frequencyLabel, fontSize = 10.sp, color = VioletaPrincipal, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

private fun RoutineDto.isScheduledOn(date: LocalDate): Boolean {
    if (!isActive) return false
    val start = LocalDate.parse(startDate)
    val end = endDate?.let(LocalDate::parse)
    if (date.isBefore(start) || (end != null && date.isAfter(end))) return false

    val weekday = date.dayOfWeek.value % 7
    return when (frequency.uppercase()) {
        "DIARIA" -> true
        "SEMANAL" -> date.dayOfWeek == start.dayOfWeek
        "UNICA" -> date == start
        "DIAS_ESPECIFICOS" -> selectedDays.orEmpty()
            .split(Regex("[,;|\\[\\]\"]+"))
            .any { token -> token.toWeekdayIndex() == weekday }
        else -> false
    }
}

private fun String.toWeekdayIndex(): Int? {
    val token = trim().uppercase(Locale.ROOT)
    token.toIntOrNull()?.let { return it.takeIf { day -> day in 0..6 } }
    return when (token) {
        "DOMINGO", "SUNDAY", "DOM", "SUN" -> 0
        "LUNES", "MONDAY", "LUN", "MON" -> 1
        "MARTES", "TUESDAY", "MAR", "TUE" -> 2
        "MIERCOLES", "MIÉRCOLES", "WEDNESDAY", "MIE", "WED" -> 3
        "JUEVES", "THURSDAY", "JUE", "THU" -> 4
        "VIERNES", "FRIDAY", "VIE", "FRI" -> 5
        "SABADO", "SÁBADO", "SATURDAY", "SAB", "SAT" -> 6
        else -> null
    }
}
