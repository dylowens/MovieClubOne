package com.example.movieclubone.movieSearch

data class MovieSearchResponse(
    val results: List<Movie>,
    val page: Int,
    val totalPages: Int,
    val totalResults: Int
)

