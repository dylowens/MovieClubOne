package com.example.movieclubone



import FirebaseUISignIn
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.theme.MovieClubOneTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract


class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var signInHelper: FirebaseUISignIn

    //Firebase SigninLauncher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        signInHelper.onSignInResult(res)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieClubOneTheme {
                //Navigation Routing Composable
                navController = rememberNavController()
                signInHelper = FirebaseUISignIn(this, signInLauncher)


                // Set up the listener for sign-in result
                signInHelper.setSignInResultListener(object : FirebaseUISignIn.SignInResultListener {
                    override fun onSignInSuccess() {
                        // Navigate to HomePage on successful sign-in
                        navController.navigate("HomePage") {
                            // Clear back stack to prevent back navigation to the sign-in screen
                            popUpTo("SignIn") { inclusive = true }
                        }
                    }

                    override fun onSignInFailed(errorCode: Int?) {
                        // Handle sign-in failure (e.g., show a message to the user)
                    }
                })
                Navigation(this@MainActivity, navController, signInHelper, AuthViewModel())

            }
            }
        }

    }



