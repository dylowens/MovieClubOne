package com.example.movieclubone

import FirebaseUISignIn
import JoinClubCreateClub
import JoinClubID
import ProfileSettings
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.movieSearch.MovieDetails
import com.example.movieclubone.movieSearch.MovieSearchScreen
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.login.CreateClub
import com.example.movieclubone.ui.login.SignIn



@Composable
fun Navigation(context: Context,
               navController: NavHostController,
               signInHelper: FirebaseUISignIn,
               authViewModel: AuthViewModel,
               moviesViewModel: MoviesViewModel,
               movie: Movie){

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
            ProfileSettings(context, navController, signInHelper, authViewModel)
        }
        composable("MovieSearchScreen") {
            MovieSearchScreen(navController, moviesViewModel, authViewModel)
        }
        composable("MovieDetails/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: return@composable
            val movie = moviesViewModel.getMovieById(movieId)

            // Check if movie is not null before proceeding
            movie?.let {
                MovieDetails(it, navController)
            } ?: run {
                // Handle null case, e.g., showing an error, or navigate back
                navController.popBackStack()
            }
        }
    }
}
