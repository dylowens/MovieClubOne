package com.example.movieclubone.dataClasses

import java.util.Date

data class Users(
    val uid: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    val turnOrder: Int? = null,
    val nextPickDate: Date? = null
)



