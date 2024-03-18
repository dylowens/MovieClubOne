package com.example.movieclubone.movieSearch

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MoviesViewModel(private val movieRepository: MovieRepository): ViewModel() {

    private val _moviesList = mutableStateOf<List<Movie>>(emptyList())
    val moviesList: State<List<Movie>> = _moviesList

    // Example of a UI state for movie details
    private val _movieDetailsState = mutableStateOf<MovieDetailsState>(MovieDetailsState.Loading)
    val movieDetailsState: State<MovieDetailsState> = _movieDetailsState

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

    fun getMovieFromListById(movieId: Int): Movie? {
        return _moviesList.value.firstOrNull { it.id == movieId }
    }

    // Implementing getMovieById from Database
    fun getMovieById(movieId: Int, completion: (Movie?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = Firebase.auth.currentUser
                user?.let { firebaseUser ->
                    val movieDocRef = Firebase.firestore.collection("users")
                        .document(firebaseUser.uid)
                        .collection("movies")
                        .document(movieId.toString())
                    val snapshot = movieDocRef.get().await()
                    val movie = snapshot.toObject<Movie>()
                    withContext(Dispatchers.Main) {
                        completion(movie)
                    }
                }
            } catch (e: Exception) {
                Log.e("MoviesViewModel", "Error fetching movie by ID", e)
                withContext(Dispatchers.Main) {
                    completion(null)
                }
            }
        }
    }

}

// Define UI states for movie details
sealed class MovieDetailsState {
    object Loading : MovieDetailsState()
    data class Success(val movie: Movie) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}


