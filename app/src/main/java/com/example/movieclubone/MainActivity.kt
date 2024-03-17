package com.example.movieclubone



import android.util.Log
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.ui.theme.MovieClubOneTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private var showWelcomeScreen by mutableStateOf(false)
    private var username by mutableStateOf("")

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
    // Firebase
    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieClubOneTheme {
                //Navigation Routing Composable
                // Initialize NavController here


                Log.d("MainActivity","this is a message part one")

                navController = rememberNavController()
                Navigation(navController)

                FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser
                    if (user == null) {
                        // User is signed out, launch the sign-in flow
                        signInLauncher.launch(signInIntent)
                    } else {
                        // User is signed in
                        val username = FirebaseAuth.getInstance().currentUser
                        username?.let {
                            // Name, email address, and profile photo Url
                            val name = it.displayName

                            Toast.makeText(this, "Sign in Successful, $name", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }





            }
        }
    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                // Name, email address, and profile photo Url
                val name = it.displayName
                val email = it.email
                val photoUrl = it.photoUrl

                // Check if user's email is verified
                val emailVerified = it.isEmailVerified

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                val uid = it.uid
                Toast.makeText(this, "Sign in Successful, $name", Toast.LENGTH_LONG).show()

            }



            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(this, "Sign in failed: ${response?.error?.errorCode}", Toast.LENGTH_LONG).show()

        }

    }
}


