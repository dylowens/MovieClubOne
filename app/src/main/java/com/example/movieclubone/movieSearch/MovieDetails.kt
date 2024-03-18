package com.example.movieclubone.movieSearch

import addMovieToUserProfile
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.ui.login.AuthViewModel
import removeMovieFromUserProfile


@Composable
fun MovieDetails(navController: NavController, movieId: Int, moviesViewModel: MoviesViewModel) {
    val context = LocalContext.current
    val movieState = remember { mutableStateOf<Movie?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    // Adjusted the method call here
    LaunchedEffect(movieId) {
        isLoading.value = true
        moviesViewModel.getMovieById(movieId) { movie ->
            movieState.value = movie
            isLoading.value = false
        }
    }

    Scaffold(bottomBar = { BottomNavigationBar(navController, AuthViewModel()) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (movieState.value != null) {
                MovieDetailsContent(navController = navController, movie = movieState.value!!, context = context)
            } else {
                Text("Movie not found", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MovieDetailsContent(navController: NavController, movie: Movie, context: Context) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        MoviePoster(movie = movie)
        MovieTitle(movie = movie)
        MovieDescription(movie = movie)
        AddAndRemoveButtons(navController = navController, movie, context = context)
        MovieDetailsInfo(movie = movie)
    }
}

@Composable
fun MoviePoster(movie: Movie) {
    Image(
        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w342/${movie.posterPath}"),
        contentDescription = "Movie Poster",
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentScale = ContentScale.FillWidth
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun MovieTitle(movie: Movie) {
    Text(
        text = movie.title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun MovieDescription(movie: Movie) {
    Text(
        text = movie.overview,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun AddAndRemoveButtons(navController: NavController, movie: Movie, context: Context) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(
            onClick = {
                removeMovieFromUserProfile(movie)
                navController.popBackStack()
                Toast.makeText(context, "Removed ${movie.title} from profile", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("- Remove")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun MovieDetailsInfo(movie: Movie) {
    // Implement this composable to display more detailed information about the movie
}
