package com.example.movieapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.runBlocking
import com.example.movieapp.Genre

object ApiService {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val movieApi: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    // Genre cache
    private var genreMap: Map<Int, String>? = null

    fun getGenreMap(apiKey: String): Map<Int, String> {
        if (genreMap == null) {
            runBlocking {
                try {
                    val response = movieApi.getGenres(apiKey)
                    genreMap = response.genres.associate { it.id to it.name }
                } catch (e: Exception) {
                    genreMap = emptyMap()
                }
            }
        }
        return genreMap ?: emptyMap()
    }

    fun mapGenreIdsToNames(ids: List<Int>, apiKey: String): List<String> {
        val map = getGenreMap(apiKey)
        return ids.mapNotNull { map[it] }
    }
} 