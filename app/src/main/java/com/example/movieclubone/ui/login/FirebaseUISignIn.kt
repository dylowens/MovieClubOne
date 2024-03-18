import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.movieclubone.MainActivity
import com.example.movieclubone.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

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
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        signInLauncher.launch(signInIntent)
    }

    fun triggerSignInFlow() {
        startSignInFlow()
    }

    fun triggerSignOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()

        // Sign out from Google and disable auto sign-in
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            // Optional: Show message or update UI
        }.addOnCompleteListener {
            // Revoke Google sign in access to ensure the account picker is shown next time
            googleSignInClient.revokeAccess().addOnCompleteListener(activity) {
                // Navigate back to your login screen or activity here
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
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
