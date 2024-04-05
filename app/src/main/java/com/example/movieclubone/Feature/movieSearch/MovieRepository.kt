package com.example.movieclubone.Feature.movieSearch

import android.content.Context
import com.example.movieclubone.R

class MovieRepository(private val context: Context, private val apiService: MovieApiService) {
    suspend fun searchMovies(query: String): List<Movie> {
        // Use the context to get your API key string resource
        val apiKey = context.getString(R.string.TMDB_API_KEY)
        val response = apiService.searchMovies(apiKey = apiKey, query = query)
        // Assuming the response body is correctly parsed and includes a 'results' field
        // Make sure your Retrofit call is setup to handle this correctly
        return response.body()?.results ?: emptyList() // Handle null case
    }
}

