package com.example.finalproject.Data

data class Movie (
    val title: String,
    val id : Int,
    val release_date : String,
    //I commented runtime out for now because it does not show up when looking up a movie by language or country.
    //val runtime: Int,
    val overview: String,
    val poster_path: String)

data class MoviePayload(
    val page : Int,
    val total_results: Int,
    val results: List<Movie>)