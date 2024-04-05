package com.example.movieclubone.Common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movieclubone.Feature.SignIn.AuthViewModel

@Composable
fun BottomNavigationBar(navController: NavController, authViewModel: AuthViewModel) {
    // Observing user LiveData from AuthViewModel
    val user by authViewModel.user.observeAsState()

    BottomAppBar {
        IconButton(
            onClick = { navController.navigate("HomePage") },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(
            onClick = { navController.navigate("MovieSearchScreen") },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(
            onClick = {navController.navigate("ChatScreen") },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Chat")
        }
        IconButton(
            onClick = { navController.navigate("ProfileSettings") },
            modifier = Modifier.weight(1f)
        ) {
            // Check if the photo URL is not null; if it is, display a default icon
            if (user?.photoUrl != null) {
                AsyncImage(
                    model = user?.photoUrl.toString(),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp) // Set the size of the image
                )
            } else {
                // Default profile icon if photo URL is null
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}




