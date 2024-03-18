package com.example.movieclubone.movieSearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.ui.login.AuthViewModel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.LineHeightStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetails(movie: Movie, navController: NavController) {
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

                // "+Add" Button
                Button(
                    onClick = { /* Handle the add action here */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("+Add")
                }

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
