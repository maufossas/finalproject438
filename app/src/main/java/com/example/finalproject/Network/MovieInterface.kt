package com.example.finalproject.Network

import android.text.method.MovementMethod
import com.example.finalproject.Data.Movie
import com.example.finalproject.Data.MoviePayload
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// interface to denote the different api calls made
interface MovieInterface {

    // for default loading in (home view), get by popular
    @GET("trending/movie/{time_window}")
    suspend fun getByTrending(@Path("time_window") time : String,
                              @Query("api_key") key : String): Response<MoviePayload>

    @GET("search/movie")
    suspend fun getBySearch(@Query("api_key") key : String, @Query("query") search : String) : Response<MoviePayload>

    @GET("movie/{movie_id}")
    suspend fun getByID(@Path("movie_id") movie : String,
                        @Query("api_key") key : String) : Response<Movie>

    @GET("discover/movie")
    suspend fun getByDiscover(@Query("api_key") args: String) : Response<MoviePayload>

}