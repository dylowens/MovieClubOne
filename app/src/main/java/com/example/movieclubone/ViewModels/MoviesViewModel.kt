package com.example.movieclubone.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieclubone.Data.ChosenMovie
import com.example.movieclubone.Data.Users
import com.example.movieclubone.Feature.movieSearch.Movie
import com.example.movieclubone.Feature.movieSearch.MovieRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoviesViewModel(private val movieRepository: MovieRepository): ViewModel() {
    // LiveData to persist the ability of the user to choose a movie
    private val _canChooseMovie = MutableLiveData(true)
    val canChooseMovie: LiveData<Boolean> = _canChooseMovie

    private val _chosenMovies = MutableStateFlow<List<ChosenMovie>>(emptyList())
    val chosenMovies: StateFlow<List<ChosenMovie>> = _chosenMovies
    fun setCanChooseMovie(canChoose: Boolean) {
        _canChooseMovie.value = canChoose
    }

    init {
        fetchChosenMovies()
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

        fun convertMillisToFormattedDate(millis: Long): String {
            val date = Date(millis)
            val formatter = SimpleDateFormat("MMMM, d", Locale.getDefault())
            val dayFormat = SimpleDateFormat("d", Locale.getDefault())
            val day = dayFormat.format(date).toInt()

            val suffix = when (day) {
                1, 21, 31 -> "st"
                2, 22 -> "nd"
                3, 23 -> "rd"
                else -> "th"
            }

            return formatter.format(date) + suffix
        }


        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentTimeMillis = System.currentTimeMillis()
                val turnOrderEndDateMillis = currentTimeMillis + 1209600000 // 2 weeks in ms
                val turnOrderEndDateFormatted = convertMillisToFormattedDate(turnOrderEndDateMillis)

                val movieData = mapOf(
                    "title" to movie.title,
                    "posterPath" to movie.posterPath,
                    "userId" to user.uid,
                    "userName" to user.displayName,
                    "turnOrderEndDate" to turnOrderEndDateMillis,
                    "turnOrderEndDateFormatted" to turnOrderEndDateFormatted, // For displaying purposes
                    "timestamp" to currentTimeMillis
                )

                Firebase.firestore.collection("systemData").document("featuredMovie")
                    .set(movieData).await()
                withContext(Dispatchers.Main) {
                    _featuredMovie.value = movie
                }

                // Add the movie to chosenMovies
                val chosenMovieDocumentName = "${movie.title}(Chosen)"
                Firebase.firestore.collection("chosenMovies").document(chosenMovieDocumentName)
                    .set(movieData).await()

                withContext(Dispatchers.Main) {
                    _featuredMovie.value = movie
                }
                Log.d("DateConversion", "Turn order end date: $turnOrderEndDateFormatted")

            } catch (e: Exception) {
                Log.e("setFeaturedMovie", "Error setting featured movie", e)
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

    fun getMovieFromListById(movieId: Int): Movie? {
        return _moviesList.value.firstOrNull { it.id == movieId }
    }
    init {
        fetchChosenMovies()
    }

    fun fetchChosenMovies() {
        viewModelScope.launch {
            val db = Firebase.firestore
            db.collection("chosenMovies")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { documents ->
                    val moviesList = documents.toObjects(ChosenMovie::class.java)
                    _chosenMovies.value = moviesList
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                }
        }
    }
}



sealed class MovieDetailsState {
    object Loading : MovieDetailsState()
    data class Success(val movie: Movie) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}
