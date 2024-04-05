package com.example.movieclubone.Feature.movieSearch

data class MovieSearchResponse(
    val results: List<Movie>,
    val page: Int,
    val totalPages: Int,
    val totalResults: Int
)

