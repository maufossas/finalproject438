package com.example.finalproject.Network

import com.example.finalproject.Data.Movie
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// interface to denote the different api calls made
interface MovieInterface {

    // for default loading in (home view), get by popular
    @GET("chart")
    suspend fun getByTrending(): Response<Movie>

}