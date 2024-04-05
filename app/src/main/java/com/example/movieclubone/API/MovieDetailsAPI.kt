package com.example.movieclubone.API

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.Common.BottomNavigationBar
import com.example.movieclubone.Feature.SignIn.AuthViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.movieclubone.Feature.movieSearch.Movie
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsAPI(context: Context, movie: Movie, navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, AuthViewModel()) }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Box(modifier = Modifier.padding(innerPadding)) {
            // Your existing MovieDetails content
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Movie Poster
                movie.posterPath?.let {
                    Image(
                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w342/$it"),
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Movie Title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Movie Description
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                MovieActionButton(movie = movie, navController = navController , context = context)
                Spacer(modifier = Modifier.height(8.dp))

                // Rest of the Movie Info
                movieDetailItem("Original Language", movie.originalLanguage)
                movieDetailItem("Original Title", movie.originalTitle)
                movieDetailItem("Popularity", movie.popularity.toString())
                movie.releaseDate?.let { movieDetailItem("Release Date", it) }
                movieDetailItem("Video", movie.video.toString())
                movieDetailItem("Vote Average", movie.voteAverage.toString())
                movieDetailItem("Vote Count", movie.voteCount.toString())
            }
//            // Floating Back Button on the top left
//            //Cannot seem to get the back button to show up
//            IconButton(
//                onClick = { navController.navigateUp() },
//                modifier = Modifier
//                    .offset(x = 16.dp, y = 16.dp) // Adjust positioning as needed
//            ) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//            }

        }
    }
}

@Composable
fun movieDetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}


@Composable
fun MovieActionButton(movie: Movie, navController: NavController, context: Context) {
    val firestore = Firebase.firestore
    val user = Firebase.auth.currentUser
    val moviesCollection = user?.let { firestore.collection("users").document(it.uid).collection("movies") }

    var isMovieInProfile by remember { mutableStateOf<Boolean?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Check if movie is in profile on composition and after adding/removing
    LaunchedEffect(movie.id, isMovieInProfile) {
        moviesCollection?.document(movie.id.toString())?.get()?.addOnSuccessListener { document ->
            isMovieInProfile = document.exists()
        }
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isMovieInProfile == true) {
            // - Remove Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        moviesCollection?.document(movie.id.toString())?.delete().also {
                            isMovieInProfile = false
                            Toast.makeText(context, "Removed ${movie.title} from profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.Center) // This Modifier is redundant inside Box with contentAlignment
            ) {
                Text("- Remove")
            }
        } else if (isMovieInProfile == false || isMovieInProfile == null) {
            // + Add Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        moviesCollection?.document(movie.id.toString())?.set(movie)
                            ?.addOnSuccessListener {
                                isMovieInProfile = true
                                Toast.makeText(context, "Added ${movie.title} to profile", Toast.LENGTH_SHORT).show()
                            }
                            ?.addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to add ${movie.title} to profile: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    }
                },
                modifier = Modifier.align(Alignment.Center) // This Modifier is redundant inside Box with contentAlignment
            ) {
                Text("+ Add")
            }
        }
    }
}
