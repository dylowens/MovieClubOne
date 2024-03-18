package com.example.movieclubone.movieSearch

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch



class MoviesViewModel(private val movieRepository: MovieRepository): ViewModel() {

    private val _moviesList = mutableStateOf<List<Movie>>(emptyList())
    val moviesList: State<List<Movie>> = _moviesList

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val movies = movieRepository.searchMovies(query)
                _moviesList.value = movies
            } catch (e: Exception) {
                // Handle errors, possibly updating another state to show an error message.
            }
        }
    }

    // Implementing getMovieById
    fun getMovieById(movieId: Int): Movie? {
        // Return the first movie that matches the ID, or null if no match is found.
        return _moviesList.value.firstOrNull { movie -> movie.id == movieId }
    }
}
