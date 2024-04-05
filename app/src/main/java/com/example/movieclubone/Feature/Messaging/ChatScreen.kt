import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieclubone.Data.Message
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.movieclubone.R
import com.example.movieclubone.Common.BottomNavigationBar
import com.example.movieclubone.Feature.Messaging.ChatViewModel
import com.example.movieclubone.Feature.SignIn.AuthViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.reflect.KFunction2

@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel, authViewModel: AuthViewModel){
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    // Observe changes in messages list to scroll to the latest message
    LaunchedEffect(messages) {
        coroutineScope.launch {
            listState.animateScrollToItem(index = 0) // Since reverseLayout = true, 0 is the latest message
        }
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply the padding provided by Scaffold
                .fillMaxSize()
        ) {
            // Integrating the title directly within the Column
            Text(
                text = "Chat",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(16.dp), // Apply padding
                color = Color.Black
            )

            // Your chat messages list
            MessagesList(
                messages = messages,
                listState = listState,
                onDeleteMessage = viewModel::deleteMessage,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth() // Make sure it fills the width
            )

            // Input field for new messages
            MessageInput(onMessageSent = { message ->
                viewModel.sendMessage(message)
            })
        }
    }
}

fun formatDate(timestamp: Timestamp): String {
    val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    // Convert Firebase Timestamp to java.util.Date
    val date = timestamp.toDate()
    return formatter.format(date)
}

@Composable
fun MessagesList(messages: List<Message>, listState: LazyListState, onDeleteMessage: KFunction2<Context, String, Unit>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        reverseLayout = true
    ) {
        items(messages) { message ->
            MessageItem(message, onDeleteMessage)
        }
    }
}
@Composable
fun MessageItem(message: Message, onDeleteMessage: KFunction2<Context, String, Unit>) {
    val showPopup = remember { mutableStateOf(false) }
    val showTimestamp = remember { mutableStateOf(false) }

    BoxWithConstraints {
        val maxWidth = maxWidth

        Card(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .wrapContentWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            showPopup.value = true  // Show the delete dialog on long press
                        },
                        onTap = {
                            showTimestamp.value = !showTimestamp.value  // Toggle timestamp visibility on tap
                        }
                    )
                },
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                ProfilePicture(photoUrl = message.photoUrl, isBot = message.type == "bot")

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.Top)
                ) {
                    Text(
                        text = message.userName ?: "Unknown User",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.message ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.widthIn(max = maxWidth - 64.dp)
                    )

                    if (showTimestamp.value) {
                        message.timestamp?.let {
                            Text(
                                text = formatDate(it),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPopup.value) {
        DeleteMessageDialog(showPopup, onDeleteMessage, message.id)
    }
}

@Composable
fun DeleteMessageDialog(showPopup: MutableState<Boolean>, onDeleteMessage: (Context, String) -> Unit, messageId: String?) {
    // Use .value to check the state and also to update it
    val context = LocalContext.current
    if (showPopup.value) {
        AlertDialog(
            onDismissRequest = { showPopup.value = false }, // Correctly update the state here
            title = { Text("Delete Message") },
            text = { Text("Do you want to delete this message?") },
            confirmButton = {
                TextButton(onClick = {
                    messageId?.let { id ->
                        onDeleteMessage(context, id)
                    }
                    showPopup.value = false // Correctly update the state here
                    Log.d("ChatScreen","Message ID: ${messageId}")
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPopup.value = false }) { // Correctly update the state here
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ProfilePicture(photoUrl: String?, isBot: Boolean) {
    val painter = if (isBot) {
        // For bot, use a drawable resource
        painterResource(R.drawable.aibot)
    } else if (photoUrl != null) {
        // For users with a photo URL, load the image from the URL
        rememberAsyncImagePainter(model = photoUrl)
    } else {
        // Fallback placeholder for users without a photo URL
        painterResource(R.drawable.aibot) // or a different placeholder if needed
    }

    Image(
        painter = painter,
        contentDescription = "Profile picture",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(onMessageSent: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    // Stylish and smoother input field with rounded corners and an icon
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium, // Use medium shape for rounded corners
        color = MaterialTheme.colorScheme.surfaceVariant, // Use a slightly different surface color for distinction
        shadowElevation = 2.dp // Slight elevation for depth
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp), // Ensure some spacing between the text field and the send button
                placeholder = { Text("Type a message...") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray.copy(alpha = 0.3f), // Use this for focused state if specific customization is not possible
                    unfocusedIndicatorColor = Color.Transparent, // Usually controls the underline color
                    focusedIndicatorColor = Color.Transparent, // Making the underline transparent for both states
                    cursorColor = Color.DarkGray,
                    // Note: The focused and unfocused container colors might need to be handled differently based on your TextField version or Material Design implementation.
                ),
                shape = MaterialTheme.shapes.medium // Ensure the text field corners are rounded
            )

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onMessageSent(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(56.dp), // Make the button a square shape
                shape = CircleShape, // Button with rounded corners for a more modern look
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Use primary color for the button
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, // Assuming you're using material icons
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary // Ensure the icon is visible against the button color
                )
            }
        }
    }
}
