package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GreetingAppNavHost()
                }
            }
        }
    }
}

@Composable
fun GreetingAppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(onShowGreeting = { typedName ->
                navController.navigate(Greeting(userName = typedName))
            })
        }

        composable<Greeting> { backStackEntry ->
            val greeting: Greeting = backStackEntry.toRoute()
            GreetingScreen(
                userName = greeting.userName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}