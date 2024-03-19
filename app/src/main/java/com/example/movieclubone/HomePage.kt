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
import com.example.movieclubone.dataClasses.PickedMovie
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
    val pickedMovies = remember { mutableStateOf<List<PickedMovie>>(emptyList()) }
    // Change the featuredMovie to hold a PickedMovie? instead of Movie?
    val featuredMovie = remember { mutableStateOf<PickedMovie?>(null) }
    val scope = rememberCoroutineScope()


    LaunchedEffect(key1 = "featuredMovie") {
        scope.launch {
            Log.d("HomePage", "Fetching featured movie")
            try {
                val featuredMovieSnapshot = Firebase.firestore.collection("systemData")
                    .document("featuredMovie")
                    .get()
                    .await()
                val movie = featuredMovieSnapshot.toObject<Movie>() // Get the Movie object
                // Assuming userName and userId are stored within the same document
                // If not, adjust the fetching logic accordingly
                val userName = featuredMovieSnapshot.getString("userName") ?: "Unknown"
                val userId = featuredMovieSnapshot.getString("userId") ?: ""
                val turnOrderEndDate = featuredMovieSnapshot.getLong("turnOrderEndDate") ?: 0L
                if (movie != null) {
                    featuredMovie.value = PickedMovie(movie, userId, userName, turnOrderEndDate)
                }
                Log.d("HomePage", "Featured movie fetched: ${featuredMovie.value?.movie?.title}")
            } catch (e: Exception) {
                Log.e("HomePage", "Error fetching featured movie", e)
            }
        }
    }


    LaunchedEffect(key1 = true) {
        scope.launch {
            Log.d("HomePage", "Fetching chosen movies")
            try {
                val fetchedPickedMovies = Firebase.firestore.collection("chosenMovies")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents.mapNotNull { document ->
                        document.toObject(Movie::class.java)?.let { movie ->
                            PickedMovie(
                                movie = movie,
                                userId = document.getString("userId") ?: "",
                                userName = document.getString("userName") ?: "",
                                turnOrderEndDate = document.getLong("turnOrderEndDate") ?: 0L
                            )
                        }
                    }
                if (fetchedPickedMovies.isNotEmpty()) {
                    Log.d("HomePage", "Chosen movies fetched successfully: ${fetchedPickedMovies.size} movies")
                } else {
                    Log.d("HomePage", "No chosen movies found")
                }
                pickedMovies.value = fetchedPickedMovies
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
                MainContentFeed(featuredMovie, pickedMovies.value) // Here featuredMovie is already State<Movie?>
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
fun MainContentFeed(featuredMovie: MutableState<PickedMovie?>, movies: List<PickedMovie>) {
    val currentDate = "March 20, 2024" // Example dynamic date

    LazyColumn {
        featuredMovie?.let {
            item {
                FeaturedMovieItem(it, currentDate)
            }
        }

        // Display the rest of the movies
        items(movies.drop(1)) { pickedMovie ->
            PreviouslyWatchedMovieItem(pickedMovie)
        }
    }
}
@Composable
fun FeaturedMovieItem(pickedMovieState: MutableState<PickedMovie?>, currentDate: String) {
    val pickedMovie = pickedMovieState.value // Extract the PickedMovie object from the state
    Box(modifier = Modifier.fillMaxWidth()) {
        // Background for attention drawing graphics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            // Custom layout for the first item
            Column(modifier = Modifier.padding(16.dp)) {
                // Assuming you have a 'MoviePoster' composable
                pickedMovie?.movie?.posterPath?.let { posterPath ->
                    MoviePoster(movie = pickedMovie.movie) // You might need to adjust MoviePoster if it expects a Movie object
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom graphics around the movie poster
                // Placeholder for popcorn graphic
                Text("🍿 Popcorn Graphic Here 🍿", textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))

                // Movie details
                Text(
                    text = pickedMovie?.movie?.title ?: "Unknown Movie",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Picked by: ${pickedMovie?.userName ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyMedium,
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
fun PreviouslyWatchedMovieItem(pickedMovie: PickedMovie) {
    // Display for each previously watched movie
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Previously Watched: ${pickedMovie.movie.title}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


