package com.example.movieclubone.dataClasses

import java.util.Date

data class MoviePick(
val movieId: String, // ID of the movie in the movie collection
val pickedBy: String, // User ID of the user who picked the movie
val pickDate: Date // Date when the movie is scheduled to be shown
)
