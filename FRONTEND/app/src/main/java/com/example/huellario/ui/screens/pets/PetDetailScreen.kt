package com.example.huellario.ui.screens.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellario.data.remote.ApiConfig
import com.example.huellario.ui.components.HuellarioFormCard
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.ErrorRed
import com.example.huellario.ui.theme.FondoClaro
import com.example.huellario.ui.theme.Lavanda
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted
import com.example.huellario.ui.theme.VioletaPrincipal

/**
 * Pantalla de perfil y detalle individual de una mascota.
 */
@Composable
fun PetDetailScreen(
    petId: Int,
    onNavigateBack: () -> Unit,
    onEditPet: (Int) -> Unit,
    onViewRoutines: (Int) -> Unit,
    viewModel: PetDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petId) {
        viewModel.loadPetDetail(petId)
    }

    Scaffold(
        containerColor = FondoClaro,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = VioletaPrincipal)
                }
            } else if (uiState.pet != null) {
                val pet = uiState.pet!!
                val fullPhotoUrl = ApiConfig.resolveImageUrl(pet.photoUrl)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    // Header con botón de regreso y título
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = VioletaPrincipal
                            )
                        }

                        Text(
                            text = "Perfil de la Mascota",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextoCharcoal
                        )

                        IconButton(onClick = { onEditPet(pet.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = VioletaPrincipal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Foto principal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (fullPhotoUrl != null) {
                            AsyncImage(
                                model = fullPhotoUrl,
                                contentDescription = pet.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(AzulSuave)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = pet.name,
                                    tint = VioletaPrincipal,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = pet.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextoCharcoal
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val speciesText = when (pet.species.uppercase()) {
                            "PERRO" -> "Perro"
                            "GATO" -> "Gato"
                            "AVE" -> "Ave"
                            "HAMSTER" -> "Hámster"
                            "PEZ" -> "Pez"
                            else -> pet.species.replaceFirstChar { it.uppercase() }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Lavanda)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = speciesText,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = VioletaPrincipal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Detalles en Tarjeta
                    HuellarioFormCard {
                        Text(
                            text = "Información detallada",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextoCharcoal
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DetailRow(label = "Especie", value = pet.species)

                        if (!pet.breed.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            DetailRow(label = "Raza / Variedad", value = pet.breed)
                        }

                        if (!pet.birthDate.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            DetailRow(label = "Fecha de nacimiento", value = pet.birthDate.take(10))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para ir a Rutinas de esta mascota
                    Button(
                        onClick = { onViewRoutines(pet.id) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VioletaPrincipal,
                            contentColor = BlancoPuro
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.EventNote, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ver Rutinas de ${pet.name}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Eliminar Mascota
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar Mascota", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Diálogo de confirmación de eliminación
            if (showDeleteDialog && uiState.pet != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Eliminar a ${uiState.pet?.name}") },
                    text = { Text("¿Estás seguro de eliminar esta mascota? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deletePet(petId) {
                                    onNavigateBack()
                                }
                            }
                        ) {
                            Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextoMuted)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextoCharcoal)
    }
}
