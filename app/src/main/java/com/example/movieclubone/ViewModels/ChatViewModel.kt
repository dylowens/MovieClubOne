import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieclubone.dataClasses.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlinx.coroutines.delay


class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages
    private var messagesListenerRegistration: ListenerRegistration? = null

    init {
        listenForMessages()
    }


    fun clearMessagesListener() {
        messagesListenerRegistration?.remove()
    }

    fun onUserAuthenticationChange() {
        _messages.value = emptyList() // Clear messages when user changes
        clearMessagesListener() // Clear existing message listener
        listenForMessages() // Reinitialize message listener for new user context
    }


    fun onUserSignedOut() {
        clearMessagesListener()
        _messages.value = emptyList()
    }

    private fun listenForMessages() {
        Log.d("ChatViewModel", "Attempting to listen for messages")
        messagesListenerRegistration?.remove() // Remove any existing listener to prevent duplicates.

        messagesListenerRegistration = db.collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ChatViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val messagesList =
                    snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) }.orEmpty()
                viewModelScope.launch(Dispatchers.Main) {
                    _messages.value = messagesList
                }
            }
    }


    private var lastBotPromptTime: Timestamp? = null


    fun sendMessage(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = auth.currentUser?.uid ?: "anonymous"
            val userName = auth.currentUser?.displayName ?: "Anonymous User"
            val timestamp = Timestamp.now()
            val photoUrl = auth.currentUser?.photoUrl.toString()

            if (content.startsWith("@bot:")) {
                val promptContent = content.removePrefix("@bot:")
                lastBotPromptTime = timestamp
                val messageId = UUID.randomUUID().toString()

                val botPrompt = Message(
                    id = messageId,
                    userId = userId,
                    userName = userName,
                    message = promptContent,
                    timestamp = timestamp,
                    photoUrl = photoUrl,
                    type = "bot_prompt"
                )

                // Adding the botPrompt to Firestore
                db.collection("messages").add(botPrompt).await()
                // Immediately display a "thinking" message
                val thinkingMessage = Message(
                    id = UUID.randomUUID().toString(), // Generate a new unique ID for the thinking message
                    userId = "",
                    userName = "",
                    message = "The bot is thinking...",
                    timestamp = Timestamp.now(),
                    type = "bot_thinking"
                )
                val thinkingMessageRef = db.collection("messages").add(thinkingMessage).await() // Keep a reference to remove it later

                // Add to 'prompts' collection for processing
                val promptDocumentRef = db.collection("prompts").add(mapOf("prompt" to promptContent, "status" to "processing")).await()

                // Wait for the bot to process the prompt
                var completeTime: Timestamp? = null
                while (completeTime == null) {
                    delay(2000) // Polling interval: 2 seconds
                    val updatedPrompt = promptDocumentRef.get().await()
                    completeTime = updatedPrompt.getTimestamp("status.completeTime")
                }

                // Once the response is ready, remove the "thinking" message
                thinkingMessageRef.delete().await()

                // Now that we have completeTime, fetch the most recent prompt document, assuming it has been updated
                val querySnapshot = db.collection("prompts")
                    .orderBy("createTime", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                val document = querySnapshot.documents.firstOrNull()
                if (document != null) {
                    val promptResponse = document.getString("response") ?: ""
                    val promptCreateTime = document.getTimestamp("createTime")

                    val botResponseMessage = Message(
                        id = messageId,
                        userId = "bot",
                        userName = "Bot",
                        message = promptResponse,
                        timestamp = promptCreateTime,
                        type = "bot",
                        response = promptResponse
                    )

                    // Add bot response message to Firestore
                    db.collection("messages").add(botResponseMessage)
                    // Optionally, update local state for immediate UI update
                    _messages.value = _messages.value + listOf(botResponseMessage)
                }

            } else {
                // Regular user message handling
                val newMessage = Message(
                    userId = userId,
                    userName = userName,
                    message = content,
                    timestamp = timestamp,
                    photoUrl = photoUrl,
                    type = "user"
                )
                // Add the regular message to Firestore
                db.collection("messages").add(newMessage)
                // Optionally, update local state for immediate UI update
                _messages.value = _messages.value + listOf(newMessage)
            }
        }
    }

    fun deleteMessage(messageId: String) {
        Log.d("ChatViewModel", "Attempting to delete message with ID: $messageId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("messages").document(messageId).delete().await()
                Log.d("ChatViewModel", "Message deleted successfully")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error deleting message: ", e)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        clearMessagesListener() // Cleanup on ViewModel destruction
    }
}


