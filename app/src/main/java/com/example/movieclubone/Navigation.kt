package com.example.movieclubone

import FirebaseUISignIn
import JoinClubCreateClub
import JoinClubID
import ProfileSettings
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.movieSearch.MovieDetails
import com.example.movieclubone.movieSearch.MovieDetailsAPI
import com.example.movieclubone.movieSearch.MovieSearchScreen
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.login.CreateClub
import com.example.movieclubone.ui.login.SignIn

    @Composable
    fun Navigation(
        context: Context,
        navController: NavHostController,
        signInHelper: FirebaseUISignIn,
        authViewModel: AuthViewModel,
        moviesViewModel: MoviesViewModel,
        movie: Movie
    ) {

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
                backStackEntry.arguments?.getString("movieId")?.toIntOrNull()?.let { movieId ->
                    MovieDetails(
                        navController = navController,
                        movieId = movieId,
                        moviesViewModel = moviesViewModel
                    )
                }
            }
            // Destination for MovieDetailsAPI
            composable(
                route = "MovieDetailsAPI/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId")
                movieId?.let {
                    // If you're fetching the movie details again based on ID
//                MovieDetailsAPI(movieId = it, navController = navController, moviesViewModel = moviesViewModel)

                    // OR, if using a shared ViewModel to hold the selected movie
                    moviesViewModel.getMovieFromListById(movieId)
                        ?.let { it1 -> MovieDetailsAPI(context, it1, navController) }
                }
            }

        }
    }
