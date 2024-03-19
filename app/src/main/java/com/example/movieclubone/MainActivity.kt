package com.example.movieclubone

import FirebaseUISignIn
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.dataClasses.Users
import com.example.movieclubone.movieSearch.Movie
import com.example.movieclubone.movieSearch.MovieApiService
import com.example.movieclubone.movieSearch.MovieRepository
import com.example.movieclubone.movieSearch.MoviesViewModel
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.ui.theme.MovieClubOneTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    private lateinit var signInHelper: FirebaseUISignIn

    // Firebase SignInLauncher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        signInHelper.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieClubOneTheme {
                MainContent(this)

            }
        }

    }

    @Composable
    fun MainContent(context: Context) {
        val firestoreInstance = FirebaseFirestore.getInstance()
        val turnOrder = TurnOrder(firestoreInstance)
        val navController = rememberNavController()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val movieApiService = retrofit.create(MovieApiService::class.java)
        val movieRepository = MovieRepository(context, movieApiService)
        val moviesViewModel = MoviesViewModel(movieRepository)

        signInHelper = FirebaseUISignIn(this, signInLauncher).apply {
            setSignInResultListener(object : FirebaseUISignIn.SignInResultListener {
                override fun onSignInSuccess() {
                    // Navigate to HomePage on successful sign-in
                    navController.navigate("HomePage") {
                        popUpTo("SignIn") { inclusive = true }
                    }

                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let { firebaseUser ->
                        val usersRef = FirebaseFirestore.getInstance().collection("users")
                        val userDocRef = usersRef.document(firebaseUser.uid)

                        // Check if the user already exists in Firestore
                        userDocRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document != null && document.exists()) {
                                    Log.d("Auth", "User already exists, skipping turn order assignment.")
                                } else {
                                    // User does not exist, proceed to set user info and assign turn order
                                    val userInfo = Users(
                                        uid = firebaseUser.uid,
                                        displayName = firebaseUser.displayName,
                                        photoUrl = firebaseUser.photoUrl.toString()
                                        // turnOrder and turnEndDate will be set later.
                                    )

                                    // Convert Users object to a HashMap to save to Firestore.
                                    val userInfoMap = hashMapOf(
                                        "uid" to userInfo.uid,
                                        "displayName" to userInfo.displayName,
                                        "photoUrl" to userInfo.photoUrl,
                                        // Initially set turnOrder to a placeholder value, it will be updated dynamically.
                                        "turnOrder" to 0,
                                        "turnEndDate" to null
                                    )

                                    userDocRef.set(userInfoMap).addOnSuccessListener {
                                        Log.d("Auth", "User info saved to Firestore for the first time.")

                                        // Now assign turn order since this is a new user
                                        turnOrder.assignTurnOrderToNewUser(userInfo) { success ->
                                            if (success) {
                                                Log.d("TurnOrder", "Turn order assigned successfully for new user.")
                                            } else {
                                                Log.e("TurnOrder", "Failed to assign turn order for new user.")
                                            }
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.w("Auth", "Error saving user info for new user", e)
                                    }
                                }
                            } else {
                                Log.e("Auth", "Failed to check if user exists", task.exception)
                            }
                        }
                    }
                }

                override fun onSignInFailed(errorCode: Int?) {
                    TODO("Not yet implemented")
                }


            })
        }

        Scaffold(
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Navigation(
                    context = context,
                    navController = navController,
                    signInHelper = signInHelper,
                    authViewModel = AuthViewModel(),
                    moviesViewModel = moviesViewModel,
                    movie = Movie()
                )
            }
        }
    }
}