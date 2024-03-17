package com.example.movieclubone.ui.login

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.navigation.NavHostController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser
class FirebaseUISignIn(private val activity: ComponentActivity) {

    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
   fun SignIn() {
        //firebase
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
        signInLauncher.launch(signInIntent)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == ComponentActivity.RESULT_OK) {
            // Successfully signed in
            Toast.makeText(activity, "Sign in Successful", Toast.LENGTH_LONG).show()
            val user = FirebaseAuth.getInstance().currentUser



            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(activity, "Sign in failed: ${response?.error?.errorCode}", Toast.LENGTH_LONG)
                .show()

        }
    }
}