package com.example.movieclubone

import JoinClubCreateClub
import JoinClubID
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.ui.login.CreateAccount
import com.example.movieclubone.ui.login.CreateClub
import com.example.movieclubone.ui.login.SignIn
import com.example.movieclubone.ui.login.SignInCreateAccount


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "SignInCreateAccount") {
        composable("SignInCreateAccount") {
            SignInCreateAccount(navController)
        }
        composable("SignIn") {
            SignIn(navController)
        }
        composable("CreateClub") {
            CreateClub(navController)
        }
        composable("JoinClubID") {
            JoinClubID(navController)
        }
        composable("CreateAccount") {
            CreateAccount(navController)
        }
        composable("JoinClubCreateClub") {
            JoinClubCreateClub(navController)
        }
        composable("HomePage") {
            HomePage(navController)
        }
    }
}
