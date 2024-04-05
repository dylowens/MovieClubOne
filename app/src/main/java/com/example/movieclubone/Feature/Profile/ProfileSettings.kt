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
import com.example.movieclubone.Common.BottomNavigationBar
import com.example.movieclubone.Feature.SignIn.AuthViewModel
import com.example.movieclubone.Feature.movieSearch.Movie
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.movieclubone.utils.TurnOrder
import com.example.movieclubone.ViewModels.MoviesViewModel
import kotlinx.coroutines.delay // Import for simulated delay
import com.example.movieclubone.Data.Users
import com.example.movieclubone.Feature.SignIn.FirebaseUISignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
    var currentUser by remember { mutableStateOf<Users?>(null) }
    val movies by getUserMovies().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val canChooseMovie by moviesViewModel.canChooseMovie.observeAsState(true)


    // Fetch the current user's turn order
    Log.d("ProfileSettings", "Current state of canChooseMovie at beginning of profile setting load $canChooseMovie")
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            currentUser = getCurrentUserAsync()
            Log.d("ProfileSettings", "Current user fetched: ${currentUser?.displayName}")

            turnOrder.fetchTurnOrder { _, userTurnOrder ->
                currentUserTurnOrder = userTurnOrder
                Log.d("ProfileSettings", "User turn order: $currentUserTurnOrder")
            }
        }
    }

    LaunchedEffect(key1 = "featuredMovieCheck") {
        scope.launch {
            val featuredMovieRef = Firebase.firestore.collection("systemData").document("featuredMovie")
            try {
                val document = featuredMovieRef.get().await()
                val featuredMovieUserId = document.getString("userId")
                val isCurrentUserFeatured = currentUser?.uid == featuredMovieUserId
                if (isCurrentUserFeatured) {
                    moviesViewModel.setCanChooseMovie(false)
                }

                Log.d("ProfileSettings", "Featured movie check completed: canChooseMovie = $canChooseMovie")
                Log.d("ProfileSettings", "Is current user admin?: ${currentUser?.isAdmin}")
            } catch (e: Exception) {
                Log.e("ProfileSettings", "Error fetching featured movie", e)
                // Optionally, handle the error case by setting canChooseMovie accordingly
            }
        }
    }


    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page title
            Text(
                text = "Profile Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Black)
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
            if (currentUser?.isAdmin == true) {
                Log.d("ProfileSettings", "Current user is admin")
                Button(
                    onClick = {
                        navController.navigate("AdminPage")
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Admin Page")
                }
            }
            if (currentUser?.turnOrder == 0 && canChooseMovie) {
                Button(
                    onClick = {
                        scope.launch {
                            movies?.firstOrNull()?.let { movie ->
                                try {
                                    moviesViewModel.setFeaturedMovie(movie, currentUser!!)
                                    // Consider moving this line inside setFeaturedMovie if it performs async operations
                                    // and updating the canChooseMovie there after the Firestore update is confirmed
                                    moviesViewModel.setCanChooseMovie(false)
                                    Toast.makeText(context, "Movie set for group watching", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Log.e("ProfileSettings", "Error setting featured movie: ${e.message}")
                                }
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
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

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




suspend fun getCurrentUserAsync(): Users? {
    val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return null
    val userDocument = Firebase.firestore.collection("users").document(firebaseUser.uid).get().await()

    // Assuming that turnOrder and nextPickDate are stored within the user's document in Firestore
    val turnOrder = userDocument.getLong("turnOrder")?.toInt() // Firestore stores numbers as Long, convert to Int
    val nextPickDate = userDocument.getDate("nextPickDate") // Firestore can store and retrieve Date objects directly
    val isAdmin = userDocument.getBoolean("isAdmin")

    return Users(
        uid = firebaseUser.uid,
        displayName = firebaseUser.displayName,
//        photoUrl = firebaseUser.photoURL?.toString(),
        turnOrder = turnOrder,
        nextPickDate = nextPickDate,
        isAdmin = isAdmin
    )

}

