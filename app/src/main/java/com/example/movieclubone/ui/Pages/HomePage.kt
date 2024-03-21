package com.example.movieclubone.ui.Pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.TurnOrder
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.dataClasses.ChosenMovie
import com.example.movieclubone.dataClasses.Users
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.ViewModels.MoviesViewModel
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
    // Change the featuredMovie to hold a ChosenMovie? instead of Movie?
    val featuredMovie = remember { mutableStateOf<ChosenMovie?>(null) }
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
                val userName = featuredMovieSnapshot.getString("userName") ?: "Unknown"
                val userId = featuredMovieSnapshot.getString("userId") ?: ""
                val turnOrderEndDate = featuredMovieSnapshot.getLong("turnOrderEndDate") ?: 0L
                val turnOrderEndDateFormatted = featuredMovieSnapshot.getString("turnOrderEndDateFormatted") ?: ""

                if (movie != null) {
                    // Ensure posterPath and title fields are directly populated from the movie object
                    featuredMovie.value = movie.posterPath?.let {
                        ChosenMovie(
                            movie = movie,
                            userId = userId,
                            userName = userName,
                            turnOrderEndDate = turnOrderEndDate,
                            turnOrderEndDateFormatted = turnOrderEndDateFormatted,
                            posterPath = it, // Populate this from the movie object
                            title = movie.title // Populate this from the movie object
                        )
                    }
                }
                Log.d("HomePage", "Featured movie fetched: ${featuredMovie.value?.title}")
            } catch (e: Exception) {
                Log.e("HomePage", "Error fetching featured movie", e)
            }
        }
    }



    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TopIconContainer(turnOrder)
            if (featuredMovie.value != null) {
                MainContentFeed(featuredMovie, navController) // Here featuredMovie is already State<Movie?>
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
fun MainContentFeed(featuredMovie: MutableState<ChosenMovie?>, navController: NavController) {
    Column {
        featuredMovie.value?.let { chosenMovie ->
            FeaturedMovieItem(chosenMovie = chosenMovie)
        }

        Button(onClick = { navController.navigate("PreviouslyChosenPage") },
            modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button
        ) {
            Text("Previously Shown Movies")
        }
    }
}

@Composable
fun FeaturedMovieItem(chosenMovie: ChosenMovie) {
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
                // Assuming you have a 'MoviePoster' composable or similar logic for displaying the poster
                Image(
                    painter = rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/w500${chosenMovie.posterPath}"),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally) // Center the image

                )

                Spacer(modifier = Modifier.height(16.dp))

                // Movie details
                Text(
                    text = chosenMovie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Picked by: ${chosenMovie.userName}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Log.d("HomePage", "turnOrderEndDateFormatted: ${chosenMovie.turnOrderEndDateFormatted}")

                // "Currently Watching" message
                Text(
                    text = "Currently Watching. You have until: ${chosenMovie.turnOrderEndDateFormatted}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



