package com.example.movieclubone.movieSearch

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.ui.login.AuthViewModel

@Composable
fun MovieSearchScreen(navController: NavHostController, viewModel: MoviesViewModel, authViewModel: AuthViewModel) {
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
            MoviesList(movies = movies)
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
fun MoviesList(movies: List<Movie>) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(movies) { movie ->
            MovieRow(movie = movie)
        }
    }
}

@Composable
fun MovieRow(movie: Movie) {

    Surface(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        color = Color.LightGray,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = movie.overview, style = MaterialTheme.typography.bodySmall)
        }
    }
}
