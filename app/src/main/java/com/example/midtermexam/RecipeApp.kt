package com.example.midtermexam

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun RecipeApp() {
    val navController = rememberNavController()

    val viewModel: DishViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dish_list"
    ) {

        composable(route = "dish_list") {
            DishListScreen(
                viewModel = viewModel,
                onDishClick = { dishId ->
                    // TODO 6 (15 pts): navigate to detail screen
                    navController.navigate("dish_detail/$dishId")
                }
            )
        }


        composable(
            route = "dish_detail/{dishId}",
            arguments = listOf(navArgument("dishId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dishId = backStackEntry.arguments?.getInt("dishId") ?: 0
            DishDetailScreen(
                dishId = dishId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}