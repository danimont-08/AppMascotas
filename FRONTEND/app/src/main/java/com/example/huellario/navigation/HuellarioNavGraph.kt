package com.example.huellario.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.huellario.data.model.UserDto
import com.example.huellario.data.remote.TokenManager
import com.example.huellario.ui.components.HuellarioBottomBar
import com.example.huellario.ui.screens.home.HomeScreen
import com.example.huellario.ui.screens.login.LoginScreen
import com.example.huellario.ui.screens.pets.PetDetailScreen
import com.example.huellario.ui.screens.pets.PetFormScreen
import com.example.huellario.ui.screens.pets.PetsScreen
import com.example.huellario.ui.screens.profile.ProfileScreen
import com.example.huellario.ui.screens.register.RegisterScreen
import com.example.huellario.ui.screens.routines.RoutineCalendarScreen
import com.example.huellario.ui.screens.routines.RoutineFormScreen
import com.example.huellario.ui.screens.routines.RoutinesScreen

/**
 * Navegación principal Huellario.
 */
@Composable
fun HuellarioNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loggedUser by remember { mutableStateOf<UserDto?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Rutas que muestran la barra inferior (coincide con prefijo para soportar ?petId=)
    val isMainTab = currentRoute == NavRoutes.HOME ||
            currentRoute == NavRoutes.PETS ||
            currentRoute?.startsWith("routines") == true ||
            currentRoute == NavRoutes.PROFILE

    fun handleLogout() {
        TokenManager.getInstance(context).clearToken()
        loggedUser = null
        navController.navigate(NavRoutes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            if (isMainTab) {
                HuellarioBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LOGIN,
            modifier = Modifier.padding(if (isMainTab) innerPadding else PaddingValues())
        ) {
            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                    onLoginSuccess = { user ->
                        loggedUser = user
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = { user ->
                        loggedUser = user
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.REGISTER) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.HOME) {
                HomeScreen(
                    user = loggedUser,
                    onNavigateToPets = { navController.navigate(NavRoutes.PETS) },
                    onNavigateToRoutines = { navController.navigate(NavRoutes.ROUTINES) },
                    onNavigateToProfile = { navController.navigate(NavRoutes.PROFILE) },
                    onAddPetClick = { navController.navigate(NavRoutes.petForm()) },
                    onPetClick = { petId -> navController.navigate(NavRoutes.petDetail(petId)) }
                )
            }

            composable(NavRoutes.PETS) {
                PetsScreen(
                    onAddPetClick = { navController.navigate(NavRoutes.petForm()) },
                    onPetClick = { petId -> navController.navigate(NavRoutes.petDetail(petId)) }
                )
            }

            composable(
                route = NavRoutes.PET_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getInt("id") ?: 0
                PetDetailScreen(
                    petId = petId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditPet = { id -> navController.navigate(NavRoutes.petForm(id)) },
                    onViewRoutines = { id -> navController.navigate(NavRoutes.routines(id)) }
                )
            }

            composable(
                route = NavRoutes.PET_FORM,
                arguments = listOf(navArgument("id") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val idArg = backStackEntry.arguments?.getInt("id")
                val petId = if (idArg == null || idArg == -1) null else idArg
                PetFormScreen(
                    petId = petId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Rutinas generales (sin filtro de mascota)
            composable(NavRoutes.ROUTINES) {
                RoutinesScreen(
                    initialPetId = null,
                    onAddRoutineClick = { petId -> navController.navigate(NavRoutes.routineForm(petId = petId)) },
                    onEditRoutineClick = { routineId -> navController.navigate(NavRoutes.routineForm(id = routineId)) },
                    onViewCalendar = { navController.navigate(NavRoutes.ROUTINE_CALENDAR) }
                )
            }

            // Rutinas filtradas por mascota (desde PetDetailScreen)
            composable(
                route = NavRoutes.ROUTINES_BY_PET,
                arguments = listOf(navArgument("petId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getInt("petId")?.takeIf { it != -1 }
                RoutinesScreen(
                    initialPetId = petId,
                    onAddRoutineClick = { pid -> navController.navigate(NavRoutes.routineForm(petId = pid)) },
                    onEditRoutineClick = { routineId -> navController.navigate(NavRoutes.routineForm(id = routineId)) },
                    onViewCalendar = { navController.navigate(NavRoutes.ROUTINE_CALENDAR) }
                )
            }

            composable(
                route = NavRoutes.ROUTINE_FORM,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument("petId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val rId = backStackEntry.arguments?.getInt("id")
                val pId = backStackEntry.arguments?.getInt("petId")
                val routineId = if (rId == null || rId == -1) null else rId
                val defaultPetId = if (pId == null || pId == -1) null else pId
                RoutineFormScreen(
                    routineId = routineId,
                    defaultPetId = defaultPetId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.ROUTINE_CALENDAR) {
                RoutineCalendarScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.PROFILE) {
                ProfileScreen(
                    user = loggedUser,
                    onProfileUpdated = { updatedUser -> loggedUser = updatedUser },
                    onLogout = { handleLogout() }
                )
            }
        }
    }
}

