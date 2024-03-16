import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log

@Composable
fun JoinClubID() {
    var clubId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        BasicTextField(
            value = clubId,
            onValueChange = { clubId = it },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { Log.d("JoinClub", "Trying to join club with ID: $clubId") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Join Club")
        }
    }
}
