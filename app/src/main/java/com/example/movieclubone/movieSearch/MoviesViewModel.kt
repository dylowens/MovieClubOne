package com.example.movieclubone.movieSearch

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieclubone.dataClasses.Users
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MoviesViewModel(private val movieRepository: MovieRepository): ViewModel() {
    // LiveData to persist the ability of the user to choose a movie
    private val _canChooseMovie = MutableLiveData(true)
    val canChooseMovie: LiveData<Boolean> = _canChooseMovie
    fun setCanChooseMovie(canChoose: Boolean) {
        _canChooseMovie.value = canChoose
    }

    private val _moviesList = mutableStateOf<List<Movie>>(emptyList())
    val moviesList: State<List<Movie>> = _moviesList

    private val _movieDetailsState = mutableStateOf<MovieDetailsState>(MovieDetailsState.Loading)
    val movieDetailsState: State<MovieDetailsState> = _movieDetailsState

    private val _featuredMovie = mutableStateOf<Movie?>(null)
    val featuredMovie: State<Movie?> = _featuredMovie

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val movies = movieRepository.searchMovies(query)
                _moviesList.value = movies
            } catch (e: Exception) {
                Log.e("MoviesViewModel", "Error searching movies: ${e.message}")
            }
        }
    }

    fun addMovieToList(movie: Movie) {
        val currentList = _moviesList.value.toMutableList()
        currentList.add(movie)
        _moviesList.value = currentList
    }

    fun setFeaturedMovie(movie: Movie, user: Users) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Example: Storing the featured movie in Firestore
                val movieData = mapOf(
                    "title" to movie.title,
                    "posterPath" to movie.posterPath,
                    "userId" to user.uid,
                    "userName" to user.displayName,
                    "turnOrderEndDate" to System.currentTimeMillis() + 1209600000, // 2 weeks in ms
                    "timestamp" to System.currentTimeMillis()
                )
                Firebase.firestore.collection("systemData").document("featuredMovie")
                    .set(movieData).await()
                withContext(Dispatchers.Main) {
                    _featuredMovie.value = movie
                }
            } catch (e: Exception) {
                Log.e("MoviesViewModel", "Error setting featured movie: ${e.message}")
            }
        }
    }

    fun removeMovieFromList(movie: Movie) {
        val currentList = _moviesList.value.toMutableList()
        currentList.remove(movie)
        _moviesList.value = currentList
    }

    fun getMovieById(movieId: Int, completion: (Movie?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Example of getting a movie by ID from Firestore
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
                Log.e("MoviesViewModel", "Error fetching movie by ID: ${e.message}")
                withContext(Dispatchers.Main) {
                    completion(null)
                }
            }
        }
    }

    // Other functions as needed...
    fun getMovieFromListById(movieId: Int): Movie? {
        return _moviesList.value.firstOrNull { it.id == movieId }
    }

    fun addMovieToChosen(movie: Movie, user: Users) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val chosenMovieData = hashMapOf(
                    "title" to movie.title,
                    "posterPath" to movie.posterPath,
                    "userId" to user.uid,
                    "userName" to user.displayName,
                    "turnOrderEndDate" to System.currentTimeMillis() + 1209600000, // 2 weeks in ms
                    "timestamp" to System.currentTimeMillis() // For ordering when displaying previous movies
                )
                Firebase.firestore.collection("chosenMovies")
                    .add(chosenMovieData)
                    .await()
                // Set this movie as the featured movie after successfully adding it to chosenMovies
                setFeaturedMovie(movie, user) // Assuming this updates Firestore and _featuredMovie state
            } catch (e: Exception) {
                Log.e("ViewModel", "Error adding movie to chosenMovies", e)
            }
        }
    }


}

sealed class MovieDetailsState {
    object Loading : MovieDetailsState()
    data class Success(val movie: Movie) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}
