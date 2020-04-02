package com.example.finalproject.Data

data class Movie (val title: String, val id : Int, val release_date : String, val runtime: Int, val overview: String)

data class MoviePayload(val page : Int, val total_results: Int, val results: List<Movie>)