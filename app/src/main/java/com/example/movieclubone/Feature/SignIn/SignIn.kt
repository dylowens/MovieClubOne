import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.movieclubone.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import com.example.movieclubone.Feature.SignIn.FirebaseUISignIn

@Composable
fun SignIn(navController: NavHostController, signInHelper: FirebaseUISignIn) {
        val compositionResult by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logolottie))
        val isAnimationReady by remember { derivedStateOf { compositionResult != null } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = isAnimationReady, animationSpec = tween(durationMillis = 500)) { ready ->
            if (ready) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(20.dp)) // Rounded corners
                            .background(Color.LightGray) // Background color for Lottie Animation
                            .padding(16.dp), // Padding inside the rounded background
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = compositionResult,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.matchParentSize() // Make Lottie fill the Box
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Space between Lottie and text
//
                    Text(
                        text = "Welcome to Movie Club!",
                        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            // Trigger the sign-in flow
                            signInHelper.triggerSignInFlow()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Show a loading indicator or alternative content while waiting
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading...", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
