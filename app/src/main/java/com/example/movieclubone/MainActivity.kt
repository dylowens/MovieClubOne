package com.example.movieclubone

import FirebaseUISignIn
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.movieSearch.MovieApiService
import com.example.movieclubone.movieSearch.MovieRepository
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.theme.MovieClubOneTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var signInHelper: FirebaseUISignIn

    // Firebase SignInLauncher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        signInHelper.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieClubOneTheme {
                MainContent(this)

            }
        }

    }

    @Composable
    fun MainContent(context: Context) {
        val navController = rememberNavController()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val movieApiService = retrofit.create(MovieApiService::class.java)
        val movieRepository = MovieRepository(context, movieApiService)
        val moviesViewModel = MoviesViewModel(movieRepository)

        signInHelper = FirebaseUISignIn(this, signInLauncher).apply {
            setSignInResultListener(object : FirebaseUISignIn.SignInResultListener {
                override fun onSignInSuccess() {

//                    // Implementation
                    // Navigate to HomePage on successful sign-in
                    navController.navigate("HomePage") {
                        // Clear back stack to prevent back navigation to the sign-in screen
                        popUpTo("SignIn") { inclusive = true }
                    }
                }
                override fun onSignInFailed(errorCode: Int?) {
                    // Implementation
                }
            })
        }

        Scaffold(
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Navigation(
                    context = context,
                    navController = navController,
                    signInHelper = signInHelper,
                    authViewModel = AuthViewModel(),
                    moviesViewModel = moviesViewModel,
                    movie = Movie()
                )
            }
        }
    }
}