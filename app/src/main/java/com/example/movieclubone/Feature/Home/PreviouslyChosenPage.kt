import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.movieclubone.ViewModels.MoviesViewModel
import com.example.movieclubone.Data.ChosenMovie

@Composable
fun PreviouslyChosenPage(navController: NavController, viewModel: MoviesViewModel = viewModel()) {
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchChosenMovies() // Assuming this is a method in your ViewModel
    }

    val chosenMovies by viewModel.chosenMovies.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chosenMovies.reversed().drop(1)) { movie ->
            MovieTile(movie = movie)
        }
    }
}

@Composable
fun MovieTile(movie: ChosenMovie) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                    .build()
            ),
            contentDescription = "Movie Poster",
            modifier = Modifier
                .size(150.dp)
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Chosen by: ${movie.userName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
