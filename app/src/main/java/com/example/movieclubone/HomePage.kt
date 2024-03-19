package com.example.movieclubone

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.dataClasses.ChosenMovie
import com.example.movieclubone.dataClasses.Users
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.movieSearch.MoviePoster
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomePage(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    turnOrder: TurnOrder,
    moviesViewModel: MoviesViewModel
) {
    val chosenMovies = remember { mutableStateOf<List<ChosenMovie>>(emptyList()) }
    val featuredMovie = remember { mutableStateOf<Movie?>(null) }
    val scope = rememberCoroutineScope()

// Fetching the featured movie from Firestore
    LaunchedEffect(key1 = "featuredMovie") {
        scope.launch {
            Log.d("HomePage", "Fetching featured movie")
            try {
                val featuredMovieSnapshot = Firebase.firestore.collection("systemData")
                    .document("featuredMovie")
                    .get()
                    .await()
                featuredMovie.value = featuredMovieSnapshot.toObject<Movie>()
                Log.d("HomePage", "Featured movie fetched: ${featuredMovie.value?.title}")
            } catch (e: Exception) {
                Log.e("HomePage", "Error fetching featured movie", e)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        scope.launch {
            Log.d("HomePage", "Fetching chosen movies")
            try {
                val fetchedChosenMovies = Firebase.firestore.collection("chosenMovies")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents.mapNotNull { document ->
                        document.toObject(Movie::class.java)?.let { movie ->
                            ChosenMovie(
                                movie = movie,
                                userId = document.getString("userId") ?: "",
                                userName = document.getString("userName") ?: "",
                                turnOrderEndDate = document.getLong("turnOrderEndDate") ?: 0L
                            )
                        }
                    }
                if (fetchedChosenMovies.isNotEmpty()) {
                    Log.d("HomePage", "Chosen movies fetched successfully: ${fetchedChosenMovies.size} movies")
                } else {
                    Log.d("HomePage", "No chosen movies found")
                }
                chosenMovies.value = fetchedChosenMovies
            } catch (e: Exception) {
                Log.e("HomePage", "Error fetching chosen movies", e)
            }
        }
    }



    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TopIconContainer(turnOrder)
            if (featuredMovie.value != null) {
                MainContentFeed(featuredMovie, chosenMovies.value) // Here featuredMovie is already State<Movie?>
            }
        }
    }
}

@Composable
fun TopIconContainer(turnOrder: TurnOrder) {
    val users = remember { mutableStateOf(emptyList<Users>()) }

    // Fetch turn order and users' data
    LaunchedEffect(key1 = true) {
        turnOrder.fetchTurnOrder { fetchedUsers, _ ->
            users.value = fetchedUsers
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        users.value.forEachIndexed { index, user ->
            user.photoUrl?.let { photoUrl ->
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    // Check if the user is the first one in the turn order list
                    val imageModifier = if (index == 0) {
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(5.dp, Color.Green, CircleShape) // Apply a green border if it's the first user
                    } else {
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    }
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "Profile",
                        modifier = imageModifier
                    )
                    // Assuming the displayName might contain the first name or full name
                    val firstName = user.displayName?.split(" ")?.firstOrNull() ?: ""
                    Text(text = firstName, textAlign = TextAlign.Center)
                }
            }
        }
    }
}


@Composable
fun MainContentFeed(featuredMovieState: State<Movie?>, movies: List<ChosenMovie>) {
    val currentDate = "March 20, 2024" // Example dynamic date
    val featuredMovie = featuredMovieState.value

    LazyColumn {
        featuredMovie?.let { movie ->
            item {
                FeaturedMovieItem(movie, currentDate)
            }
        }

        // Display the rest of the movies
        items(movies.drop(1)) { chosenMovie ->
            PreviouslyWatchedMovieItem(chosenMovie)
        }
    }
}

@Composable
fun FeaturedMovieItem(movie: Movie, currentDate: String){
    Box(modifier = Modifier.fillMaxWidth()) {
        // Background for attention drawing graphics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) // Themed background
        ) {
            // Custom layout for the first item
            Column(modifier = Modifier.padding(16.dp)) {
                // Assuming you have a 'MoviePoster' composable

                movie.posterPath?.let {
                    MoviePoster(movie = movie)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom graphics around the movie poster
                // Placeholder for popcorn graphic
                Text("🍿 Popcorn Graphic Here 🍿", textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))

                // Movie details
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Movie tape graphic at the bottom
                Text("🎞️ Movie Tape Graphic Here 🎞️", textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(8.dp))

                // "Currently Watching" message
                Text(
                    text = "Currently Watching. You have until: $currentDate",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PreviouslyWatchedMovieItem(chosenMovie: ChosenMovie) {
    // Display for each previously watched movie
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Previously Watched: ${chosenMovie.movie.title}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


