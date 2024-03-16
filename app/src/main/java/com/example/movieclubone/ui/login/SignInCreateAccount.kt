package com.example.movieclubone.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun SignInCreateAccount(onSignIn: () -> Unit, onCreateAccount: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onSignIn) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onCreateAccount) {
            Text(text = "Create Account")
        }
    }
}