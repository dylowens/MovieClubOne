package com.example.movieclubone.ViewModels

import android.util.Log
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieclubone.dataClasses.Users
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.State


class UserViewModel : ViewModel() {

    private val _users = mutableStateOf<List<Users>>(emptyList())
    val users: State<List<Users>> = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            val usersList = Firebase.firestore.collection("users")
                .orderBy("turnOrder")
                .get()
                .await()
                .toObjects(Users::class.java)
            _users.value = usersList
        }
    }

    fun updateUserTurnOrder(updatedList: List<Users>) {
        viewModelScope.launch {
            val db = Firebase.firestore
            val batch = db.batch()

            // Loop through the updated list and prepare batch updates
            updatedList.forEachIndexed { newIndex, user ->
                val userRef = db.collection("users").document(user.uid)
                batch.update(userRef, "turnOrder", newIndex)
            }

            // Commit the batch
            try {
                batch.commit().await()
                // Optionally refetch the users to reflect the new order in the UI
                fetchUsers()
                Log.d("UserViewModel", "User turn orders updated successfully")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to update user turn orders: ", e)
            }
        }
    }
}
