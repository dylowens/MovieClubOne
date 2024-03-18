package com.example.movieclubone.bottomappbar

import FirebaseUISignIn
import android.content.Context
import android.widget.Toast
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



@Composable
fun ProfileSettings(context: Context, navController: NavHostController, signInHelper: FirebaseUISignIn){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile settings options here
        // For example, Sign Out
        Button(
            onClick = { signInHelper.triggerSignOut()
                        navController.navigate("HomePage")
                        Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show()
                      },
            modifier = Modifier.padding(top = 16.dp)

        ) {
            Text("Sign Out")
        }
    }
}
