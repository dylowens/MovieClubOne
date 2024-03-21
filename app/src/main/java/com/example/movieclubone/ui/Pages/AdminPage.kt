package com.example.movieclubone.ui.Pages

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.movieclubone.dataClasses.Users
import com.example.movieclubone.fetchUserPrivileges
import getCurrentUserAsync
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieclubone.ViewModels.UserViewModel


@Composable
fun AdminPage(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isAdmin by remember { mutableStateOf(false) }
    val users = userViewModel.users.value

    // Assuming `getCurrentUserAsync` and `fetchUserPrivileges` are suspend functions
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            val currentUser = getCurrentUserAsync()
            currentUser?.uid?.let { uid ->
                fetchUserPrivileges(uid) { admin ->
                    isAdmin = admin
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else if (isAdmin) {
        AdminUI(users = users, userViewModel = userViewModel)
    } else {
        Text("You do not have access to this page.")
    }
}

@Composable
fun AdminUI(users: List<Users>, userViewModel: UserViewModel) {
    var list by remember { mutableStateOf(users) }

    Column {
        LazyColumn {
            itemsIndexed(list) { index, user ->
                UserListItem(
                    user = user,
                    onMoveUp = { if (index > 0) list = list.toMutableList().apply {
                        add(index - 1, removeAt(index))
                    } },
                    onMoveDown = { if (index < list.size - 1) list = list.toMutableList().apply {
                        add(index + 1, removeAt(index))
                    } }
                )
            }
        }
        Button(onClick = {
            // Update Firestore based on the new order
            userViewModel.updateUserTurnOrder(list)
        }) {
            Text("Save Order")
        }
    }
}

@Composable
fun UserListItem(user: Users, onMoveUp: () -> Unit, onMoveDown: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = user.displayName ?: "No Name", modifier = Modifier.weight(1f))
        Button(onClick = onMoveUp) { Text("Up") }
        Button(onClick = onMoveDown) { Text("Down") }
    }
}
