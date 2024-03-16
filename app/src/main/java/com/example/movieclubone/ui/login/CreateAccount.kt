package com.example.movieclubone.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CreateAccount() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var clubId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerTextField ->
                if (username.isEmpty()) {
                    Text("Username", style = MaterialTheme.typography.bodyMedium)
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                if (password.isEmpty()) {
                    Text("Password", style = MaterialTheme.typography.bodyMedium)
                }
                innerTextField()
            }
        )

        BasicTextField(
            value = clubId,
            onValueChange = { clubId = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerTextField ->
                if (clubId.isEmpty()) {
                    Text("Club ID (Optional)", style = MaterialTheme.typography.bodyMedium)
                }
                innerTextField()
            }
        )

        Button(
            onClick = { /* Handle create account */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Account")
        }
    }
}
