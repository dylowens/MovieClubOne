import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.movieSearch.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.movieclubone.TurnOrder
import com.example.movieclubone.dataClasses.Users
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay // Import for simulated delay


// Adjust the Composable function signature as needed
@Composable
fun ProfileSettings(
    context: Context,
    navController: NavHostController,
    signInHelper: FirebaseUISignIn,
    authViewModel: AuthViewModel,
    turnOrder: TurnOrder,
    moviesViewModel: MoviesViewModel,
) {
    var currentUserTurnOrder by remember { mutableStateOf<Int?>(null) }
    val movies by getUserMovies().collectAsState(initial = emptyList())

    // Fetch the current user's turn order
    LaunchedEffect(key1 = Unit) {
        Log.d("ProfileSettings", "Fetching turn order")

        turnOrder.fetchTurnOrder { _, userTurnOrder ->
            currentUserTurnOrder = userTurnOrder
            Log.d("ProfileSettings", "User turn order: $currentUserTurnOrder")
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sign Out Button
            Button(
                onClick = {
                    signInHelper.triggerSignOut()
                    navController.navigate("HomePage")
                    Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Sign Out")
            }

            // Check if user is current turn
            if (currentUserTurnOrder == 0 && movies?.isNotEmpty() == true) {
                Log.d("ProfileSettings", "User is current turn")
                Button(
                    onClick = {
                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                        firebaseUser?.let { user ->
                            val customUserDetails = Users( // Assuming you have a Users data class
                                uid = user.uid,
                                displayName = user.displayName ?: "",
                                photoUrl = user.photoUrl.toString(),
                                // include other fields your Users class might require
                            )
                            movies?.first()?.let { movie ->
                                moviesViewModel.addMovieToChosen(movie, customUserDetails) // Adjust according to actual method parameters
                                Toast.makeText(context, "Movie set for group watching", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("+Choose Movie")
                }
            }

            // Check if movies are null (loading)
            if (movies == null) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                // Movies list displayed after the "Sign Out" button
                LazyColumn {
                    items(movies ?: emptyList()) { movie ->
                        MovieItem(movie, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, navController: NavController) {
    val route = "MovieDetails/${movie.id}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                    // Navigate to MovieDetails screen with the movie ID as an argument
                    navController.navigate(route)
                       },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Movie Poster
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = "https://image.tmdb.org/t/p/w500/${movie.posterPath}").apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                    }).build()
            ),
            contentDescription = "Movie Poster",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        // Movie Title
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}



// This function now emits loading state as well
fun getUserMovies(): Flow<MutableList<Movie>?> = flow {
    emit(null) // Emit null initially to represent loading state
    val user = Firebase.auth.currentUser
    user?.let { firebaseUser ->
        // Simulate a network delay
        delay(1000) // Remove this line in your actual app
        val userDocRef = Firebase.firestore.collection("users").document(firebaseUser.uid)
        val snapshot = userDocRef.collection("movies").get().await()
        val movies = snapshot.toObjects(Movie::class.java)
        emit(movies)
    }
}.catch { e ->
    Log.e("GetMovies", "Error getting user movies", e)
    emit(emptyList<Movie>())
}.flowOn(Dispatchers.IO)


fun removeMovieFromUserProfile(movie: Movie) {
    val user = Firebase.auth.currentUser
    user?.let { firebaseUser ->
        // Directly use Firebase Firestore reference
        val userDocRef = Firebase.firestore.collection("users").document(firebaseUser.uid)
        // Remove the movie document from the subcollection
        userDocRef.collection("movies").document(movie.id.toString()).delete()
            .addOnSuccessListener { Log.d("RemoveMovie", "Movie successfully removed from profile") }
            .addOnFailureListener { e -> Log.w("RemoveMovie", "Error removing movie from profile", e) }
    }
}

fun addMovieToUserProfile(movie: Movie) {
    val user = Firebase.auth.currentUser
    user?.let { firebaseUser ->
        // Directly use Firebase Firestore reference
        val userDocRef = Firebase.firestore.collection("users").document(firebaseUser.uid)
        // Add movie to a subcollection under the user's document
        userDocRef.collection("movies").document(movie.id.toString()).set(movie)
            .addOnSuccessListener { Log.d("AddMovie", "Movie successfully added to profile") }
            .addOnFailureListener { e -> Log.w("AddMovie", "Error adding movie to profile", e) }
    }
}



