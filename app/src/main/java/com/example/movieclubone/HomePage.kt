package com.example.movieclubone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.movieclubone.ui.login.AuthViewModel



@Composable
fun HomePage(navController: NavHostController, authViewModel: AuthViewModel) {

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, authViewModel) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TopIconContainer()
            MainContentFeed()
        }
    }
}

@Composable
fun TopIconContainer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
    }
}

@Composable
fun MainContentFeed() {
    val items = List(20) { "Item $it" }
    LazyColumn {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, authViewModel: AuthViewModel) {
    // Observing user LiveData from AuthViewModel
    val user by authViewModel.user.observeAsState()

    BottomAppBar {
        IconButton(
            onClick = { /* Handle Home Click */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(
            onClick = { /* Handle Search Click */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(
            onClick = { /* Handle Chat Click */ },
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


