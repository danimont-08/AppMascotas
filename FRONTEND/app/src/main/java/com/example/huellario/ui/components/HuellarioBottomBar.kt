package com.example.huellario.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.huellario.navigation.NavRoutes
import com.example.huellario.ui.theme.AzulPrincipal
import com.example.huellario.ui.theme.AzulSuave
import com.example.huellario.ui.theme.BlancoPuro
import com.example.huellario.ui.theme.TextoCharcoal
import com.example.huellario.ui.theme.TextoMuted

/**
 * Barra de navegación inferior con 4 pestañas: Inicio, Mascotas, Rutinas y Perfil.
 * El indicador activo usa AzulPrincipal (#83CBE8) como color protagonista.
 */
@Composable
fun HuellarioBottomBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Considera activa la pestaña de Rutinas si la ruta empieza con "routines"
    val isRoutinesActive = currentRoute?.startsWith("routines") == true

    NavigationBar(containerColor = BlancoPuro, modifier = modifier) {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HOME,
            onClick = { onNavigateToRoute(NavRoutes.HOME) },
            icon = { Icon(imageVector = if (currentRoute == NavRoutes.HOME) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextoCharcoal,
                selectedTextColor = TextoCharcoal,
                unselectedIconColor = TextoMuted,
                unselectedTextColor = TextoMuted,
                indicatorColor = AzulPrincipal
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.PETS,
            onClick = { onNavigateToRoute(NavRoutes.PETS) },
            icon = { Icon(imageVector = if (currentRoute == NavRoutes.PETS) Icons.Filled.Pets else Icons.Outlined.Pets, contentDescription = "Mascotas") },
            label = { Text("Mascotas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextoCharcoal,
                selectedTextColor = TextoCharcoal,
                unselectedIconColor = TextoMuted,
                unselectedTextColor = TextoMuted,
                indicatorColor = AzulPrincipal
            )
        )

        NavigationBarItem(
            selected = isRoutinesActive,
            onClick = { onNavigateToRoute(NavRoutes.ROUTINES) },
            icon = { Icon(imageVector = if (isRoutinesActive) Icons.Filled.EventNote else Icons.Outlined.EventNote, contentDescription = "Rutinas") },
            label = { Text("Rutinas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextoCharcoal,
                selectedTextColor = TextoCharcoal,
                unselectedIconColor = TextoMuted,
                unselectedTextColor = TextoMuted,
                indicatorColor = AzulPrincipal
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.PROFILE,
            onClick = { onNavigateToRoute(NavRoutes.PROFILE) },
            icon = { Icon(imageVector = if (currentRoute == NavRoutes.PROFILE) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextoCharcoal,
                selectedTextColor = TextoCharcoal,
                unselectedIconColor = TextoMuted,
                unselectedTextColor = TextoMuted,
                indicatorColor = AzulPrincipal
            )
        )
    }
}

