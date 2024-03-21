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
import com.example.movieclubone.dataClasses.Message
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()



    // Observe changes in messages list to scroll to the latest message
    LaunchedEffect(messages) {
        coroutineScope.launch {
            listState.animateScrollToItem(index = 0) // Since reverseLayout = true, 0 is the latest message
        }
    }


        Column(modifier = Modifier.fillMaxSize()) {
            MessagesList(messages = messages, listState = listState, onDeleteMessage = viewModel::deleteMessage, modifier = Modifier.weight(1f, fill = true))
            // Remaining components...
        Spacer(modifier = Modifier.height(8.dp)) // Optional, for spacing
        MessageInput(onMessageSent = { message ->
            viewModel.sendMessage(message)
        })
    }
}



fun formatDate(timestamp: Timestamp): String {
    val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    // Convert Firebase Timestamp to java.util.Date
    val date = timestamp.toDate()
    return formatter.format(date)
}

@Composable
fun MessagesList(messages: List<Message>, listState: LazyListState, onDeleteMessage: (String) -> Unit, modifier: Modifier = Modifier) {
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
fun MessageItem(message: Message, onDeleteMessage: (String) -> Unit) {
    var showTimestamp by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    // You might want to show timestamp or other information in the popup as well.
    // For simplicity, this example focuses on the delete action.
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text("Delete Message") },
            text = { Text("Do you want to delete this message?") },
            confirmButton = {
                TextButton(onClick = {
                    message.id?.let { onDeleteMessage(it) }
                    showPopup = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { showPopup = true }
            )
        }
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
            when (message.type) {
                "bot" -> {
                    // Assuming bot responses contain a message field with the response text
                    Text(
                        text = "Bot Response: \n\n${message.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.background(Color.Blue.copy(alpha = 0.1f)) // Set background color to transparent light blue
                    )

                }
                "bot_prompt" -> {
                    // Display user's profile photo
                    Image(
                        painter = rememberAsyncImagePainter(model = message.photoUrl),
                        contentDescription = "${message.userName}'s profile picture",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    // Display user's name and message in a column
                    Column {
                        // Assuming bot prompts contain a message field with the prompt text
                        Text(
                            text = "@BOT: ${message.message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
//                        modifier = Modifier.background(Color.Green.copy(alpha = 0.1f)) // Set background color to transparent light green
                        )
                    }
                }
                else -> {
                    // Display user's profile photo
                    Image(
                        painter = rememberAsyncImagePainter(model = message.photoUrl),
                        contentDescription = "${message.userName}'s profile picture",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    // Display user's name and message in a column
                    Column {
                        message.userName?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
                        Spacer(modifier = Modifier.height(4.dp))
                        message.message?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
        }
        if (showTimestamp) {
            val timestampText = message.timestamp?.let { formatDate(it) } ?: "Unknown time"
            Text(
                text = timestampText, // Safe call with default value
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 38.dp)
            )
        }

    }
}



@Composable
fun MessageInput(onMessageSent: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    // Use Modifier.fillMaxWidth() to ensure the TextField and Button fill the width of their parent
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(0.85f), // Adjust this value as needed
            placeholder = { Text("Type a message...") }
        )
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onMessageSent(text)
                    text = ""
                }
            },
            modifier = Modifier.align(Alignment.CenterVertically) // Align button with the TextField
        ) {
            Text("Send")
        }
    }
}
