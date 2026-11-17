package com.example.huellario.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.huellario.R
import com.example.huellario.data.model.PetDto
import com.example.huellario.data.model.UserDto
import com.example.huellario.data.remote.ApiConfig
import com.example.huellario.ui.components.HuellarioFormCard
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.FondoClaro
import com.example.huellario.ui.theme.Lavanda
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted
import com.example.huellario.ui.theme.VioletaPrincipal

/**
 * Pantalla Principal (Home Dashboard) de Huellario.
 */
@Composable
fun HomeScreen(
    user: UserDto?,
    onNavigateToPets: () -> Unit,
    onNavigateToRoutines: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddPetClick: () -> Unit,
    onPetClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val firstName = user?.fullName?.trim()?.split(" ")?.firstOrNull() ?: "Usuario"

    LaunchedEffect(Unit) {
        viewModel.loadPets()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Header con Saludo y Perfil
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡Hola, $firstName!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextoCharcoal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompaña el cuidado cotidiano de tus mascotas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AzulSuave)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Perfil de usuario",
                            tint = VioletaPrincipal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Banner compacto institucional
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulSuave),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_sinfondo),
                            contentDescription = "Logo Huellario",
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Huellario",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextoCharcoal
                            )
                            Text(
                                text = "Cuidado amoroso para perros, gatos, aves, hámsters y peces",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Encabezado Mis Mascotas
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Mis mascotas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextoCharcoal
                    )

                    OutlinedButton(
                        onClick = onNavigateToPets,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VioletaPrincipal)
                    ) {
                        Text(
                            text = "Ver todas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido Mis Mascotas
                if (uiState.isLoadingPets) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        CircularProgressIndicator(color = VioletaPrincipal)
                    }
                } else if (uiState.pets.isEmpty()) {
                    HuellarioFormCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(AzulSuave)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = VioletaPrincipal,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Todavía no has registrado mascotas",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextoCharcoal,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Agrega a tu perro, gato, ave, hámster o pez para comenzar a organizar sus actividades.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoMuted,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onAddPetClick,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VioletaPrincipal,
                                    contentColor = BlancoPuro
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Registrar mascota")
                            }
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.pets) { pet ->
                            HomePetCardItem(
                                pet = pet,
                                onClick = { onPetClick(pet.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Acceso a Rutinas de Cuidado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Rutinas de cuidado",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextoCharcoal
                    )

                    OutlinedButton(
                        onClick = onNavigateToRoutines,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VioletaPrincipal)
                    ) {
                        Text(
                            text = "Ir a Rutinas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HuellarioFormCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Lavanda)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EventNote,
                                contentDescription = null,
                                tint = VioletaPrincipal,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Diario de cumplimiento",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextoCharcoal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Organiza alimentación, paseos, medicación y aseo de tus mascotas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual para el carrusel de mascotas en Home.
 */
@Composable
fun HomePetCardItem(
    pet: PetDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fullPhotoUrl = ApiConfig.resolveImageUrl(pet.photoUrl)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            if (fullPhotoUrl != null) {
                AsyncImage(
                    model = fullPhotoUrl,
                    contentDescription = pet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AzulSuave)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = pet.name,
                        tint = VioletaPrincipal,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextoCharcoal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            val speciesText = when (pet.species.uppercase()) {
                "PERRO" -> "Perro"
                "GATO" -> "Gato"
                "AVE" -> "Ave"
                "HAMSTER" -> "Hámster"
                "PEZ" -> "Pez"
                else -> pet.species.replaceFirstChar { it.uppercase() }
            }

            Text(
                text = speciesText,
                style = MaterialTheme.typography.bodySmall,
                color = TextoMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
