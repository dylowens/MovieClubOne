package com.example.movieclubone.movieSearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.ui.login.AuthViewModel

@Composable
fun MovieSearchScreen(navController: NavController, viewModel: MoviesViewModel, authViewModel: AuthViewModel) {
    val searchBarState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val movies = viewModel.moviesList.value // Assume this LiveData or StateFlow that collects movies from your ViewModel

    Scaffold(
        topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SearchBar(
                        query = searchBarState.value,
                        onQueryChanged = { searchBarState.value = it },
                        onSearch = {
                            viewModel.searchMovies(searchBarState.value)
                            keyboardController?.hide()
                        }
                    )
                }
        },
        bottomBar = { BottomNavigationBar(navController, authViewModel) }


    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MoviesList(movies = movies, navController)
        }

    }

}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit, onSearch: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        singleLine = true,
        label = { Text("Search Movies") },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            keyboardController?.hide() // Ensure the keyboard is hidden
        }),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MoviesList(movies: List<Movie>, navController: NavController) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(movies) { movie ->
            MovieRow(movie = movie, navController)
        }
    }
}


@Composable
fun MovieRow(movie: Movie, navController: NavController) {
    // Example of choosing a different size. For dynamic sizing, you might adjust based on screen size or other factors.
    val posterSize = "w342" // Choose based on what TMDB supports and your app needs. "w342" is generally a good middle-ground.
    val imageUrlBase = "https://image.tmdb.org/t/p/$posterSize"
    val posterUrl = movie.posterPath?.let { imageUrlBase + it }

    val route = "MovieDetails/${movie.id}"

    Surface(
        modifier = Modifier
            .clickable { navController.navigate(route) } // Add navigation action on click
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        color = Color.LightGray,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display movie poster if available

            posterUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .height(200.dp) // You might adjust this height based on the size of the image or your UI needs.
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillHeight

                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = movie.overview, style = MaterialTheme.typography.bodySmall)
        }
    }
}

