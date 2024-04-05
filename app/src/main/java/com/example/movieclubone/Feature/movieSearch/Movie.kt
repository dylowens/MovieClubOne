package com.example.movieclubone.Feature.movieSearch

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val backdropPath: String? = null,
    val genreIds: List<Int> = emptyList(),
    val originalLanguage: String = "",
    val originalTitle: String = "",
    val popularity: Double = 0.0,
    @SerializedName("poster_path") val posterPath: String? = null,
    val releaseDate: String? = null,
    val video: Boolean = false,
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0
)
