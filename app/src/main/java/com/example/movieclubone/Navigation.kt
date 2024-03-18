package com.example.movieclubone

import FirebaseUISignIn
import JoinClubCreateClub
import JoinClubID
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.bottomappbar.ProfileSettings
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.login.CreateAccount
import com.example.movieclubone.ui.login.CreateClub
import com.example.movieclubone.ui.login.SignIn
import com.example.movieclubone.ui.login.SignInCreateAccount


@Composable
fun Navigation(context: Context, navController: NavHostController, signInHelper: FirebaseUISignIn, authViewModel: AuthViewModel){

    NavHost(navController = navController, startDestination = "SignIn") {
        composable("SignIn") {
            SignIn(navController, signInHelper)
        }
        composable("CreateClub") {
            CreateClub(navController)
        }
        composable("JoinClubID") {
            JoinClubID(navController)
        }
        composable("JoinClubCreateClub") {
            JoinClubCreateClub(navController)
        }
        composable("HomePage") {
            HomePage(navController, authViewModel)
        }
        composable("ProfileSettings") {
            ProfileSettings(context, navController, signInHelper)
        }
    }
}
