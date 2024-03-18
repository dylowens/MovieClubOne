package com.example.movieclubone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.movieclubone.ui.login.AuthViewModel
import com.example.movieclubone.bottomappbar.BottomNavigationBar


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

