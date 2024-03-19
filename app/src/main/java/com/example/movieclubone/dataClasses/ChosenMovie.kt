package com.example.movieclubone.dataClasses

import com.example.movieclubone.movieSearch.Movie

data class ChosenMovie(
    val movie: Movie,
    val userId: String = "",
    val userName: String = "",
    val turnOrderEndDate: Long = 0L
)