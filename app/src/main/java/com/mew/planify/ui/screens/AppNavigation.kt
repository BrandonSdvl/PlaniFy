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
import com.mew.planify.data.repository.MateriaRepository
import com.mew.planify.data.repository.ProfesorRepository
import com.mew.planify.data.repository.TareaRepository
import com.mew.planify.ui.screens.materias.CrearMateriaScreen
import com.mew.planify.ui.screens.materias.MostrarMateriasScreen
import com.mew.planify.ui.screens.profesores.CrearProfesorScreen
import com.mew.planify.ui.screens.profesores.MostrarProfesoresScreen
import com.mew.planify.ui.screens.tareas.MostrarTareasScreen
import com.mew.planify.ui.screens.tareas.CrearTareaScreen
import com.mew.planify.ui.viewmodel.MateriaViewModel
import com.mew.planify.ui.viewmodel.ProfesorViewModel
import com.mew.planify.ui.viewmodel.TareaViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavigation(db: AppDatabase) {
    val navController = rememberNavController()


    val tareaDao = db.tareaDao()
    val tareaRepository = TareaRepository(tareaDao)
    val tareaViewModel = TareaViewModel(tareaRepository)

    val profesorDao = db.profesorDao()
    val profesorRepository = ProfesorRepository(profesorDao)
    val profesorViewModel = ProfesorViewModel(profesorRepository)

    val materiaDao = db.materiaDao()
    val materiaRepository = MateriaRepository(materiaDao)
    val materiaViewModel = MateriaViewModel(materiaRepository)

    NavHost(navController = navController, startDestination = "mostrar_materias") {
        composable("crear_tarea") {
            CrearTareaScreen(
                onBack = {
                    navController.popBackStack() // Regresar a la lista de tareas
                },
                viewModel = tareaViewModel
            )
        }

        composable("mostrar_tareas") {
            MostrarTareasScreen(
                onCrearTareaClick = { navController.navigate("crear_tarea") },
                onTareaClick = { tareaId ->
                    navController.navigate("detalle_tarea/$tareaId")
                },
                viewModel = tareaViewModel
            )
        }

        composable(
            route = "detalle_tarea/{tareaId}",
            arguments = listOf(navArgument("tareaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getInt("tareaId") ?: 0
            CrearTareaScreen(
                onBack = { navController.popBackStack() },
                viewModel = tareaViewModel,
                tareaId = tareaId
            )
//            DetalleTareaScreen(
//                tareaId = tareaId,
//                onBack = { navController.popBackStack() },
//                viewModel = viewModel
//            )
        }

        composable("mostrar_profesores") {
            MostrarProfesoresScreen(
                onCrearProfesorClick = { navController.navigate("crear_profesor") },
                onProfesorClick = { profesorId ->
                    navController.navigate("detalle_profesor/$profesorId")
                },
                viewModel = profesorViewModel
            )
        }

        composable("crear_profesor") {
            CrearProfesorScreen(
                onBack = { navController.popBackStack() },
                viewModel = profesorViewModel
            )
        }

        composable(
            route = "detalle_profesor/{profesorId}",
            arguments = listOf(navArgument("profesorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val profesorId = backStackEntry.arguments?.getInt("profesorId") ?: 0
            CrearProfesorScreen(
                onBack = { navController.popBackStack() },
                viewModel = profesorViewModel,
                profesorId = profesorId
            )
        }

        composable("mostrar_materias") {
            MostrarMateriasScreen(
                onCrearMateriaClick = { navController.navigate("crear_materia") },
                onMateriaClick = { materiaId ->
                    navController.navigate("detalle_materia/$materiaId")
                },
                materiaViewModel = materiaViewModel,
                profesorViewModel = profesorViewModel
            )
        }

        composable("crear_materia") {
            CrearMateriaScreen(
                onBack = { navController.popBackStack() },
                materiaViewModel = materiaViewModel,
                profesorViewModel = profesorViewModel
            )
        }

        composable(
            route = "detalle_materia/{materiaId}",
            arguments = listOf(navArgument("materiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            CrearMateriaScreen(
                onBack = { navController.popBackStack() },
                materiaViewModel = materiaViewModel,
                profesorViewModel = profesorViewModel,
                idMateria = backStackEntry.arguments?.getInt("materiaId") ?: 0
            )
        }
    }
}