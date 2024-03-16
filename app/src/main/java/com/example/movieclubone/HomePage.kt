package com.example.movieclubone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomePage(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
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
fun BottomNavigationBar() {


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
            onClick = { /* Handle Profile Click */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    HomePage(navController)
}
