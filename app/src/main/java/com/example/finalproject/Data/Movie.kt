package com.example.finalproject.Data

// data objects for API access
data class Movie (
    val title: String,
    val id : Int,
    val release_date : String,
    val overview: String,
    val poster_path: String)

data class MoviePayload(
    val page : Int,
    val total_results: Int,
    val results: List<Movie>)