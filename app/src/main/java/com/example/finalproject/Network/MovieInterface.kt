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
    @GET("trending/movie/{time_window}?api_key={api_key}")
    suspend fun getByTrending(@Path("time_window") time : String,
                              @Path("api_key") key : String): Response<List<Movie>>

    @GET("search/movie?api_key={api_key}&query={search}")
    suspend fun getBySearch(@Path("search") search : String, @Path("api_key") key : String) : Response<List<Movie>>

    @GET("movie/{movie_id}?api_key={api_key}")
    suspend fun getByID(@Path("movie_id") movie : String,
                        @Path("api_key") key : String) : Response<Movie>

    @GET("discover/movie?api_key={api_key}&language={lang}&vote_average.gte={rating}&region={region}")
    suspend fun getByDiscover(@Path("lang") language : String,
                              @Path("rating") rating : String,
                              @Path("region") region : String,
                              @Path("api_key") key : String) : Response<List<Movie>>


}