package com.mew.planify.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mew.planify.data.local.AppDatabase
import com.mew.planify.data.repository.TareaRepository
import com.mew.planify.ui.screens.tareas.DetalleTareaScreen
import com.mew.planify.ui.screens.tareas.MostrarTareasScreen
import com.mew.planify.ui.screens.tareas.CrearTareaScreen
import com.mew.planify.ui.viewmodel.TareaViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavigation(db: AppDatabase) {
    val navController = rememberNavController()


    val tareaDao = db.tareaDao()
    val repository = TareaRepository(tareaDao)
    val viewModel = TareaViewModel(repository)

    NavHost(navController = navController, startDestination = "mostrar_tareas") {
        composable("crear_tarea") {
            CrearTareaScreen(
                onBack = {
                    navController.popBackStack() // Regresar a la lista de tareas
                },
                viewModel = viewModel
            )
        }

        composable("mostrar_tareas") {
            MostrarTareasScreen(
                onCrearTareaClick = { navController.navigate("crear_tarea") },
                onTareaClick = { tareaId ->
                    navController.navigate("detalle_tarea/$tareaId")
                },
                viewModel = viewModel
            )
        }

        composable(
            route = "detalle_tarea/{tareaId}",
            arguments = listOf(navArgument("tareaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getInt("tareaId") ?: 0
            CrearTareaScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel,
                tareaId = tareaId
            )
//            DetalleTareaScreen(
//                tareaId = tareaId,
//                onBack = { navController.popBackStack() },
//                viewModel = viewModel
//            )
        }
    }
}