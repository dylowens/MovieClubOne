package com.example.movieclubone

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.movieSearch.MoviePoster
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun HomePage(navController: NavHostController, authViewModel: AuthViewModel, movie: Movie) {

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TopIconContainer()
            MainContentFeed(movie)
        }
    }
}

@Composable
fun TopIconContainer() {
    val users = remember { mutableStateOf(listOf<Map<String, Any>>()) }

    LaunchedEffect(key1 = true) {
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { documents ->
                users.value = documents.map { it.data }
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        users.value.forEach { userData ->
            userData["photoUrl"]?.let { photoUrl ->
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "Profile",
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                        // Adjust size as needed
                    )
                    val fullName = userData["displayName"].toString()
                    val firstName = fullName.split(" ").firstOrNull() ?: ""
                    Text(text = firstName)
                }
            }
        }
    }
}
@Composable
fun MainContentFeed(movie: Movie) {
    val items = List(20) { "Item $it" }
    val currentDate = "March 20, 2024" // Replace with your dynamic date

    LazyColumn {
        // Highlight the first item
        if (items.isNotEmpty()) {
            item {
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
                                text = items.first(),
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
        }

        // The rest of the items as previously watched
        items(items.drop(1)) { item ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Previously Watched: $item",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
