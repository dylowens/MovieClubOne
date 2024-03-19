package com.example.movieclubone.dataClasses

import java.util.Date

data class Users(
    val uid: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val turnOrder: Long = 0L, // Note: Make sure this matches the type expected in Firestore.
    val turnEndDate: Date? = null
)


