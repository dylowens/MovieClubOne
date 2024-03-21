package com.example.movieclubone.dataClasses

import com.example.movieclubone.movieSearch.Movie

data class ChosenMovie(
    val movie: Movie? = null,
    val userId: String = "",
    val userName: String = "",
    val turnOrderEndDate: Long = 0L,
    val posterPath: String = "",
    val title: String = "",
    val turnOrderEndDateFormatted: String = "",

        // Additional fields as needed
    ) {


}
