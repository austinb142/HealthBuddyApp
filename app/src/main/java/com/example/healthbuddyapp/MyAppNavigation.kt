package com.example.healthbuddyapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.healthbuddyapp.pages.ActivityPage
import com.example.healthbuddyapp.pages.DietPage
import com.example.healthbuddyapp.pages.HomePage
import com.example.healthbuddyapp.pages.LoginPage
import com.example.healthbuddyapp.pages.SignUpPage
import com.example.healthbuddyapp.pages.SleepPage


@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("activity") {
            ActivityPage(modifier, navController, authViewModel)
        }
        composable("diet") {
            DietPage(modifier, navController, authViewModel)
        }
        composable("sleep") {
            SleepPage(modifier, navController, authViewModel)
        }
    })
}