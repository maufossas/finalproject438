package com.example.finalproject.Network

import com.example.finalproject.Data.Movie
import com.example.finalproject.Data.MoviePayload
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

// interface to denote the different api calls made
interface MovieInterface {

    // for default loading in, get by trending (default: 1 day)
    @GET("trending/movie/{time_window}")
    suspend fun getByTrending(@Path("time_window") time : String,
                              @Query("api_key") key : String): Response<MoviePayload>

    // search terms
    @GET("search/movie")
    suspend fun getBySearch(@Query("api_key") key : String, @Query("query") search : String) : Response<MoviePayload>

    // by movie id
    @GET("movie/{movie_id}")
    suspend fun getByID(@Path("movie_id") movie : String,
                        @Query("api_key") key : String) : Response<Movie>

    // by discovery terms
    @GET("discover/movie")
    suspend fun getByDiscover(@QueryMap args: Map<String, String>) : Response<MoviePayload>

}