package com.example.movieclubone.movieSearch

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val backdropPath: String?, // Use this for the background image of the movie
    val genreIds: List<Int>,
    val originalLanguage: String,
    val originalTitle: String,
    val popularity: Double,
    val posterPath: String?, // Path to the movie poster image
    val releaseDate: String?,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int
)

