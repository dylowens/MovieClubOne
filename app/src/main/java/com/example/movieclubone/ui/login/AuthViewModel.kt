package com.example.movieclubone.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData to observe the authentication state
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    init {
        // Set the initial user value
        _user.value = firebaseAuth.currentUser

        // Listen for authentication state changes
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            _user.value = firebaseAuth.currentUser
        }
    }

    // Method to sign out the user
    fun signOut() {
        firebaseAuth.signOut()
        // No need to manually set _user to null, the AuthStateListener will update it
    }
}
