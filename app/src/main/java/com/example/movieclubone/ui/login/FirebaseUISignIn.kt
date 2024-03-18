import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.provider.Settings.System.getString
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.startActivity
import com.example.movieclubone.MainActivity
import com.example.movieclubone.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class FirebaseUISignIn(private val activity: Activity, private val signInLauncher: ActivityResultLauncher<Intent>) {


    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(activity.getString(R.string.web_client_id)) // Use string resource
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(activity, gso)

    interface SignInResultListener {
        fun onSignInSuccess()
        fun onSignInFailed(errorCode: Int?)
    }

    private var signInResultListener: SignInResultListener? = null

    fun setSignInResultListener(listener: SignInResultListener) {
        signInResultListener = listener
    }

    fun startSignInFlow() {
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

    fun triggerSignInFlow() {
        startSignInFlow()
    }

    fun triggerSignOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            // Handle sign-out success, for example, navigate the user to the sign-in screen
        }.continueWithTask {
            // Now, disconnect the account to ensure the account picker is shown next time
            googleSignInClient.revokeAccess()
        }.addOnCompleteListener(activity) {
            // After successfully revoking access, navigate back to your main activity or login screen
            val intent = Intent(activity, MainActivity::class.java) // Adjust to your main activity
            activity.startActivity(intent)
            activity.finish()
        }
    }


    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            signInResultListener?.onSignInSuccess()
        } else {
            signInResultListener?.onSignInFailed(response?.error?.errorCode)
        }
    }
}
