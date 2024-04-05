import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.movieclubone.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Lottie animation
        val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.movieclublogolottie))
        LottieAnimation(
            composition = composition.value,
            modifier = Modifier.fillMaxSize() // Adjust the modifier as needed
        )

        LaunchedEffect(key1 = true) {
            delay(3000) // Delay for 3 seconds
            navController.navigate("signIn") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}
