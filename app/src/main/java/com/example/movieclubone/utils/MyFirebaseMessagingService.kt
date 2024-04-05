import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.movieclubone.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        getRegistrationToken()
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d(TAG, "Token updated successfully") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating token", e) }
    }


    private fun sendNotification(title: String?, body: String?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.loginlogo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "Channel_ID"
    }

    fun getRegistrationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result ?: return@OnCompleteListener
            // Log and optionally update the token in the server
            Log.d(TAG, "FCM Token: $token")
            sendRegistrationToServer(token)
        })
    }
}
suspend fun getRegistrationToken() = withContext(Dispatchers.IO) {
    try {
        val token = FirebaseMessaging.getInstance().token.await()
        return@withContext token
        Log.d(TAG, "FCM Token: $token")
    } catch (e: Exception) {
        Log.w(TAG, "Fetching FCM registration token failed", e)
        return@withContext null
    }
}


suspend fun updateFCMTokenForCurrentUser(token: Any) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        try {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .update("fcmToken", token)
                .await() // Use await for suspend function
            Log.d(TAG, "FCM token updated for user: $userId")
        } catch (e: Exception) {
            Log.w(TAG, "Error updating FCM token for user: $userId", e)
        }
    } else {
        Log.w(TAG, "Cannot update FCM token because user ID is null")
    }
}
