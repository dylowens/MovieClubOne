package com.example.movieclubone.Common

import com.example.movieclubone.Feature.Messaging.ChatViewModel
import com.example.movieclubone.Feature.SignIn.FirebaseUISignIn
import com.example.movieclubone.Common.theme.MovieClubOneTheme
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.movieclubone.R
import com.example.movieclubone.utils.TurnOrder
import com.example.movieclubone.ViewModels.MoviesViewModel
import com.example.movieclubone.Data.Users
import com.example.movieclubone.Feature.movieSearch.MovieApiService
import com.example.movieclubone.Feature.movieSearch.MovieRepository
import com.example.movieclubone.Feature.SignIn.AuthViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import getRegistrationToken
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import updateFCMTokenForCurrentUser
import kotlin.math.abs


class MainActivity : ComponentActivity() {
    private lateinit var signInHelper: FirebaseUISignIn
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var auth: FirebaseAuth
    private val chatViewModel = ChatViewModel()
    private var gestureDetector: GestureDetector? = null
    // Firebase SignInLauncher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        signInHelper.onSignInResult(res)
    }

    // Declare the launcher at the top of your Activity/Fragment:
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieClubOneTheme {
                MainContent(this)
            }
            askNotificationPermission()
            gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    // Ensure it's a left-to-right swipe and not too much vertical movement
                    if (e2.x - e1!!.x > 100 && abs((e2.y - e1!!.y).toDouble()) < 100) {
                        // Go back action
                        onBackPressed()
                        return true
                    }
                    return false
                }
            })

        }

        fun onTouchEvent(event: MotionEvent?): Boolean {
            if (event != null) {
                gestureDetector?.onTouchEvent(event)
            }
            return super.onTouchEvent(event)
        }





        auth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                // User is signed in
                chatViewModel.onUserAuthenticationChange()
            } else {
                // User is signed out
                chatViewModel.onUserSignedOut()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "chat_messages_channel" // This is the Channel ID
            val name = getString(R.string.channel_name) // Chat Messages
            val descriptionText = getString(R.string.channel_description) // Notifications for new chat messages
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)

    }

    override fun onStop() {
        super.onStop()
        if(::authStateListener.isInitialized) {
            auth.removeAuthStateListener(authStateListener)
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

                                    lifecycleScope.launch {
                                        val token = getRegistrationToken()
                                        if (token != null) {
                                            updateFCMTokenForCurrentUser(token)
                                        } else {
                                            Log.w(TAG, "FCM token retrieval failed")
                                        }
                                    }

                                    Log.d(
                                        "Auth",
                                        "User already exists, skipping turn order assignment."
                                    )
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
                                        "turnEndDate" to null,
                                        "isAdmin" to false,
                                        "fcmToken" to null
                                    )
                                    lifecycleScope.launch {
                                        val token = getRegistrationToken()
                                        if (token != null) {
                                            updateFCMTokenForCurrentUser(token)
                                        } else {
                                            Log.w(TAG, "FCM token retrieval failed")
                                        }
                                    }

                                    userDocRef.set(userInfoMap).addOnSuccessListener {
                                        Log.d(
                                            "Auth",
                                            "User info saved to Firestore for the first time."
                                        )

                                        // Now assign turn order since this is a new user
                                        turnOrder.assignTurnOrderToNewUser(userInfo) { success ->
                                            if (success) {
                                                Log.d(
                                                    "TurnOrder",
                                                    "Turn order assigned successfully for new user."
                                                )
                                            } else {
                                                Log.e(
                                                    "TurnOrder",
                                                    "Failed to assign turn order for new user."
                                                )
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

        Scaffold() { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Navigation(
                    context = context,
                    navController = navController,
                    signInHelper = signInHelper,
                    authViewModel = AuthViewModel(),
                    moviesViewModel = moviesViewModel,
                    turnOrder = turnOrder,
                    chatViewModel = ChatViewModel()
                )
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

