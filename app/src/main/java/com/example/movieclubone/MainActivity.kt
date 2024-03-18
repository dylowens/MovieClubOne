package com.example.movieclubone



import FirebaseUISignIn
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
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.theme.MovieClubOneTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


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


                signInHelper.setSignInResultListener(object : FirebaseUISignIn.SignInResultListener {
                    override fun onSignInSuccess() {
                        Toast.makeText(this@MainActivity, "Sign in Successful", Toast.LENGTH_LONG).show()
                    }
                    override fun onSignInFailed(errorCode: Int?) {
                        Toast.makeText(this@MainActivity, "Sign in failed: $errorCode", Toast.LENGTH_LONG).show()
                    }
                })
                Navigation(this@MainActivity, navController, signInHelper, AuthViewModel())

            }
            }
        }

    }



