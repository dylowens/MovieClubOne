package com.example.movieclubone.movieSearch


import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface MovieApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String
    ): Response<MovieSearchResponse>
}

