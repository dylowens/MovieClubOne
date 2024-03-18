package com.example.movieclubone.ui.login

import FirebaseUISignIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.movieclubone.MainActivity


@Composable
fun SignIn(navController: NavHostController, signInHelper: FirebaseUISignIn) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Button(
            onClick = {
                signInHelper.triggerSignInFlow()
                navController.navigate("HomePage")
            }
        ) {
            Text(text = "Sign In")
        }
    }
}
