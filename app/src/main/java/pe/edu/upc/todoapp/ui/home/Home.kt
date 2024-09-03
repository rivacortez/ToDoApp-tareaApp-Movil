package pe.edu.upc.todoapp.ui.home

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.edu.upc.todoapp.ui.taskdetail.TaskDetail
import pe.edu.upc.todoapp.ui.tasklist.TaskList

@SuppressLint("MutableCollectionMutableState")
@Composable
fun Home() {
    val navController = rememberNavController()

    val tasks = remember {
        mutableStateOf(mutableListOf<String>())
    }

    NavHost(navController = navController, startDestination = Routes.TaskList.route) {
        composable(route = Routes.TaskList.route) {
            TaskList(
                tasks = tasks.value,
                onSelectTask = { index ->
                    navController.navigate("${Routes.TaskDetail.route}/$index")
                },
                onAddTask = {
                    navController.navigate(Routes.TaskDetail.routeWithoutArgument)
                },
                onDeleteTask = { index ->

                    tasks.value = tasks.value.toMutableList().apply {
                        removeAt(index)
                    }
                }
            )
        }
        composable(
            route = Routes.TaskDetail.routeWithArgument,
            arguments = listOf(navArgument(Routes.TaskDetail.argument) { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt(Routes.TaskDetail.argument) ?: return@composable

            val selectedTask = if (index == -1) null else tasks.value[index]

            TaskDetail(task = selectedTask) { newTask ->
                if (selectedTask == null) {
                    tasks.value += newTask
                } else {
                    tasks.value[index] = newTask
                }

                navController.popBackStack()
            }
        }
    }
}

sealed class Routes(val route: String) {
    data object TaskList : Routes(route = "TaskList")
    data object TaskDetail : Routes(route = "TaskDetail") {
        const val routeWithArgument = "TaskDetail/{index}"
        const val argument = "index"
        const val routeWithoutArgument = "TaskDetail/-1"
    }
}



@Preview
@Composable
fun HomePreview() {
    Home()
}