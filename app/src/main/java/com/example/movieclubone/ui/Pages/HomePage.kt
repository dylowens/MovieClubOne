package com.example.movieclubone.ui.Pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.TurnOrder
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.bottomappbar.BottomNavigationBar
import com.example.movieclubone.dataClasses.ChosenMovie
import com.example.movieclubone.dataClasses.Users
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.ViewModels.MoviesViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomePage(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    turnOrder: TurnOrder,
    moviesViewModel: MoviesViewModel
) {
    val chosenMovies = remember { mutableStateOf<List<ChosenMovie>>(emptyList()) }
    // Change the featuredMovie to hold a ChosenMovie? instead of Movie?
    val featuredMovie = remember { mutableStateOf<ChosenMovie?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = "featuredMovie") {
        scope.launch {
            Log.d("HomePage", "Fetching featured movie")
            try {
                val featuredMovieSnapshot = Firebase.firestore.collection("systemData")
                    .document("featuredMovie")
                    .get()
                    .await()
                val movie = featuredMovieSnapshot.toObject<Movie>() // Get the Movie object
                val userName = featuredMovieSnapshot.getString("userName") ?: "Unknown"
                val userId = featuredMovieSnapshot.getString("userId") ?: ""
                val turnOrderEndDate = featuredMovieSnapshot.getLong("turnOrderEndDate") ?: 0L
                val turnOrderEndDateFormatted = featuredMovieSnapshot.getString("turnOrderEndDateFormatted") ?: ""

                if (movie != null) {
                    // Ensure posterPath and title fields are directly populated from the movie object
                    featuredMovie.value = movie.posterPath?.let {
                        ChosenMovie(
                            movie = movie,
                            userId = userId,
                            userName = userName,
                            turnOrderEndDate = turnOrderEndDate,
                            turnOrderEndDateFormatted = turnOrderEndDateFormatted,
                            posterPath = it, // Populate this from the movie object
                            title = movie.title // Populate this from the movie object
                        )
                    }
                }
                Log.d("HomePage", "Featured movie fetched: ${featuredMovie.value?.title}")
            } catch (e: Exception) {
                Log.e("HomePage", "Error fetching featured movie", e)
            }
        }
    }



    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Page title
            Text(
                text = "HomePage",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.Black
            )
            TopIconContainer(turnOrder)
            if (featuredMovie.value != null) {
                MainContentFeed(featuredMovie, navController) // Here featuredMovie is already State<Movie?>
            }
        }
    }
}

@Composable
fun TopIconContainer(turnOrder: TurnOrder) {
    val users = remember { mutableStateOf(emptyList<Users>()) }
    var showDialog by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf<Users?>(null) }

    LaunchedEffect(key1 = true) {
        turnOrder.fetchTurnOrder { fetchedUsers, _ ->
            users.value = fetchedUsers
            isAdmin = fetchedUsers.find { it.isAdmin == true }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            users.value.take(5).forEachIndexed { index, user -> // Limit to 5 for cleaner UI
                UserIcon(user, index == 0)
            }
            MoreIcon(users.value.size > 5) // Indicate more users are available
        }
    }
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(text = "Turn Order", style = MaterialTheme.typography.headlineMedium)
                    Divider(color = Color.Gray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    users.value.forEachIndexed { index, user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (index == 0) "Current: " else "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(80.dp) // Adjusted width for alignment
                            )
                            Image(
                                painter = rememberAsyncImagePainter(user.photoUrl),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = user.displayName ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Clickable text for suggesting an edit
                    Text(
                        text = "Suggest an edit",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        modifier = Modifier
                            .clickable {
                                isAdmin?.let { admin ->
                                    // Implement the logic to send a message to the admin
                                    // sendMessageToAdmin(admin)
                                }
                                showDialog = false
                            }
                            .align(Alignment.End)
                            .padding(8.dp)
                    )
                    Text(
                        text = "Turn Order will Rotate Every Other Tuesday at 7:00pm PST.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun UserIcon(user: Users, isCurrent: Boolean) {
    val borderColor = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.photoUrl),
            contentDescription = "Profile of ${user.displayName}",
            modifier = Modifier
                .size(46.dp) // Slightly smaller to create a border effect
                .clip(CircleShape)
        )
    }
}

@Composable
fun MoreIcon(hasMore: Boolean) {
    if (hasMore) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(4.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}


@Composable
fun MainContentFeed(featuredMovie: MutableState<ChosenMovie?>, navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        featuredMovie.value?.let { chosenMovie ->
            FeaturedMovieItem(chosenMovie = chosenMovie)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("PreviouslyChosenPage") },
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(imageVector = Icons.Filled.Lock, contentDescription = "Previously Shown Movies", tint = MaterialTheme.colorScheme.onSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Previously Shown Movies", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}
@Composable
fun FeaturedMovieItem(chosenMovie: ChosenMovie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            focusedElevation = 7.dp,
            hoveredElevation = 7.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Ensure the column itself aligns its children in the center horizontally
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth() // Ensure the Column takes up the full width of the Card
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/w500${chosenMovie.posterPath}"),
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp)) // Maintains the image aspect ratio and clips to a rounded shape
            )

            Spacer(modifier = Modifier.height(16.dp))

            // For the title and the popcorn icon, wrapping them in a Box with centered alignment to ensure they are centered as well
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock, // Placeholder for your icon
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chosenMovie.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Text(
                text = "Picked by: ${chosenMovie.userName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Currently Watching. You have until: ${chosenMovie.turnOrderEndDateFormatted}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center, // Ensures the text itself is centered within its layout space
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


// Assuming you have a Popcorn and Movie Tape icon defined somewhere, or use placeholders if not
//@Composable
//fun Icons.Default.Popcorn() = Icons.Filled.Fastfood // Placeholder for a popcorn icon
