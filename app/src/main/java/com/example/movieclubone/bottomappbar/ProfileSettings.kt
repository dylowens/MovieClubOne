package com.example.movieclubone.bottomappbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.movieclubone.MainActivity
import com.example.movieclubone.ui.login.FirebaseUISignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileSettings(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile settings options here
        // For example, Sign Out
        Button(
            onClick = { FirebaseAuth.getInstance().signOut()

//                      navController.navigate("SignIn")
                      },
// After sign out, handle what you want to do next (e.g., navigate the user to the sign-in screen).
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Sign Out")
        }
    }
}
