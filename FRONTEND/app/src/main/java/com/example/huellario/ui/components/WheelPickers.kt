package com.example.huellario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted
import com.example.huellario.ui.theme.VioletaPrincipal
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs

private val ItemHeight: Dp = 40.dp
private const val VisibleItems = 5

/**
 * Columna giratoria (tipo rueda) que se detiene centrada en un elemento.
 */
@Composable
fun WheelColumn(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Elemento que queda en el centro de la rueda
    val centeredIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - center) }?.index ?: selectedIndex
        }
    }

    LaunchedEffect(centeredIndex, items.size) {
        if (items.isNotEmpty()) onSelectedIndexChange(centeredIndex.coerceIn(items.indices))
    }

    Box(modifier = modifier.height(ItemHeight * VisibleItems), contentAlignment = Alignment.Center) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = ItemHeight * (VisibleItems / 2)),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(items) { index, item ->
                val distance = abs(index - centeredIndex)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ItemHeight)
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        fontSize = if (distance == 0) 20.sp else 17.sp,
                        fontWeight = if (distance == 0) FontWeight.SemiBold else FontWeight.Normal,
                        color = when (distance) {
                            0 -> TextoCharcoal
                            1 -> TextoMuted
                            else -> TextoMuted.copy(alpha = 0.45f)
                        }
                    )
                }
            }
        }
    }
}

/** Contenedor común de las ruedas: franja de selección, título y botón "Listo". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WheelSheet(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onClear: (() -> Unit)?,
    wheels: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BlancoPuro
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextoCharcoal,
                    modifier = Modifier.weight(1f)
                )
                if (onClear != null) {
                    TextButton(onClick = onClear) { Text("Quitar", color = TextoMuted) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                // Franja que marca la fila seleccionada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ItemHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AzulSuave.copy(alpha = 0.55f))
                )
                wheels()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VioletaPrincipal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Listo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

/**
 * Selector de fecha tipo rueda (mes, día, año) en una hoja inferior, en español.
 * No permite fechas posteriores a [maxDate].
 */
@Composable
fun DateWheelSheet(
    title: String,
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    onClear: (() -> Unit)? = null,
    minYear: Int = 1990,
    maxDate: LocalDate = LocalDate.now()
) {
    val spanish = Locale.forLanguageTag("es-CO")
    val start = (initialDate ?: maxDate).let { if (it.isAfter(maxDate)) maxDate else it }
    val years = remember(minYear, maxDate) { (minYear..maxDate.year).toList() }
    val months = remember {
        Month.entries.map { m -> m.getDisplayName(TextStyle.FULL, spanish).replaceFirstChar { it.uppercase() } }
    }

    var monthIndex by remember { mutableIntStateOf(start.monthValue - 1) }
    var yearIndex by remember { mutableIntStateOf(years.indexOf(start.year).coerceAtLeast(0)) }
    var dayIndex by remember { mutableIntStateOf(start.dayOfMonth - 1) }

    val daysInMonth = LocalDate.of(years[yearIndex], monthIndex + 1, 1).lengthOfMonth()
    val days = remember(daysInMonth) { (1..daysInMonth).map { it.toString() } }

    WheelSheet(
        title = title,
        onDismiss = onDismiss,
        onClear = onClear,
        onConfirm = {
            val day = (dayIndex + 1).coerceAtMost(daysInMonth)
            val chosen = LocalDate.of(years[yearIndex], monthIndex + 1, day)
            onConfirm(if (chosen.isAfter(maxDate)) maxDate else chosen)
        }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            WheelColumn(months, monthIndex, { monthIndex = it }, Modifier.weight(1.6f))
            WheelColumn(days, dayIndex.coerceAtMost(days.size - 1), { dayIndex = it }, Modifier.weight(0.8f))
            WheelColumn(years.map { it.toString() }, yearIndex, { yearIndex = it }, Modifier.weight(1f))
        }
    }
}

/**
 * Selector de hora tipo rueda (hora, minutos, a. m./p. m.) en una hoja inferior.
 */
@Composable
fun TimeWheelSheet(
    title: String,
    initialTime: LocalTime?,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val start = initialTime ?: LocalTime.now().withSecond(0).withNano(0)
    val hours = remember { (1..12).map { it.toString() } }
    val minutes = remember { (0..59).map { it.toString().padStart(2, '0') } }
    val periods = remember { listOf("a. m.", "p. m.") }

    var hourIndex by remember { mutableIntStateOf(((start.hour + 11) % 12)) }
    var minuteIndex by remember { mutableIntStateOf(start.minute) }
    var periodIndex by remember { mutableIntStateOf(if (start.hour < 12) 0 else 1) }

    WheelSheet(
        title = title,
        onDismiss = onDismiss,
        onClear = null,
        onConfirm = {
            val hour12 = hourIndex + 1
            val hour24 = (hour12 % 12) + if (periodIndex == 1) 12 else 0
            onConfirm(LocalTime.of(hour24, minuteIndex))
        }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            WheelColumn(hours, hourIndex, { hourIndex = it }, Modifier.weight(1f))
            WheelColumn(minutes, minuteIndex, { minuteIndex = it }, Modifier.weight(1f))
            WheelColumn(periods, periodIndex, { periodIndex = it }, Modifier.weight(1f))
        }
    }
}

/**
 * Campo de solo lectura que abre un selector al tocarlo (fecha u hora).
 */
@Composable
fun PickerField(
    value: String,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                singleLine = true,
                isError = errorMessage != null,
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                trailingIcon = { androidx.compose.material3.Icon(icon, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextoCharcoal,
                    disabledContainerColor = BlancoPuro,
                    disabledBorderColor = if (errorMessage != null) com.example.huellario.ui.theme.ErrorRed else com.example.huellario.ui.theme.AzulCielo,
                    disabledLabelColor = if (errorMessage != null) com.example.huellario.ui.theme.ErrorRed else TextoMuted,
                    disabledPlaceholderColor = TextoMuted,
                    disabledTrailingIconColor = VioletaPrincipal
                ),
                modifier = Modifier.fillMaxWidth()
            )
            // Capa transparente encima para abrir el selector al tocar
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onClick)
            )
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = com.example.huellario.ui.theme.ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
