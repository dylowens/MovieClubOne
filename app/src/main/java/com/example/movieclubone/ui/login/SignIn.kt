package com.example.movieclubone.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignIn() {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val clubId = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        BasicTextField(
            value = username.value,
            onValueChange = { username.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerTextField ->
                if (username.value.isEmpty()) {
                    Text("Username", style = MaterialTheme.typography.bodyMedium)
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = password.value,
            onValueChange = { password.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                if (password.value.isEmpty()) {
                    Text("Password", style = MaterialTheme.typography.bodyMedium)
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = clubId.value,
            onValueChange = { clubId.value = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerTextField ->
                if (clubId.value.isEmpty()) {
                    Text("Club ID (Optional)", style = MaterialTheme.typography.bodyMedium)
                }
                innerTextField()
            }
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Sign In")
        }
    }
}
