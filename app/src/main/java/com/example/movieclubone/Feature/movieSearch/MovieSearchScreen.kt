package com.example.movieclubone.Feature.movieSearch

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.ViewModels.MoviesViewModel
import com.example.movieclubone.Common.BottomNavigationBar
import com.example.movieclubone.Feature.SignIn.AuthViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchScreen(navController: NavController, viewModel: MoviesViewModel, authViewModel: AuthViewModel) {
    val searchBarState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val movies = viewModel.moviesList.value // Assume this collects movies from your ViewModel

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Page title
            Text(
                text = "Movie Search",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.Black
            )
            SearchBar(
                query = searchBarState.value,
                onQueryChanged = { searchBarState.value = it },
                onSearch = {
                    viewModel.searchMovies(searchBarState.value)
                    keyboardController?.hide()
                }
            )
            MoviesList(movies = movies, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit, onSearch: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        singleLine = true,
        placeholder = { Text("Search Movies") },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            keyboardController?.hide() // Ensure the keyboard is hidden
        }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.small, // Rounded corners for the text field
        colors = TextFieldDefaults.textFieldColors() // Customize colors to fit your theme
    )
}

@Composable
fun MoviesList(movies: List<Movie>, navController: NavController) {
    LazyColumn(contentPadding = PaddingValues(all = 16.dp)) {
        items(movies) { movie ->
            MovieRow(movie = movie, navController)
        }
    }
}

@Composable
fun MovieRow(movie: Movie, navController: NavController) {
    val posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }
    val route = "MovieDetailsAPI/${movie.id}"

    Card(
        modifier = Modifier
            .clickable { navController.navigate(route) }
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            posterUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3, // Limit overview text to avoid clutter
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
