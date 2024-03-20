package com.example.movieclubone


import com.example.movieclubone.dataClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class TurnOrder(private val firestore: FirebaseFirestore) {

    private val usersCollection = firestore.collection("users")

    // Assigns turn order to a new user
    fun assignTurnOrderToNewUser(user: Users, onComplete: (Boolean) -> Unit) {
        usersCollection.orderBy("turnOrder", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val highestOrder = querySnapshot.documents.firstOrNull()?.getLong("turnOrder") ?: 0
                val newUserOrder = highestOrder + 1

                usersCollection.document(user.uid)
                    .update("turnOrder", newUserOrder)
                    .addOnCompleteListener { task ->
                        onComplete(task.isSuccessful)
                    }
            }
    }

    // Fetches the turn order and the user's position within it
    fun fetchTurnOrder(onComplete: (List<Users>, Int?) -> Unit) {
        usersCollection.orderBy("turnOrder")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = querySnapshot.documents.mapNotNull { it.toObject(Users::class.java) }
                val userTurnOrder = users.indexOfFirst { it.uid == FirebaseAuth.getInstance().currentUser?.uid }
                onComplete(users, userTurnOrder)
            }
    }

    // Rotates the turn order every two weeks
    fun rotateTurnOrder() {
        // This could be triggered by a Cloud Function on a scheduled basis
        fetchTurnOrder { users, _ ->
            if (users.isNotEmpty()) {
                // Move the first user to the end and update the others' turnOrder
                val newOrder = users.drop(1) + users.first()
                newOrder.forEachIndexed { index, user ->
                    usersCollection.document(user.uid).update("turnOrder", index.toLong())
                }
            }
        }
    }

    // Update the turn order manually - useful for a UI where admins can change the order
    fun updateTurnOrder(updatedUsers: List<Users>) {
        updatedUsers.forEachIndexed { index, user ->
            usersCollection.document(user.uid).update("turnOrder", index.toLong())
        }
    }
}


