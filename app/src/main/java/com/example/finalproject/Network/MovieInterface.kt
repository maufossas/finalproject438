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
    @GET("trending/movie/{time_window}?api_key={api_key}")
    suspend fun getByTrending(@Path("time_window") time : String,
                              @Path("api_key") key : String): Response<MoviePayload>

    @GET("search/movie?api_key={api_key}&query={search}")
    suspend fun getBySearch(@Path("search") search : String, @Path("api_key") key : String) : Response<MoviePayload>

    @GET("movie/{movie_id}?api_key={api_key}")
    suspend fun getByID(@Path("movie_id") movie : String,
                        @Path("api_key") key : String) : Response<Movie>

    @GET("discover/movie?api_key={api_key}{args}")
    suspend fun getByDiscover(@Path("args") args: String,
                              @Path("api_key") key : String) : Response<MoviePayload>


}