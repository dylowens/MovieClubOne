package com.example.movieclubone

import FirebaseUISignIn
import JoinClubCreateClub
import JoinClubID
import PreviouslyChosenPage
import ProfileSettings
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movieclubone.ui.Pages.AdminPage
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.ui.Pages.MovieDetails
import com.example.movieclubone.ui.Pages.MovieDetailsAPI
import com.example.movieclubone.ui.Pages.MovieSearchScreen
import com.example.movieclubone.ViewModels.MoviesViewModel
import com.example.movieclubone.ui.Pages.HomePage
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.login.CreateClub
import com.example.movieclubone.ui.Pages.SignIn

    @Composable
    fun Navigation(
        context: Context,
        navController: NavHostController,
        signInHelper: FirebaseUISignIn,
        authViewModel: AuthViewModel,
        moviesViewModel: MoviesViewModel,
        movie: Movie,
        turnOrder: TurnOrder,
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
                HomePage(navController, authViewModel, turnOrder, moviesViewModel)
            }
            composable("ProfileSettings") {
                ProfileSettings(context, navController, signInHelper, authViewModel, turnOrder, moviesViewModel)
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
            composable("AdminPage") {
                AdminPage(navController)
            }
            composable("PreviouslyChosenPage") {
                PreviouslyChosenPage(navController, moviesViewModel)
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
