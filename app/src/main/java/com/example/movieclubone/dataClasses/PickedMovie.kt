package com.example.movieclubone.dataClasses

import com.example.movieclubone.movieSearch.Movie

data class PickedMovie(
    val movie: Movie,
    val userId: String = "",
    val userName: String = "",
    val turnOrderEndDate: Long = 0L
)